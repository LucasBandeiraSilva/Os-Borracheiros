package com.borracheiros.projeto.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.client.Cliente;

public interface PedidoRealizadoRepository extends JpaRepository<PedidoRealizado,Long> {

    List<PedidoRealizado> findByCliente(Cliente cliente);
    
}
