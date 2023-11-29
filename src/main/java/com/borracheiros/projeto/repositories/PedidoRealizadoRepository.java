package com.borracheiros.projeto.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.client.Cliente;

public interface PedidoRealizadoRepository extends JpaRepository<PedidoRealizado, Long> {

    List<PedidoRealizado> findByCliente(Cliente cliente);

    List<PedidoRealizado> findByClienteId(Long id);

    List<PedidoRealizado> findDistinctByClienteIdAndCodigoPedidoIsNotNull(Long clienteId);

    List<PedidoRealizado> findByClienteIdAndCodigoPedido(Long clienteId, Long codigoPedido);

    List<PedidoRealizado> findByClienteIdAndCodigoPedidoIn(Long clienteId, List<Long> codigosPedidos);

    List<PedidoRealizado> findByCodigoPedido(Long codigoPedido);

}
