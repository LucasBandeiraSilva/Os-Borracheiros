package com.borracheiros.projeto.dto;

import java.math.BigDecimal;
import java.util.List;

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

    private List<byte[]> imagem; 

    public Estoque toEstoque() {
        Estoque estoque = new Estoque();

        estoque.setNome(this.nome);
        estoque.setPreco(this.preco);
        estoque.setAvaliacao(this.avaliacao);
        estoque.setDescricaoDetalhada(this.descricaoDetalhada);
        estoque.setDescricao(this.descricao);
        if (this.imagem != null && !this.imagem.isEmpty()) {
            estoque.setImagem(this.imagem.get(0));
        }
        estoque.setQuantidadeEstoque(this.quantidadeEstoque);
        return estoque;
    }
}
