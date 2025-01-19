package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Optional<Transaction> getTransactionById(Long id);

    Transaction createTransaction(Transaction transaction);

    Optional<Transaction> updateTransaction(Long id, Transaction transactionDetails);

    void deleteTransaction(Long id);
}
