package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<Account> getAllAccounts();

    Optional<Account> getAccountById(Long id);

    void sendTosave(Account account);

    Account updateAccount(Long id, Account updatedAccount);

    void deleteAccount(Long id);

    Account save(Account account);

     Account findById(Long id);

}
