package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.TransactionProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
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
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    @Track
    public void sendToSave(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(transaction.getAccount().getId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setIsDeleted(transaction.getIsDeleted());

        transactionProducer.send(dto);
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
