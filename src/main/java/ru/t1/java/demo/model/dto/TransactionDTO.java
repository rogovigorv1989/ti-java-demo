package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class TransactionDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("transaction_amount")
    private Double transactionAmount;

    @JsonProperty("transaction_time")
    private LocalDateTime transactionTime;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;
}
