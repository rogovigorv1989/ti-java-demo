package ru.t1.java.demo.exception;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.Transaction;


@Getter
@Setter
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    @Column(name = "exception_stack_trace", columnDefinition = "TEXT", nullable = false)
    private String exceptionStackTrace;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;
}
