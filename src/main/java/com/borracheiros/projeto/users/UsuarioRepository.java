package com.borracheiros.projeto.users;

import org.springframework.data.jpa.repository.JpaRepository;
import com.borracheiros.projeto.users.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Usuario findByEmail(String nome);
}
