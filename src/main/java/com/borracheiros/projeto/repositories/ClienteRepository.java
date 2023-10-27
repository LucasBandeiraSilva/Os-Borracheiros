package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.client.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Cliente findByEmail(String nome);
    Boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Cliente findByCpf(String cpf);
    
}
