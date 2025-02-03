package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> parseJson() throws IOException;

    List<Client> getAllClients();

    Optional<Client> getClientById(Long id);

    Client saveClient(Client client);

    Client updateClient(Long id, Client updatedClient);

    void deleteClient(Long id);

    Client findById(String id);
}
