package ru.t1.java.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping("/{clientId}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public void createAccount(@PathVariable String clientId, @RequestBody Account account) {
        accountService.createAccount(clientId, account);
    }

    @PutMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 1L)
    public Account updateAccount(@PathVariable String id, @RequestBody Account updatedAccount) {
        return accountService.updateAccount(id, updatedAccount);
    }

    @DeleteMapping("/{id}")
    @LogDataSourceError
    @Metric(threshold = 1L)
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}
