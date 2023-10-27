package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.carrinho.Carrinho;

public interface CarrinhoRepository extends JpaRepository<Carrinho,Long> {
    Carrinho findByNome(String nome);
}
