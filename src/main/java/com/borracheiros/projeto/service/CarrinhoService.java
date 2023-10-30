package com.borracheiros.projeto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private CarrinhoRepository carrinhoRepository;

    public ModelAndView adicionaProdutoCarrinho(Long id, HttpSession session) {

        ModelAndView mv = new ModelAndView();
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
            
            List<Estoque> produtos = estoqueRepository.findAll();
            mv.addObject("produtos", produtos);
           // mv.addObject("qtde", Cliente);
            mv.setViewName("clientes/catalogo");
            return mv;

        }
    
        return null;
    }
    public ModelAndView verCarrinho(@PathVariable Long id){
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id); 
        if(clienteOptional.isPresent()){
            Cliente cliente = clienteOptional.get();
            List<Carrinho> pedidos = cliente.getCarrinho();
            mv.addObject("cliente", cliente);
            mv.addObject("pedidos", pedidos);
            System.out.println("cliente id: " + cliente.getId());
            for (Carrinho carrinho : pedidos) {
                System.out.println("carrinho id" + carrinho.getId());
            }
            mv.setViewName("clientes/EnderecoExcluido"); 
            /*
            tela de endereço excluido somente para testes em breve será substituida por uma tela mais adequada
            */ 
            return mv;
        }else{
            mv.setViewName("Erro");
            return mv;
        }
    }
}