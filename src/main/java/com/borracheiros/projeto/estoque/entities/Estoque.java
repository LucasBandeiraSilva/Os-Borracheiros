package com.borracheiros.projeto.estoque.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(precision = 8, scale = 2)
    private BigDecimal preco;

    @Column
    private Integer quantidadeEstoque;

    @Column
    private double avaliacao;

    @Column(length = 2000)
    private String descricaoDetalhada;
    
    @Column(length = 200)
    private String descricao;

    @Lob
    @Column(length = 5242880) 
    private byte[] imagem;

}
