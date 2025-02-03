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
import ru.t1.java.demo.kafka.AccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDTO;
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

    @Autowired
    private final AccountProducer<AccountDTO> accountProducer;

    @Override
    @Transactional
    @Track
    @HandlingResult
    public List<Account> getAllAccounts() {
        return accountRepository.findAllActive();
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id).filter(account -> !account.getIsDeleted());
    }

    @Override
    @Track
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    @Track
    public Account findById(String id) {
        return accountRepository.findByAccountId(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Override
    @Transactional
    public Optional<Account> findByAccountId(String id) {
        return accountRepository.findByAccountId(id);
    }

    @Override
    @Track
    public void sendTosave(Account account) {
        AccountDTO dto = new AccountDTO(
                account.getClient().getClientId(),
                account.getAccountType(),
                account.getBalance(),
                account.getIsDeleted()
        );
        accountProducer.send(dto);
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 1, maxDelay = 100, random = true))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Track
    @HandlingResult
    public Account updateAccount(String id, Account updatedAccount) {
        return accountRepository.findByAccountId(id).filter(account -> !account.getIsDeleted()).map(account -> {
            account.setAccountType(updatedAccount.getAccountType());
            account.setBalance(updatedAccount.getBalance());
            return accountRepository.save(account);
        }).orElseThrow(() -> new IllegalArgumentException("Account not found or deleted"));
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public void deleteAccount(Long id) {
        accountRepository.markAsDeleted(id);
    }
}
