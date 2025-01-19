package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Override
    List<Account> findAll();

    @Override
    Optional<Account> findById(Long id);

    @Override
    Account save(Account account);

    @Override
    void delete(Account account);
}
