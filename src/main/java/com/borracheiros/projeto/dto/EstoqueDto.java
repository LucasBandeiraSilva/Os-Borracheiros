package com.borracheiros.projeto.dto;

import java.math.BigDecimal;

import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class EstoqueDto {
    private String nome;
    private BigDecimal preco;

    private Integer quantidadeEstoque;
    private double avaliacao;
    private String descricaoDetalhada;
    private String descricao;
    @Lob
    private byte[] imagem;

    public Estoque toEstoque() {
        Estoque estoque = new Estoque();

        estoque.setNome(this.nome);
        estoque.setPreco(this.preco);
        estoque.setAvaliacao(this.avaliacao);
        estoque.setDescricaoDetalhada(this.descricaoDetalhada);
        estoque.setDescricao(this.descricao);
        estoque.setImagem(this.imagem);
        estoque.setQuantidadeEstoque(this.quantidadeEstoque);
        return estoque;
    }
}
