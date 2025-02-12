package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false")
    List<Transaction> findAllActive();

    @Modifying
    @Query("UPDATE Transaction t SET t.isDeleted = true WHERE t.id = :id")
    void markAsDeleted(Long id);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId AND t.createdAt >= :startTime")
    List<Transaction> findRecentTransactions(@Param("accountId") String accountId,
                                             @Param("startTime") LocalDateTime startTime);

    @Query("FROM Transaction t WHERE t.transactionId = :transactionId")
    Optional<Transaction> findByTransactionId(@Param("transactionId") String transactionId);
}
