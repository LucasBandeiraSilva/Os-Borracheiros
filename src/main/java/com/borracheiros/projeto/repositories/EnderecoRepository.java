package com.borracheiros.projeto.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.client.endereco.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
    Optional<Endereco> findByClienteIdAndEnderecoPadraoTrue(Long clienteId);}
