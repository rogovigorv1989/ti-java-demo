package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDTO;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionConsumer {

    @Autowired
    private final TransactionService transactionService;

    @Qualifier("transactionMapperImpl")
    @Autowired
    private final TransactionMapper mapper;


    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = {"${t1.kafka.topic.t1_demo_transactions}"},
            containerFactory = "kafkaTransactionListenerContainerFactory")
    public void transactionListener(@Payload List<TransactionDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream().forEach(System.err::println);
            List<Transaction> transactions = messageList.stream()
                    .map(dto -> {
                        dto.setAccountId(dto.getAccountId());
                        dto.setTransactionAmount(dto.getTransactionAmount());
                        dto.setTransactionTime(dto.getTransactionTime());
                        dto.setIsDeleted(dto.getIsDeleted());
                        dto.setClientId(dto.getClientId());
                        dto.setTransactionId(dto.getTransactionId());
                        dto.setCreatedAt(dto.getCreatedAt());
                        dto.setAccountBalance(dto.getAccountBalance());
                        dto.setStatus(dto.getStatus());
                        return mapper.toEntity(dto);
                    }).toList();
            transactionService.requestAndSave(transactions);
            log.debug("Transaction consumer: записи обработаны");
        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = {"${t1.kafka.topic.t1_demo_transaction_result}"},
            containerFactory = "kafkaTransactionListenerContainerFactory")
    public void transactionResultListener(@Payload List<TransactionDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream().forEach(System.err::println);
            List<Transaction> transactions = messageList.stream()
                    .map(dto -> {
                        dto.setAccountId(dto.getAccountId());
                        dto.setTransactionAmount(dto.getTransactionAmount());
                        dto.setTransactionTime(dto.getTransactionTime());
                        dto.setIsDeleted(dto.getIsDeleted());
                        dto.setClientId(dto.getClientId());
                        dto.setTransactionId(dto.getTransactionId());
                        dto.setCreatedAt(dto.getCreatedAt());
                        dto.setAccountBalance(dto.getAccountBalance());
                        dto.setStatus(dto.getStatus());
                        return mapper.toEntity(dto);
                    }).toList();
            transactionService.processTransactionResult(transactions);
            log.debug("Transaction consumer: записи обработаны");
        } finally {
            ack.acknowledge();
        }
    }
}
