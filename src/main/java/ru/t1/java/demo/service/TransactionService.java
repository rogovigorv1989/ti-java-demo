package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Optional<Transaction> getTransactionById(Long id);

    List<Transaction> saveAll(List<Transaction> transactions);

    void sendAndSave(Transaction transaction);

    Transaction updateTransaction(Long id, Transaction updatedTransaction);

    void deleteTransaction(Long id);

}
