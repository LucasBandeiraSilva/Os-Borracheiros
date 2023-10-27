package com.borracheiros.projeto.client;

import java.util.List;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.client.endereco.Endereco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private String cpf;
    private java.sql.Date dataAniversario;
    private String genero;
    private String senha;
    private String nome;
    @OneToMany(mappedBy = "cliente")
    private List<Endereco> enderecos;
    @OneToMany(mappedBy = "cliente")
    private List<Carrinho> carrinho;

}
