package com.borracheiros.projeto.carrinho;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CarrinhoNaoAutenticado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @ManyToMany
@JoinTable(
    name = "carrinho_estoque_nao_autenticado",
    joinColumns = @JoinColumn(name = "carrinho_nao_autenticado_id"),
    inverseJoinColumns = @JoinColumn(name = "estoque_id")
)
private List<Estoque> estoques = new ArrayList<>();

    private Integer quantidade;

    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.EAGER)
    private Cliente cliente;

    private BigDecimal frete;

    private BigDecimal valorTotal;
}
