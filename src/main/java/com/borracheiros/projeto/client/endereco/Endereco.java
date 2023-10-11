package com.borracheiros.projeto.client.endereco;

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.persistence.CascadeType;
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
    private int cep;
    private String logradouro;
    private int complemento;
    private String Bairro;
    private int numero;
    private String cidade;
    private String endereco;
    private boolean statusEndereco;
    private String enderecoPadrao;

    @ManyToOne()
    @JoinColumn(name = "usuario_id")
    private Cliente cliente;
}
