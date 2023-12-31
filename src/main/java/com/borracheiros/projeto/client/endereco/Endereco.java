package com.borracheiros.projeto.client.endereco;

import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.client.Cliente;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Endereços")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cep;
    private String logradouro;
    private String complemento;
    private String Bairro;
    private int numero;
    private String cidade;
    private String endereco;
    private String enderecoFaturamento;
    private boolean statusEndereco;
    private boolean enderecoPadrao;

    @ManyToOne()
    @JoinColumn(name = "usuario_id")
    private Cliente cliente;
    @ManyToOne
    @JoinColumn(name = "pedidoRealizado_id")
    private PedidoRealizado pedidoRealizado;

}
