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

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("transaction_amount")
    private Double transactionAmount;

    @JsonProperty("transaction_time")
    private LocalDateTime transactionTime;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("created_At")
    private LocalDateTime createdAt;

    @JsonProperty("account_balance")
    private Double accountBalance;
}
