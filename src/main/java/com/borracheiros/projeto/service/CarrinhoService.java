package com.borracheiros.projeto.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;

    public ModelAndView adicionaProdutoCarrinho(Long id, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        Optional<Estoque> estoqueOptional = this.estoqueRepository.findById(id);

        if (estoqueOptional.isPresent()) {
            Estoque estoque = estoqueOptional.get();
            String nomeProduto = estoque.getNome();

            // Verifique se o cliente está logado
            Cliente cliente = (Cliente) session.getAttribute("cliente");

            if (cliente != null) {
                Carrinho carrinho = null;

                // Verifique se o cliente já tem um carrinho
                if (!cliente.getCarrinho().isEmpty()) {
                    carrinho = cliente.getCarrinho().get(0);
                }

                if (carrinho == null) {
                    // Se não estiver no carrinho, crie um novo Carrinho
                    carrinho = new Carrinho();
                    carrinho.setCliente(cliente);
                    carrinho.setPreco(estoque.getPreco());
                    carrinho.setNome(nomeProduto);
                    carrinho.getEstoques().add(estoque);
                    carrinho.setQuantidade(1);
                } else {
                    // Se estiver no carrinho, atualize a quantidade e adicione o preço original
                    int novaQuantidade = carrinho.getQuantidade() + 1;
                    carrinho.setQuantidade(novaQuantidade);
                    // Atualize o preço do carrinho somente uma vez
                    carrinho.setPreco(carrinho.getPreco().add(estoque.getPreco()));
                }

                session.setAttribute("carrinhoNaoAutenticadoID", carrinho.getId());
                carrinhoRepository.save(carrinho);

                List<Estoque> produtos = estoqueRepository.findAll();
                mv.addObject("produtos", produtos);
                if (session.getAttribute("cliente") != null) {
                    mv.addObject("cliente", session.getAttribute("cliente"));
                    mv.addObject("nomeUsuario", session.getAttribute("nomeUsuario"));
                    mv.addObject("produtos", produtos);
                    mv.setViewName("clientes/CatalogoClienteLogado");
                    return mv;
                }

                if (session.getAttribute("cliente") == null) {
                    mv.setViewName("clientes/SecaoInvalida");
                    return mv;
                }

                mv.setViewName("clientes/catalogo");
            }
        }
        return null;
    }

    public ModelAndView verCarrinho(Long id, HttpSession session) {
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        if (clienteSession != null) {
            System.out.println("eu to no carrinho");
        }
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            List<Carrinho> carrinhos = carrinhoRepository.findByCliente(cliente);

            ModelAndView mv = new ModelAndView();
            mv.addObject("cliente", cliente);

            mv.addObject("carrinhos", carrinhos);
            List<Estoque> produtosComImagem = new ArrayList<>(); // Lista para armazenar produtos com imagem

            for (Carrinho carrinho : carrinhos) {
                for (Estoque estoque : carrinho.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }
                    if (carrinho.getFrete() != null) {
                        mv.addObject("totalFrete", carrinho.getFrete());
                    }

                }
            }
            // Cálculo do total dos produtos
            BigDecimal total = BigDecimal.ZERO;
            for (Carrinho carrinho : carrinhos) {
                total = total.add(carrinho.getPreco());
                carrinho.setValorTotal(total);
                System.out.println("valor total: " + carrinho.getValorTotal());
            }

            // Adiciona o valor do frete ao totalFrete

            mv.addObject("totalItens", total); // Adiciona o total dos produtos para exibição na página

            mv.addObject("produtosComImagem", produtosComImagem); // Adiciona os produtos com imagem para exibição

            mv.setViewName("clientes/ResumoPedido");

            return mv;
        } else {
            return null;
        }

    }

    public ModelAndView adcionaUm(@PathVariable Long carrinhoId, HttpSession session) {
        System.out.println("entrei no metodo");
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(carrinhoId);

        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            System.out.println("adicionando +1");

            // Incrementa a quantidade em 1
            int novaQuantidade = carrinho.getQuantidade() + 1;
            carrinho.setQuantidade(novaQuantidade);

            // Atualiza o preço do carrinho somente uma vez
            BigDecimal precoProduto = carrinho.getEstoques().get(0).getPreco();

            BigDecimal novoPreco = precoProduto.multiply(BigDecimal.valueOf(carrinho.getQuantidade()));
            carrinho.setPreco(novoPreco);

            carrinhoRepository.save(carrinho);

            // Redireciona de volta para a página do resumo do pedido
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        } else {

            // Redireciona para uma página de erro ou outra página adequada
            return new ModelAndView("redirect:/pagina-de-erro");
        }
    }

    public ModelAndView removeUM(@PathVariable Long carrinhoId, HttpSession session) {
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(carrinhoId);

        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();

            int novaQuantidade = carrinho.getQuantidade() - 1;
            carrinho.setQuantidade(novaQuantidade);

            BigDecimal precoProduto = carrinho.getEstoques().get(0).getPreco();

            BigDecimal novoPreco = precoProduto.multiply(BigDecimal.valueOf(carrinho.getQuantidade()));
            carrinho.setPreco(novoPreco);

            carrinhoRepository.save(carrinho);
            if (novaQuantidade == 0) {
                removeCarrinho(carrinhoId, session);
            }

            // Redireciona de volta para a página do resumo do pedido
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        } else {

            // Redireciona para uma página de erro ou outra página adequada
            return new ModelAndView("redirect:/pagina-de-erro");
        }
    }

    public ModelAndView removeCarrinho(@PathVariable Long carrinhoId, HttpSession session) {
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(carrinhoId);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            carrinhoRepository.deleteById(carrinhoId);
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        } else {
            return new ModelAndView("Erro");
        }
    }

    public ModelAndView calcularFrete(@PathVariable Long id, @RequestParam("frete") String freteSelecionado) {
        BigDecimal frete = new BigDecimal(freteSelecionado);

        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(id);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();

            BigDecimal valorTotal = carrinho.getPreco();

            if (carrinho.getFrete() == null || !carrinho.getFrete().equals(frete)) {
                BigDecimal freteAntigo = carrinho.getFrete();

                if (freteAntigo != null) {
                    // Subtrai o frete antigo do valor total
                    valorTotal = valorTotal.subtract(freteAntigo);
                }

                // Adiciona o novo frete ao valor total
                valorTotal = valorTotal.add(frete);

                // Atualiza o frete no carrinho
                carrinho.setFrete(frete);

                // Atualiza o valor total no carrinho
                carrinho.setPreco(valorTotal);

                // Salva as alterações no carrinho
                this.carrinhoRepository.saveAndFlush(carrinho);
            }

            // Redireciona para a página do carrinho do cliente
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        }
        return new ModelAndView("erro");
    }

    public ModelAndView associarEnderecoAoCarrinho(@PathVariable Long id, @PathVariable Long carrinhoId,
            HttpSession session) {
        Optional<Endereco> enderecoOptional = enderecoRepository.findById(id);
        Optional<Carrinho> carrinhoOptional = carrinhoRepository.findById(carrinhoId);

        if (enderecoOptional.isPresent() && carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            carrinho.setEndereco(enderecoOptional.get());
            carrinhoRepository.save(carrinho);

            ModelAndView mv = new ModelAndView("clientes/FormaPagamento");
            mv.addObject("carrinhoPreco", carrinho.getPreco());
            mv.addObject("id", carrinho.getId());
            System.out.println("preço: " + carrinho.getPreco());
            System.out.println("id: " + carrinho.getId());

            return mv;
        }

        return new ModelAndView("erro");
    }

    public ModelAndView resumoPedido(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(id);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            Cliente carrinhoCliente = carrinho.getCliente();
            // Random random = new Random();

            List<Carrinho> pedidosCliente = carrinhoCliente.getCarrinho();

            List<Estoque> produtosComImagem = new ArrayList<>();
            for (Carrinho pedido : pedidosCliente) {
                for (Estoque estoque : pedido.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }
                }
            }
            BigDecimal total = BigDecimal.ZERO;
            for (Carrinho pedidoCarrinho : pedidosCliente) {
                total = total.add(pedidoCarrinho.getPreco());
                carrinho.setValorTotal(total);
                System.out.println("valor total: " + carrinho.getValorTotal());
            }

            System.out.println("forma pagamento " + carrinho.getTipoPagamento());
            System.out.println("valor total: " + carrinho.getValorTotal());

            mv.addObject("valorTotal", carrinho.getValorTotal());
            mv.addObject("pedidos", pedidosCliente);
            mv.addObject("produtosComImagem", produtosComImagem);
            mv.addObject("idCarrinho", carrinho.getId()); // Adicionando o id do carrinho

            // **Adicionando o modelo da view à view**
            mv.setViewName("clientes/ResumoFinalPedido");

            return mv;
        }
        return null;
    }

    public ModelAndView concluirPedido(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();

        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(id);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            Random random = new Random();
            Cliente cliente = carrinho.getCliente();
            String getNomeCliente = cliente.getNome();
            long codigoPedido = random.nextLong();
            carrinho.setCodigoPedido(codigoPedido);
            carrinho.setStatusPagamento("Aguardando o pagamento....");

            carrinhoRepository.save(carrinho);

            List<Estoque> produtos = estoqueRepository.findAll();
            mv.addObject("produtos", produtos);

            mv.addObject("cliente", cliente);
            mv.addObject("nomeUsuario", getNomeCliente);
            mv.addObject("produtos", produtos);

            mv.setViewName("clientes/CatalogoClienteLogado");
            return mv;

        }
        return new ModelAndView("Erro");
    }

    public ModelAndView verPedido(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            List<Carrinho> carrinho = cliente.getCarrinho();
            List<Estoque> produtosComImagem = new ArrayList<>();
            for (Carrinho pedido : carrinho) {
                for (Estoque estoque : pedido.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }
                }
            }
            mv.addObject("produtosComImagem", produtosComImagem);
            mv.addObject("carrinho", carrinho);
            mv.setViewName("clientes/MeusPedidos");
            return mv;
        }
        return null;
    }
}
