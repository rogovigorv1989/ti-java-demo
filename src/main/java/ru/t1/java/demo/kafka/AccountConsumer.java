package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDTO;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@Slf4j
@Component
public class AccountConsumer implements AccountMapper {

    private final AccountService accountService;
    private final ClientService clientService;

    @Autowired
    public AccountConsumer(AccountService accountService, ClientService clientService) {
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = {"${t1.kafka.topic.t1_demo_accounts}"},
            containerFactory = "kafkaAccountListenerContainerFactory")
    public void listener(@Payload List<AccountDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка новых сообщений");


        try {
            log.error("Topic: " + topic);
            log.error("Key: " + key);
            messageList.stream()
                    .forEach(System.err::println);
            List<Account> accounts = messageList.stream()
                    .map(dto -> {
                        dto.setClient(dto.getClient());
                        dto.setAccountType(dto.getAccountType());
                        dto.setBalance(dto.getBalance());
                        dto.setIsDeleted(dto.getIsDeleted());
                        return toEntity(dto);
                    })
                    .toList();
            accounts.forEach(accountService::save);
        } finally {
            ack.acknowledge();
        }


        log.debug("Account consumer: записи обработаны");
    }

    @Override
    public Account toEntity(AccountDTO accountDTO) {
        Account account = new Account();
        account.setClient(clientService.findById(accountDTO.getClient()));
        account.setAccountType(accountDTO.getAccountType());
        account.setBalance(accountDTO.getBalance());
        account.setIsDeleted(accountDTO.getIsDeleted());
        return account;
    }
}
