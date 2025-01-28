package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogExecution;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@Slf4j
@RequiredArgsConstructor
class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @PutMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction) {
        return transactionService.updateTransaction(id, updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @LogDataSourceError
    @Metric(threshold = 10)
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
