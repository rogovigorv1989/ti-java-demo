package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "transaction_amount", nullable = false)
    private Double transactionAmount;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;
}

