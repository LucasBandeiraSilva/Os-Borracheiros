package com.borracheiros.projeto.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.client.Cliente;

public interface CarrinhoRepository extends JpaRepository<Carrinho,Long> {
    Carrinho findByNome(String nome);
        List<Carrinho> findByCliente(Cliente cliente);
        List<Carrinho> findAllByClienteId(Long clienteId);


}
