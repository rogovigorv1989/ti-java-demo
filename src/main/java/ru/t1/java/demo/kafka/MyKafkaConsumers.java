package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

@Component
@Slf4j
public class MyKafkaConsumers {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

//    @KafkaListener(topics = "t1_demo_accounts", groupId = "demo_group")
//    public void consumeAccount(String message) {
//        // Преобразование сообщения в объект Account (предполагается JSON)
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Account account = objectMapper.readValue(message, Account.class);
//            accountRepository.save(account);
//        } catch (JsonProcessingException e) {
//           log.error(e.getMessage());
//        }
//    }
//
//    @KafkaListener(topics = "t1_demo_transactions", groupId = "demo_group")
//    public void consumeTransaction(String message) {
//        // Преобразование сообщения в объект Transaction (предполагается JSON)
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Transaction transaction = objectMapper.readValue(message, Transaction.class);
//            transactionRepository.save(transaction);
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//        }
//    }
}
