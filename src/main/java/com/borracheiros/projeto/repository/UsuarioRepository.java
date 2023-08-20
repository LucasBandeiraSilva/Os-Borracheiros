package com.borracheiros.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
}
