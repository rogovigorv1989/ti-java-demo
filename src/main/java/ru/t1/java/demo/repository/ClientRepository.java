package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.t1.java.demo.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Override
    Optional<Client> findById(Long aLong);

    @Query("SELECT c FROM Client c WHERE c.isDeleted = false")
    List<Client> findAllActive();

    @Modifying
    @Query("UPDATE Client c SET c.isDeleted = true WHERE c.id = :id")
    void markAsDeleted(Long id);
}