package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.Account;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AccountDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("client_id")
    private Long client;

    @JsonProperty("account_type")
    private Account.AccountType accountType;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;
}
