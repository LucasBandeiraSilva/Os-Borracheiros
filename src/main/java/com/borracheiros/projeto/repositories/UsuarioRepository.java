package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.borracheiros.projeto.users.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Usuario findByEmail(String nome);
    Boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Usuario findByCpf(String cpf);
    
}
