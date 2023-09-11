package com.borracheiros.projeto.dto;

import java.math.BigDecimal;

import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueDto {
    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;
    @NotNull(message = "Você Precisa definir os preços do produto")
    private BigDecimal preco;

    private Integer quantidadeEstoque;
    @NotNull(message = "você precisa escolher uma avaliação")
    private double avaliacao;
    @NotBlank(message = "Você precisa fornecer uma descrição longa")
    private String descricaoDetalhada;
    @NotBlank(message = "Você precisa fornecer uma descrição longa")
    private String descricao;
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
