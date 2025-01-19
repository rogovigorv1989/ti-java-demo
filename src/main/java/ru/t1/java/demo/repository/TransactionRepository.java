package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Override
    List<Transaction> findAll();

    @Override
    Optional<Transaction> findById(Long id);

    @Override
    Transaction save(Transaction transaction);

    @Override
    void delete(Transaction transaction);
}
