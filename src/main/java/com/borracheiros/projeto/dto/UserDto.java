package com.borracheiros.projeto.dto;

import java.time.LocalDate;

import com.borracheiros.projeto.users.entities.Usuario;

import lombok.Data;

@Data

public class UserDto {
    private String nome;
    private LocalDate dataNascimento;
    private String senha;
    private String StatusUsuario;
    private Long telefone;
    private String email;
    private String cpf;
    private Long role;

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setDataNascimento(this.dataNascimento);
        usuario.setCpf(this.cpf);
        usuario.setEmail(this.email);
        usuario.setTelefone(telefone);
        usuario.setStatusUsuario(this.StatusUsuario);
        usuario.setSenha(this.senha);

        return usuario;
    }
}
