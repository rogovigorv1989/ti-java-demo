package ru.t1.java.demo.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.service.AccountService;

@Component
@RequiredArgsConstructor
public class TransactionMapperImpl implements TransactionMapper {
    @Autowired
    private final AccountService accountService;

    @Override
    public Transaction toEntity(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionDTO.getTransactionId());
        transaction.setAccount(accountService.findById(transactionDTO.getAccountId()));
        transaction.setTransactionAmount(transactionDTO.getTransactionAmount());
        transaction.setTransactionTime(transactionDTO.getTransactionTime());
        transaction.setCreatedAt(transactionDTO.getCreatedAt());
        transaction.setStatus(transactionDTO.getStatus());
        transaction.setIsDeleted(transactionDTO.getIsDeleted());

        return transaction;
    }

    @Override
    public TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(transaction.getAccount().getAccountId());
        dto.setTransactionAmount(transaction.getTransactionAmount());
        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setIsDeleted(transaction.getIsDeleted());
        dto.setClientId(transaction.getAccount().getClient().getClientId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setAccountBalance(transaction.getAccount().getBalance());
        dto.setStatus(transaction.getStatus());

        return dto;
    }

    @Override
    public Transaction partialUpdate(TransactionDTO transactionDTO, Transaction transaction) {
        return null;
    }
}
