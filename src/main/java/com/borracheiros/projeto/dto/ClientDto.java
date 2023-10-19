package com.borracheiros.projeto.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.borracheiros.projeto.client.Cliente;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ClientDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String email;
    @NotBlank
    // @CPF(message = "CPF inválido")
    private String cpf;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataAniversario;
    private String genero;
    private String senha;
    private String nome;

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setEmail(this.email);
        cliente.setCpf(this.cpf);
        cliente.setDataAniversario(this.dataAniversario);
        cliente.setGenero(this.genero);
        cliente.setSenha(this.senha);
        cliente.setNome(this.nome);
        return cliente;
    }
}
