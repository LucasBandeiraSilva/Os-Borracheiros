package com.borracheiros.projeto.estoque;

import org.springframework.data.jpa.repository.JpaRepository;
import com.borracheiros.projeto.estoque.entities.Estoque;


public interface EstoqueRepository extends JpaRepository<Estoque,Long>{
    
}
