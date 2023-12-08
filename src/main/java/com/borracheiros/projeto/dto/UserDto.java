package com.borracheiros.projeto.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class UserDto {
    private Long id;
    @NotBlank(message = "não pode ser vazio")
    private String nome;
    @NotBlank
    private String senha;
    
    private Boolean StatusUsuario;
    @NotBlank(message = "não pode ser vazio")
    @Email
    private String email;
    @NotBlank
     @CPF(message = "CPF inválido")
    private String cpf;
    private Long role;
    private String confirmPassword;

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setCpf(this.cpf);
        usuario.setEmail(this.email);
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
