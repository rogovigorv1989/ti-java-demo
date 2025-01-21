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


@Slf4j
@RequiredArgsConstructor
@Service
class AccountServiceImpl implements AccountService {
    @Autowired
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public List<Account> getAllAccounts() {
        return accountRepository.findAllActive();
    }

    @Override
    @Transactional
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id).filter(account -> !account.getIsDeleted());
    }

    @Override
    @Transactional
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 1, maxDelay = 100, random = true))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account updateAccount(Long id, Account updatedAccount) {
        return accountRepository.findById(id).filter(account -> !account.getIsDeleted()).map(account -> {
            account.setAccountType(updatedAccount.getAccountType());
            account.setBalance(updatedAccount.getBalance());
            return accountRepository.save(account);
        }).orElseThrow(() -> new IllegalArgumentException("Account not found or deleted"));
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.markAsDeleted(id);
    }
}
