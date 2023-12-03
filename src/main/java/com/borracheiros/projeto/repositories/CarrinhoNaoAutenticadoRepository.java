package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.carrinho.CarrinhoNaoAutenticado;

public interface CarrinhoNaoAutenticadoRepository extends JpaRepository<CarrinhoNaoAutenticado,Long>{
    
}
