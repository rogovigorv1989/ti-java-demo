package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findByAccountId(@Param("accountId") String accountId);

    @Query("SELECT a FROM Account a WHERE a.isDeleted = false")
    List<Account> findAllActive();

    @Modifying
    @Query("UPDATE Account a SET a.isDeleted = true WHERE a.id = :id")
    void markAsDeleted(Long id);
}
