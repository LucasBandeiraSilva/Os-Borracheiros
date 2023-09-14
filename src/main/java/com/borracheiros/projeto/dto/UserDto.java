package com.borracheiros.projeto.dto;

import java.time.LocalDate;

import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class UserDto {
    @NotBlank
    private String nome;
    @NotBlank
    private String senha;
    
    private Boolean StatusUsuario;
    @NotNull
    private LocalDate dataNascimento;

    private String telefone;
    @NotBlank
    private String email;
    @NotBlank
    private String cpf;
    private Long role;
    private String confirmPassword;

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setCpf(this.cpf);
        usuario.setDataNascimento(this.dataNascimento);
        usuario.setEmail(this.email);
        usuario.setTelefone(telefone);
        usuario.setStatusUsuario(this.StatusUsuario);
        usuario.setSenha(this.senha);
        return usuario;
    }

    public void fromUsuario(Usuario usuario){
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.email = usuario.getEmail();
        this.StatusUsuario = usuario.getStatusUsuario();
        
    }
}
