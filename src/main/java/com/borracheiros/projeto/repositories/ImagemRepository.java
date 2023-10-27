package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.estoque.entities.ImagemProduto;

public interface ImagemRepository extends JpaRepository<ImagemProduto,Long> {
    
}
