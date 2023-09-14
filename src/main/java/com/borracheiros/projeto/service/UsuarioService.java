package com.borracheiros.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borracheiros.projeto.users.UsuarioRepository;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    @Autowired    
    private UsuarioRepository usuarioRepository;

    public void status(long id, boolean StatusUsuario){
        
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setStatusUsuario(StatusUsuario);
            usuarioRepository.save(usuario);
        } else {
            throw new EntityNotFoundException("Impossivel mudar o status do usuario");
        }

    }
    
}
