package com.borracheiros.projeto.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.repositories.PedidoRealizadoRepository;

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
    @Autowired
    private PedidoRealizadoRepository pedidoRealizadoRepository;

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
                if (cliente.getCarrinho() != null && !cliente.getCarrinho().isEmpty()) {
                    carrinho = cliente.getCarrinho().get(0);
                }
    
                if (carrinho == null) {
                    // Se não estiver no carrinho, crie um novo Carrinho
                    carrinho = new Carrinho();
                    carrinho.setCliente(cliente);
                    carrinho.getEstoques().add(estoque);
                    carrinho.setQuantidade(1);
                    carrinho.setPreco(BigDecimal.ZERO); // Inicialize o preço como zero
                    carrinho.setNome(nomeProduto);
                } else {
                    // Se estiver no carrinho, atualize a quantidade e adicione o preço original
                    int novaQuantidade = carrinho.getQuantidade() + 1;
                    carrinho.setQuantidade(novaQuantidade);
                }
    
                carrinho.setPreco(carrinho.getPreco().add(estoque.getPreco()));
                carrinhoRepository.save(carrinho); // Salve o carrinho no banco de dados
    
                session.setAttribute("cliente", cliente);
    
                List<Estoque> produtos = estoqueRepository.findAll();
                mv.addObject("produtos", produtos);
                mv.addObject("cliente", cliente);
                mv.addObject("nomeUsuario", cliente.getNome());
                mv.setViewName("clientes/CatalogoClienteLogado");
            } else {
                // Cliente não autenticado
                Carrinho carrinhoNaoAutenticado = (Carrinho) session.getAttribute("carrinhoNaoAutenticado");
    
                if (carrinhoNaoAutenticado == null) {
                    carrinhoNaoAutenticado = new Carrinho();
                    carrinhoNaoAutenticado.getEstoques().add(estoque);
                    carrinhoNaoAutenticado.setNome(nomeProduto);
                    carrinhoNaoAutenticado.setQuantidade(1);
                    carrinhoNaoAutenticado.setPreco(BigDecimal.ZERO); // Inicialize o preço como zero
                } else {
                    int novaQuantidade = carrinhoNaoAutenticado.getQuantidade() + 1;
                    carrinhoNaoAutenticado.setQuantidade(novaQuantidade);
                }
    
                carrinhoNaoAutenticado.setPreco(carrinhoNaoAutenticado.getPreco().add(estoque.getPreco()));
                carrinhoRepository.save(carrinhoNaoAutenticado); // Atualize o carrinho no banco de dados
    
                session.setAttribute("carrinhoNaoAutenticadoID", carrinhoNaoAutenticado.getId());
    
                if (carrinhoNaoAutenticado != null) {
                    System.out.println("carrinho passando......");
                    System.out.println(carrinhoNaoAutenticado.getId());
                }
    
                mv.addObject("produtos", estoqueRepository.findAll());
                mv.setViewName("clientes/catalogo");
            }
        } else {
            // Lógica para lidar com o caso em que o estoque não foi encontrado
            // Pode ser uma boa ideia redirecionar o usuário para uma página de erro.
            mv.setViewName("erro");
        }
    
        return mv;
    }
    

    public ModelAndView verCarrinhoNaoLogado(@PathVariable Long id, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        Optional<Estoque> estoqueOptional = this.estoqueRepository.findById(id);
        
        if (estoqueOptional.isPresent()) {
            System.out.println("Eu não estou logado...");
            Estoque estoque = estoqueOptional.get();
            mv.addObject("carrinhos", estoque.getCarrinhos());
            List<Estoque> produtosComImagem = new ArrayList<>(); // Lista para armazenar produtos com imagem
            BigDecimal totalItens = BigDecimal.ZERO;
    
            for (Carrinho carrinho : estoque.getCarrinhos()) {
                for (Estoque produto : carrinho.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(produto.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }
                    totalItens = totalItens.add(carrinho.getPreco()); // Adiciona o preço do produto ao total
                }
            }
            Long carrinhoNaoAutenticadoID = (Long) session.getAttribute("carrinhoNaoAutenticadoID");
            if (carrinhoNaoAutenticadoID != null) {
                System.out.println("to no carrinho não autenticado!!!" + carrinhoNaoAutenticadoID);
            }
    
            mv.addObject("produtosComImagem", produtosComImagem);
            mv.addObject("totalItens", totalItens);
            mv.setViewName("clientes/ResumoPedidoNaoLogado");
            return mv;
        }
    
        System.out.println("Estou retornando null");
        return null;
    }
    
    

    public ModelAndView verCarrinho(Long id, HttpSession session) {
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        ModelAndView mv = new ModelAndView();

        // Verifica se o cliente está autenticado
        if (clienteSession != null) {
            System.out.println("Eu estou no carrinho");
        }

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            List<Carrinho> carrinhos = carrinhoRepository.findByCliente(cliente);

            mv.addObject("cliente", cliente);
            mv.addObject("carrinhos", carrinhos);

            List<Estoque> produtosComImagem = new ArrayList<>(); // Lista para armazenar produtos com imagem

            for (Carrinho carrinho : carrinhos) {
                for (Estoque estoque : carrinho.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }
                    
                    if(carrinho.getPreco() != null){
                        mv.addObject("totalItens", carrinho.getPreco());
                        System.out.println("preçooooooooo");
                    }
                }
            }

            // Cálculo do total dos produtos
            BigDecimal total = BigDecimal.ZERO;
            for (Carrinho carrinho : carrinhos) {
                total = total.add(carrinho.getPreco());
                carrinho.setValorTotal(total);
            }

            // Adiciona o valor do frete ao totalFrete
            mv.addObject("totalItens", total); // Adiciona o total dos produtos para exibição na página
            mv.addObject("produtosComImagem", produtosComImagem); // Adiciona os produtos com imagem para exibição
            mv.setViewName("clientes/ResumoPedido");
        } else {
            // Cliente não autenticado
            Carrinho carrinhoNaoAutenticado = (Carrinho) session.getAttribute("carrinhoNaoAutenticado");

            if (carrinhoNaoAutenticado != null) {
                // Cálculo do total dos produtos
                BigDecimal total = BigDecimal.ZERO;
                total = total.add(carrinhoNaoAutenticado.getPreco());
                carrinhoNaoAutenticado.setValorTotal(total);

                mv.addObject("totalItens", total);
                mv.addObject("produtosComImagem", carrinhoNaoAutenticado.getEstoques());
                mv.addObject("carrinhoNaoAutenticado", carrinhoNaoAutenticado);
                mv.setViewName("clientes/ResumoPedidoNaoLogado");
            } else {
                return null; // Lidar com o caso em que não há carrinho não autenticado
            }
        }

        return mv;
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

    public ModelAndView resumoPedido(@PathVariable Long id, @RequestParam("tipoPagamento") String tipoPagamento,
            HttpSession session) {

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        ModelAndView mv = new ModelAndView();

        // Verifica se o cliente está autenticado
        if (clienteSession != null) {
            System.out.println("Eu estou no resumo");
        }
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(id);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            Cliente carrinhoCliente = carrinho.getCliente();
            Long idCliente = carrinhoCliente.getId();
            // Random random = new Random();

            carrinho.setTipoPagamento(tipoPagamento);
            this.carrinhoRepository.save(carrinho);
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
            System.out.println("id cliente: " + idCliente);
            mv.addObject("idcliente", idCliente);
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

    public ModelAndView concluirPedido(@PathVariable Long clienteId) {
        ModelAndView mv = new ModelAndView();

        // Variáveis a serem trazidas à tona
        Cliente cliente = null;
        String getNomeCliente = null;

        // Recupere todos os carrinhos do cliente
        List<Carrinho> carrinhos = this.carrinhoRepository.findAllByClienteId(clienteId);

        for (Carrinho carrinho : carrinhos) {
            cliente = carrinho.getCliente();

            // Iterar sobre cada produto no carrinho
            for (Estoque produto : carrinho.getEstoques()) {
                PedidoRealizado pedidoRealizado = new PedidoRealizado();
                pedidoRealizado.setCodigoPedido(Math.abs(new Random().nextLong()));
                pedidoRealizado.setStatusPagamento("Aguardando o pagamento");
                LocalDateTime horaAtualBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
                Date sqlDate = Date.valueOf(horaAtualBrasilia.toLocalDate());
                pedidoRealizado.setDataPedido(sqlDate);

                pedidoRealizado.setCliente(cliente);
                pedidoRealizado.setNome(produto.getNome());
                pedidoRealizado.setPreco(carrinho.getPreco());
                pedidoRealizado.setQuantidade(carrinho.getQuantidade());
                pedidoRealizado.setEndereco(carrinho.getEndereco());
                pedidoRealizado.setTipoPagamento(carrinho.getTipoPagamento());
                pedidoRealizado.setFrete(carrinho.getFrete());

                // Salvar o pedido realizado para cada produto no carrinho
                pedidoRealizadoRepository.save(pedidoRealizado);
            }
            //deleta o carrinho
            carrinhoRepository.delete(carrinho);
        }

        if (cliente != null) {
            getNomeCliente = cliente.getNome(); 
        }

        List<Estoque> produtos = estoqueRepository.findAll();
        mv.addObject("produtos", produtos);
        mv.addObject("cliente", cliente);
        mv.addObject("nomeUsuario", getNomeCliente);
        mv.setViewName("clientes/CatalogoClienteLogado");
        return mv;
    }

    

    public ModelAndView verPedido(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();

        Optional<Cliente> clienteOptional = clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            // Buscar pedidos realizados pelo cliente
            List<PedidoRealizado> pedidosRealizados = pedidoRealizadoRepository.findByCliente(cliente);

            mv.addObject("pedidosRealizados", pedidosRealizados);
            mv.addObject("nomeCliente", cliente.getNome());
            mv.setViewName("clientes/MeusPedidos");
            return mv;
        }

        return null;
    }

}
