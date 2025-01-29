package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogExecution;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@Slf4j
@RequiredArgsConstructor
class TransactionController {
    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final AccountService accountService;

    @GetMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/{accountId}")
//    @LogDataSourceError
//    @LogExecution
//    @Metric(threshold = 1L)
    public void createTransaction(@PathVariable Long accountId, @RequestBody Transaction transaction) {
        Account account = accountService.findById(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found");
        }
        transaction.setAccount(account);
        transactionService.sendToSave(transaction);
    }

    @PutMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction) {
        return transactionService.updateTransaction(id, updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @LogDataSourceError
    @Metric(threshold = 1L)
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
