package com.borracheiros.projeto.carrinho;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

@Getter
@Setter
@NoArgsConstructor

@Entity(name = "Pedido_realizado")
public class PedidoRealizado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer quantidade;

    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.EAGER)
    private Cliente cliente;

    private BigDecimal frete;

    @ManyToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    private Long codigoPedido;

    private String tipoPagamento;

    private int qtdeParcelas;

    private String StatusPagamento;

    private BigDecimal valorTotal;

    private java.sql.Date dataPedido;

    @ManyToOne
    @JoinColumn(name = "carrinho_id")
    private Carrinho carrinho;

    @OneToMany(mappedBy = "pedidoRealizado")
    private List<Endereco> enderecos;

}
