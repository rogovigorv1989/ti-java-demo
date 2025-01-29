package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionConsumer implements TransactionMapper {

    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final AccountService accountService;

    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = {"${t1.kafka.topic.t1_demo_transactions}"},
            containerFactory = "kafkaTransactionListenerContainerFactory")
    public void listener(@Payload List<TransactionDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream()
                    .forEach(System.err::println);
            List<Transaction> transactions = messageList.stream()
                    .map(dto -> {
                        dto.setAccountId(dto.getAccountId());
                        dto.setTransactionAmount(dto.getTransactionAmount());
                        dto.setTransactionTime(dto.getTransactionTime());
                        dto.setIsDeleted(dto.getIsDeleted());
                        return toEntity(dto);
                    })
                    .toList();
            transactions.forEach(transactionService::save);
        } finally {
            ack.acknowledge();
        }


        log.debug("Transaction consumer: записи обработаны");
    }

    @Override
    public Transaction toEntity(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAccount(accountService.findById(transactionDTO.getAccountId()));
        transaction.setTransactionAmount(transactionDTO.getTransactionAmount());
        transaction.setTransactionTime(transactionDTO.getTransactionTime());
        transaction.setIsDeleted(transactionDTO.getIsDeleted());

        return transaction;
    }

    @Override
    public TransactionDTO toDto(Transaction client) {
        return null;
    }

    @Override
    public Transaction partialUpdate(TransactionDTO transactionDTO, Transaction transaction) {
        return null;
    }
}
