package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.client.endereco.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
    
}
