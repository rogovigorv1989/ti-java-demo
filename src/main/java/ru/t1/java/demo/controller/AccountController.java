package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogExecution;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @GetMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public Optional<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Client client = clientService.findById(account.getClient().getId());
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found");
        }
        account.setClient(client);
        Account savedAccount = accountService.saveAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
    }

    @PutMapping("/{id}")
    @LogDataSourceError
    @LogExecution
    @Metric(threshold = 10)
    public Account updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        return accountService.updateAccount(id, updatedAccount);
    }

    @DeleteMapping("/{id}")
    @LogDataSourceError
    @Metric(threshold = 10)
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}
