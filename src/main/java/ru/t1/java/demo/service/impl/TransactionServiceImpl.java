package ru.t1.java.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.kafka.TransactionProducer;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.t1.java.demo.config.Сonstants.T;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Value("${t1.kafka.topic.t1_demo_transaction_accept}")
    private String acceptTransactionTopic;

    private final TransactionRepository transactionRepository;
    private final TransactionProducer<TransactionDTO> transactionProducer;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionProducer<TransactionDTO> transactionProducer,
                                  AccountRepository accountRepository,
                                  @Qualifier("transactionMapperImpl") TransactionMapper transactionMapper,
                                  AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.transactionProducer = transactionProducer;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
        this.accountService = accountService;
    }


    @Override
    @Transactional
    @Track
    @HandlingResult
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllActive();
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id).filter(transaction -> !transaction.getIsDeleted());
    }

    @Override
    @Track
    @HandlingResult
    public List<Transaction> requestAndSave(List<Transaction> transactions)  {
        List<Transaction> requestedTransactions = new ArrayList<>();

        transactions.forEach(transaction -> {
            if (transaction.getAccount().getStatus().equals(Account.Status.OPEN)) {
                transaction.setStatus(Transaction.Status.REQUESTED);
                requestedTransactions.add(transaction);
            } else {
                log.info("Transaction with id:{} rejected", transaction.getTransactionId());
                throw new TransactionException("Transaction cannot be accepted because account status is 'CLOSED'");
            }
        });

        try {
            List<Transaction> savedTransactions = transactionRepository.saveAll(requestedTransactions);
            savedTransactions.forEach(this::sendToAndSave);
            return savedTransactions;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processTransactionResult(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            Optional<Transaction> existingTransaction =
                    transactionRepository.findByTransactionId(transaction.getTransactionId());

            existingTransaction.ifPresent(t -> {
                Account account = t.getAccount();
                switch (transaction.getStatus()) {
                    case ACCEPTED:
                        t.setStatus(Transaction.Status.ACCEPTED);
                        account.setBalance(account.getBalance() + t.getTransactionAmount());
                        accountRepository.save(account);
                        break;
                    case BLOCKED:
                        List<Transaction> blockedTransactions = transactionRepository.findRecentTransactions(
                                account.getAccountId(), LocalDateTime.now().minusSeconds(T)
                        );
                        blockedTransactions.forEach(bt -> bt.setStatus(Transaction.Status.BLOCKED));
                        transactionRepository.saveAll(blockedTransactions);
                        account.setStatus(Account.Status.BLOCKED);
                        double blockedAmount = blockedTransactions.stream().mapToDouble(Transaction::getTransactionAmount).sum();
                        account.setFrozenAmount(account.getFrozenAmount() + blockedAmount);
                        accountRepository.save(account);
                        break;
                    case REJECTED:
                        t.setStatus(Transaction.Status.REJECTED);
                        break;
                }
                transactionRepository.save(t);
            });
        });
    }

    @Override
    public void createTransaction(String accountId, Transaction transaction) {
        Account account = accountService.findById(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account not found");
        }
        transaction.setAccount(account);
        sendAndSave(transaction);
    }

    @Override
    @Track
    public void sendAndSave(Transaction transaction) {
        TransactionDTO dto = transactionMapper.toDto(transaction);
        transactionProducer.send(dto);
    }

    public void sendToAndSave(Transaction transaction) {
        TransactionDTO dto = transactionMapper.toDto(transaction);
        transactionProducer.sendTo(acceptTransactionTopic, dto);
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 1, maxDelay = 100, random = true))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Track
    @HandlingResult
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        return transactionRepository.findById(id).filter(transaction ->
                !transaction.getIsDeleted()).map(transaction -> {
            transaction.setTransactionAmount(updatedTransaction.getTransactionAmount());
            transaction.setTransactionTime(updatedTransaction.getTransactionTime());
            return transactionRepository.save(transaction);
        }).orElseThrow(() -> new IllegalArgumentException("Transaction not found or deleted"));
    }

    @Override
    @Transactional
    @Track
    public void deleteTransaction(Long id) {
        transactionRepository.markAsDeleted(id);
    }
}
