package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();

    Account getAccountById(Long id);

    void sendTosave(Account account);

    Account updateAccount(String id, Account updatedAccount) throws IllegalArgumentException;

    void deleteAccount(Long id);

    Account save(Account account);

    Account findById(String id);

    void createAccount(String clientId, Account account);
}
