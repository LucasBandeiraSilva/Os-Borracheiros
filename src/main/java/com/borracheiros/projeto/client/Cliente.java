package com.borracheiros.projeto.client;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data

@Entity
@Table(name = "Clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    // @NotBlank(message = "O CPF não pode estar em branco")
    // @CPF(message = "CPF inválido")
    private String cpf;
    private Date dataAniversario;
    private String genero;
    private String senha;
    private String nome;
   
}
