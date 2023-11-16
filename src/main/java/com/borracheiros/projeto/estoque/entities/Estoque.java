package com.borracheiros.projeto.estoque.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.borracheiros.projeto.carrinho.Carrinho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column
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

   @ManyToMany(mappedBy = "estoques")
    private List<Carrinho> carrinhos = new ArrayList<>();

    @Override
public String toString() {
    return "Estoque{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", preco=" + preco +
            ", quantidadeEstoque=" + quantidadeEstoque +
            // Adicione outros campos conforme necessário
            '}';
}

}
