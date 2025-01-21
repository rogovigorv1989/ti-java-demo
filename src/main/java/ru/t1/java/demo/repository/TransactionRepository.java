package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false")
    List<Transaction> findAllActive();

    @Modifying
    @Query("UPDATE Transaction t SET t.isDeleted = true WHERE t.id = :id")
    void markAsDeleted(Long id);
}
