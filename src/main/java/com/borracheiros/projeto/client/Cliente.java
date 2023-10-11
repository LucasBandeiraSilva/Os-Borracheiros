package com.borracheiros.projeto.client;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import com.borracheiros.projeto.client.endereco.Endereco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
