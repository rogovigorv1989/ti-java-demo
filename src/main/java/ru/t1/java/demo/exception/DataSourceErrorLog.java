package ru.t1.java.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exception_trace", nullable = false, columnDefinition = "TEXT")
    private String exceptionTrace;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "method_signature", nullable = false, length = 255)
    private String methodSignature;

}

