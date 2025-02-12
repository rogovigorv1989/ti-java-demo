package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDTO;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    void init() {
        try {
            List<Client> clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
//        repository.saveAll(clients);
    }

    @Override
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDTO[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDTO[].class);

        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public List<Client> getAllClients() {
        return clientRepository.findAllActive();
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id).filter(client -> !client.isDeleted());
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    @Retryable(backoff = @Backoff(delay = 1, maxDelay = 100, random = true))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Track
    @HandlingResult
    public Client updateClient(Long id, Client updatedClient) {
        return clientRepository.findById(id).filter(client -> !client.isDeleted()).map(client -> {
            client.setFirstName(updatedClient.getFirstName());
            client.setLastName(updatedClient.getLastName());
            client.setMiddleName(updatedClient.getMiddleName());
            return clientRepository.save(client);
        }).orElseThrow(() -> new IllegalArgumentException("Client not found or deleted"));
    }

    @Override
    @Transactional
    @Track
    @HandlingResult
    public void deleteClient(Long id) {
        clientRepository.markAsDeleted(id);
    }

    @Override
    @Transactional
    @Track
    public Client findById(String id) {
        return clientRepository.findByClientId(id).orElseThrow(() -> new IllegalArgumentException("Client not found"));
    }
}
