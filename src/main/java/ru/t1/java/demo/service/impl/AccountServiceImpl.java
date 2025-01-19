package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Autowired
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 1, maxDelay = 100, random = true))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<Account> updateAccount(Long id, Account accountDetails) {
        return Optional.ofNullable(accountRepository.save(accountDetails));
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
