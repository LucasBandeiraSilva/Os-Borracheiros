package com.borracheiros.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private CarrinhoRepository carrinhoRepository;

    public String adicionaProdutoCarrinho(Long id, HttpSession session) {

        Optional<Estoque> estoqueOptional = this.estoqueRepository.findById(id);
    
        if (estoqueOptional.isPresent()) {
            //session.setAttribute("carrinhoNaoAutenticado", carrinhoNaoAutenticado);
            Estoque estoque = estoqueOptional.get();
            String nomeProduto = estoque.getNome();
    
            // Verifique se o produto já está no carrinho
            Carrinho carrinho = carrinhoRepository.findByNome(nomeProduto);
    
            if (carrinho == null) {
                // Se não estiver no carrinho, crie um novo Carrinho
                carrinho = new Carrinho();
                carrinho.setPreco(estoque.getPreco());
                carrinho.setNome(nomeProduto);
                carrinho.getEstoques().add(estoque);
                carrinho.setQuantidade(1);
            } else {
                // Se estiver no carrinho, atualize a quantidade e o preço
                int novaQuantidade = carrinho.getQuantidade() + 1;
                carrinho.setQuantidade(novaQuantidade);
                carrinho.setPreco(carrinho.getPreco().add(estoque.getPreco()));
            }
            session.setAttribute("carrinhoNaoAutenticadoID", carrinho.getId());
            carrinhoRepository.save(carrinho);
            return "Produto adicionado ao carrinho";
        }
    
        return null;
    }
    
}