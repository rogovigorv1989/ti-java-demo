package ru.t1.java.demo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.AccountProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDTO;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.util.List;


@Slf4j
@Service
class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountProducer<AccountDTO> accountProducer;
    private final ClientService clientService;

    @Autowired
    AccountServiceImpl(AccountRepository accountRepository,
                       AccountProducer<AccountDTO> accountProducer,
                       ClientService clientService) {
        this.accountRepository = accountRepository;
        this.accountProducer = accountProducer;
        this.clientService = clientService;
    }

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
    public Account getAccountById(Long id) throws RuntimeException {
        try {
            return accountRepository.findById(id).filter(account -> !account.getIsDeleted())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Track
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    @Track
    public Account findById(String id) throws RuntimeException {
        try {
            return accountRepository.findByAccountId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAccount(String clientId, Account account) {
        Client client = null;
        try {
            client = clientService.findById(clientId);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client not found");
        }
        account.setClient(client);
        sendTosave(account);
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
