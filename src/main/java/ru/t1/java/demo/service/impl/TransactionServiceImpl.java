package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.kafka.TransactionProducer;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService, TransactionMapper {
    @Value("${t1.kafka.topic.t1_demo_transaction_accept}")
    private String acceptTransactionTopic;

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final TransactionProducer<TransactionDTO> transactionProducer;

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
    @Transactional
    @Track
    @HandlingResult
    public List<Transaction> saveAll(List<Transaction> transactions)  {
        List<Transaction> acceptedTransactions = new ArrayList<>();

        transactions.forEach(transaction -> {
            if (transaction.getAccount().getStatus().equals(Account.Status.OPEN)) {
                transaction.setStatus(Transaction.Status.REQUESTED);
                acceptedTransactions.add(transaction);
            } else {
                log.info("Transaction with id:{} rejected", transaction.getTransactionId());
                throw new TransactionException("Transaction cannot be accepted because account status is 'CLOSED'");
            }
        });

        try {
            List<Transaction> savedTransactions = transactionRepository.saveAll(acceptedTransactions);
            savedTransactions.forEach(this::sendToAndSave);
            return savedTransactions;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Track
    public void sendAndSave(Transaction transaction) {
        TransactionDTO dto = toDto(transaction);
        transactionProducer.send(dto);
    }

    public void sendToAndSave(Transaction transaction) {
        TransactionDTO dto =toDto(transaction);
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

    @Override
    public Transaction toEntity(TransactionDTO transactionDTO) {
        return null;
    }

    @Override
    public TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(transaction.getAccount().getAccountId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setIsDeleted(transaction.getIsDeleted());
        dto.setClientId(transaction.getAccount().getClient().getClientId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setAccountBalance(transaction.getAccount().getBalance());
        return dto;
    }

    @Override
    public Transaction partialUpdate(TransactionDTO transactionDTO, Transaction transaction) {
        return null;
    }
}
