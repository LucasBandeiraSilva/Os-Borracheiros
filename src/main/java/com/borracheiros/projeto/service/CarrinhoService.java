package com.borracheiros.projeto.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.carrinho.CarrinhoNaoAutenticado;
import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.CarrinhoNaoAutenticadoRepository;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.repositories.PedidoRealizadoRepository;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CarrinhoNaoAutenticadoRepository carrinhoNaoAutenticadoRepository;

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
                // Cliente autenticado
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
                CarrinhoNaoAutenticado carrinhoNaoAutenticado = (CarrinhoNaoAutenticado) session
                        .getAttribute("carrinhoNaoAutenticado");

                if (carrinhoNaoAutenticado == null) {
                    carrinhoNaoAutenticado = new CarrinhoNaoAutenticado();
                }

                // Adicione o estoque ao carrinho não autenticado
                carrinhoNaoAutenticado.getEstoques().add(estoque);

                carrinhoNaoAutenticado.setQuantidade(1);
                carrinhoNaoAutenticado.setPreco(BigDecimal.ZERO); // Inicialize o preço como zero

                carrinhoNaoAutenticado.setNome(nomeProduto);
                carrinhoNaoAutenticado.setPreco(carrinhoNaoAutenticado.getPreco().add(estoque.getPreco()));

                // Salve o carrinho não autenticado no banco de dados
                carrinhoNaoAutenticadoRepository.save(carrinhoNaoAutenticado);

                if (carrinhoNaoAutenticado != null) {
                    System.out.println("Carrinho não autenticado salvo no banco.");
                    System.out.println(carrinhoNaoAutenticado.getId());
                }

                mv.addObject("produtos", estoqueRepository.findAll());
                mv.setViewName("clientes/catalogo");
            }
        } else {
            mv.setViewName("erro");
        }

        return mv;
    }

    public ModelAndView verCarrinhoNaoLogado(HttpSession session) {
        ModelAndView mv = new ModelAndView();

        // Recupera todos os carrinhos não autenticados
        List<CarrinhoNaoAutenticado> carrinhosNaoAutenticados = carrinhoNaoAutenticadoRepository.findAll();

        if (!carrinhosNaoAutenticados.isEmpty()) {
            List<Estoque> produtosComImagem = new ArrayList<>(); // Lista para armazenar produtos com imagem
            BigDecimal totalItens = BigDecimal.ZERO;
            BigDecimal totalFrete = BigDecimal.ZERO; // Inicializa o total do frete

            for (CarrinhoNaoAutenticado carrinhoNaoAutenticado : carrinhosNaoAutenticados) {
                for (Estoque estoque : carrinhoNaoAutenticado.getEstoques()) {
                    Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                    if (produtoComImagem != null) {
                        produtosComImagem.add(produtoComImagem);
                    }

                    if (carrinhoNaoAutenticado.getPreco() != null) {
                        totalItens = totalItens.add(carrinhoNaoAutenticado.getPreco());
                    }
                }

                // Adiciona o frete ao totalFrete
                if (carrinhoNaoAutenticado.getFrete() != null) {
                    totalFrete = (carrinhoNaoAutenticado.getFrete());
                }
            }

            System.out.println("total do frete no carrinho nao logado: " + totalFrete);

            // adicionando models
            mv.addObject("totalItens", totalItens);
            mv.addObject("totalFrete", totalFrete); // Adiciona o total do frete ao modelo

            mv.addObject("produtosComImagem", produtosComImagem);
            mv.addObject("carrinhosNaoAutenticados", carrinhosNaoAutenticados);
            mv.setViewName("clientes/CarrinhoNaoLogado");
        } else {
            // Se a lista de carrinhos não autenticados estiver vazia, redirecione para a
            // página de carrinho vazio
            return new ModelAndView("clientes/CarrinhoVazioNaoLogado");
        }

        return mv;
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
            BigDecimal totalFrete = BigDecimal.ZERO;

            Cliente cliente = clienteOptional.get();
            List<Carrinho> carrinhos = carrinhoRepository.findByCliente(cliente);
            Optional<Endereco> enderecoPadraoOptional = enderecoRepository
                    .findByClienteIdAndEnderecoPadraoTrue(cliente.getId());

            if (enderecoPadraoOptional.isPresent()) {
                Endereco enderecoPadrao = enderecoPadraoOptional.get();
                String cepCliente = enderecoPadrao.getCep();
                mv.addObject("cepCliente", cepCliente); // Adiciona o CEP ao modelo
            } else {
                System.out.println("o cliente não possui um endereço padrão de entrega");
            }

            mv.addObject("cliente", cliente);
            mv.addObject("carrinhos", carrinhos);

            List<Estoque> produtosComImagem = new ArrayList<>(); // Lista para armazenar produtos com imagem

            // Verifica se a lista carrinhos não está vazia antes de acessar seus elementos
            if (!carrinhos.isEmpty()) {
                for (Carrinho carrinho : carrinhos) {
                    for (Estoque estoque : carrinho.getEstoques()) {
                        Estoque produtoComImagem = estoqueRepository.findById(estoque.getId()).orElse(null);
                        if (produtoComImagem != null) {
                            produtosComImagem.add(produtoComImagem);
                        }

                        if (carrinho.getPreco() != null) {
                            mv.addObject("totalItens", carrinho.getPreco());
                        }
                        if(carrinho.getFrete() != null){
                            totalFrete = carrinho.getFrete();
                            System.out.println("logado frete: " + totalFrete);
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
                mv.addObject("totalItens", total);
                mv.addObject("produtosComImagem", produtosComImagem);
                mv.addObject("carrinho", carrinhos); 
                mv.addObject("totalFrete",totalFrete);
                mv.setViewName("clientes/Carrinho");
            } else {
                // A lista carrinhos está vazia, redirecione para a página de carrinho vazio
                return new ModelAndView("clientes/CarrinhoVazio");
            }
        }

        // Se clienteOptional estiver vazio, retorna uma ModelAndView vazia
        return mv;
    }

    // adição de mais 1 produto no carrinho de cliente não logado
    public ModelAndView maisUmProduto(@PathVariable Long carrinhoId) {
        Optional<CarrinhoNaoAutenticado> carrinhoNaoAutenticadoOptional = this.carrinhoNaoAutenticadoRepository
                .findById(carrinhoId);
        if (carrinhoNaoAutenticadoOptional.isPresent()) {
            CarrinhoNaoAutenticado carrinhoNaoAutenticado = carrinhoNaoAutenticadoOptional.get();
            int novaQuantidade = carrinhoNaoAutenticado.getQuantidade() + 1;
            carrinhoNaoAutenticado.setQuantidade(novaQuantidade);
            BigDecimal preco = carrinhoNaoAutenticado.getEstoques().get(0).getPreco();
            BigDecimal novoPreco = preco.multiply(BigDecimal.valueOf(carrinhoNaoAutenticado.getQuantidade()));
            carrinhoNaoAutenticado.setPreco(novoPreco);
            carrinhoNaoAutenticadoRepository.save(carrinhoNaoAutenticado);
            ModelAndView mv = new ModelAndView("redirect:/cliente/anonimo/carrinho/");
            return mv;
        }
        return null;
    }

    // remoção de um produto do cliente não logado
    public ModelAndView menosUmProduto(@PathVariable Long carrinhoId) {
        Optional<CarrinhoNaoAutenticado> carrinhoNaoAutenticadoOptional = this.carrinhoNaoAutenticadoRepository
                .findById(carrinhoId);
        if (carrinhoNaoAutenticadoOptional.isPresent()) {
            CarrinhoNaoAutenticado carrinhoNaoAutenticado = carrinhoNaoAutenticadoOptional.get();
            int novaQuantidade = carrinhoNaoAutenticado.getQuantidade() - 1;
            carrinhoNaoAutenticado.setQuantidade(novaQuantidade);
            BigDecimal preco = carrinhoNaoAutenticado.getEstoques().get(0).getPreco();
            BigDecimal novoPreco = preco.multiply(BigDecimal.valueOf(carrinhoNaoAutenticado.getQuantidade()));
            carrinhoNaoAutenticado.setPreco(novoPreco);
            carrinhoNaoAutenticadoRepository.save(carrinhoNaoAutenticado);
            ModelAndView mv = new ModelAndView("redirect:/cliente/anonimo/carrinho/");
            return mv;
        } else {
            return new ModelAndView("erro");
        }
    }

    // remover produto do carrinho do cliente não logado
    public ModelAndView removerProduto(@PathVariable Long carrinhoId) {
        Optional<CarrinhoNaoAutenticado> carrinhoNaoAutenticadoOptional = this.carrinhoNaoAutenticadoRepository
                .findById(carrinhoId);

        if (carrinhoNaoAutenticadoOptional.isPresent()) {
            carrinhoNaoAutenticadoRepository.deleteById(carrinhoId);
            ModelAndView mv = new ModelAndView("redirect:/cliente/anonimo/carrinho/");
            return mv;
        } else {
            return new ModelAndView("erro");

        }

    }

    public ModelAndView adcionaUm(@PathVariable Long carrinhoId, HttpSession session) {
        System.out.println("entrei no metodo");
        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(carrinhoId);

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        if (clienteSession == null) {
            return new ModelAndView("redirect:/clientes/AvisoLogin");

        }

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
        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        if (clienteSession == null) {
            return new ModelAndView("redirect:/clientes/AvisoLogin");

        }

        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();

            int novaQuantidade = carrinho.getQuantidade() - 1;
            carrinho.setQuantidade(novaQuantidade);

            BigDecimal precoProduto = carrinho.getEstoques().get(0).getPreco();

            BigDecimal novoPreco = precoProduto.multiply(BigDecimal.valueOf(carrinho.getQuantidade()));
            carrinho.setPreco(novoPreco);

            carrinhoRepository.save(carrinho);
            if (novaQuantidade == 0) {
                carrinhoRepository.deleteById(carrinhoId);
                return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());

            }

            // Redireciona de volta para a página do resumo do pedido
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        } else {

            // Redireciona para uma página de erro ou outra página adequada
            return new ModelAndView("redirect:/pagina-de-erro");
        }
    }

    public ModelAndView removeCarrinho(@PathVariable Long carrinhoId, HttpSession session) {

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        if (clienteSession == null) {
            return new ModelAndView("redirect:/clientes/AvisoLogin");

        }

        Optional<Carrinho> carrinhoOptional = this.carrinhoRepository.findById(carrinhoId);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            carrinhoRepository.deleteById(carrinhoId);
            return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
        } else {
            return new ModelAndView("Erro");
        }
    }

    public ModelAndView calcularFrete(@PathVariable Long id, @RequestParam("frete") String freteSelecionado,
            HttpSession session) {
        BigDecimal frete = new BigDecimal(freteSelecionado);

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        if (clienteSession == null) {
            return new ModelAndView("clientes/LoginCliente");

        } else {
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

                    this.carrinhoRepository.saveAndFlush(carrinho);
                }

                return new ModelAndView("redirect:/cliente/carrinho/" + carrinho.getCliente().getId());
            }
        }
        return new ModelAndView("erro");
    }

    public ModelAndView calcularFretecarrinhoNaoAutenticado(@PathVariable Long id,
            @RequestParam("frete") String freteSelecionado,
            HttpSession session) {
        freteSelecionado = freteSelecionado.replace(",", ".");

        BigDecimal frete = new BigDecimal(freteSelecionado);

        Optional<CarrinhoNaoAutenticado> carrinhoNaoAutenticadoOptional = this.carrinhoNaoAutenticadoRepository
                .findById(id);
        if (carrinhoNaoAutenticadoOptional.isPresent()) {
            CarrinhoNaoAutenticado carrinhoNaoAutenticado = carrinhoNaoAutenticadoOptional.get();

            BigDecimal valorTotal = carrinhoNaoAutenticado.getPreco();

            if (carrinhoNaoAutenticado.getFrete() == null || !carrinhoNaoAutenticado.getFrete().equals(frete)) {
                BigDecimal freteAntigo = carrinhoNaoAutenticado.getFrete();

                if (freteAntigo != null) {
                    // Subtrai o frete antigo do valor total
                    valorTotal = valorTotal.subtract(freteAntigo);
                }

                // Adiciona o novo frete ao valor total
                valorTotal = valorTotal.add(frete);

                // Atualiza o frete no carrinho
                carrinhoNaoAutenticado.setFrete(frete);

                // Atualiza o valor total no carrinho
                carrinhoNaoAutenticado.setPreco(valorTotal);

                this.carrinhoNaoAutenticadoRepository.saveAndFlush(carrinhoNaoAutenticado);
                return new ModelAndView("redirect:/cliente/anonimo/carrinho/");
            }

        }

        return new ModelAndView("erro");
    }

    public ModelAndView associarEnderecoAoCarrinho(@PathVariable Long id, HttpSession session) {
        Optional<Endereco> enderecoOptional = enderecoRepository.findById(id);

        if (enderecoOptional.isPresent()) {
            Endereco enderecoEscolhido = enderecoOptional.get();
            Cliente cliente = enderecoEscolhido.getCliente();

            List<Endereco> enderecos = cliente.getEnderecos();
            List<Carrinho> carrinhos = cliente.getCarrinho();

            ModelAndView mv = new ModelAndView("clientes/FormaPagamento");
            mv.addObject("enderecos", enderecos);
            mv.addObject("carrinhos", carrinhos);
            Carrinho carrinhoEscolhido = carrinhos.get(0);
            carrinhoEscolhido.setEndereco(enderecoEscolhido);
            carrinhoRepository.save(carrinhoEscolhido);

            mv.addObject("carrinhoPreco", carrinhoEscolhido.getPreco());
            mv.addObject("id", carrinhoEscolhido.getId());

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

            session.setAttribute("valorTotalPedido", carrinho.getValorTotal());

            System.out.println("forma pagamento " + carrinho.getTipoPagamento());
            System.out.println("valor total: " + carrinho.getValorTotal());
            System.out.println("id cliente: " + idCliente);
            mv.addObject("idcliente", idCliente);
            mv.addObject("formaPagamento", carrinho.getTipoPagamento());
            mv.addObject("frete", carrinho.getFrete());
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

    public ModelAndView concluirPedido(@PathVariable Long clienteId, HttpSession session) {
        ModelAndView mv = new ModelAndView();

        Cliente cliente = null;
        String getNomeCliente = null;
        Long idCliente = null;
        BigDecimal valorTotalSessao = (BigDecimal) session.getAttribute("valorTotalPedido");

        if (valorTotalSessao == null) {
            System.out.println("carrinho nulll");
        }

        List<Carrinho> carrinhos = this.carrinhoRepository.findAllByClienteId(clienteId);
        Long codigoPedidoUnico = Math.abs(new Random().nextLong());

        for (Carrinho carrinho : carrinhos) {
            cliente = carrinho.getCliente();
            idCliente = cliente.getId();

            // Movendo a criação do PedidoRealizado para fora do loop interno
            PedidoRealizado pedidoRealizado = new PedidoRealizado();
            pedidoRealizado.setCodigoPedido(codigoPedidoUnico);
            pedidoRealizado.setStatusPagamento("Aguardando o pagamento");
            LocalDateTime horaAtualBrasilia = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
            Date sqlDate = Date.valueOf(horaAtualBrasilia.toLocalDate());
            pedidoRealizado.setDataPedido(sqlDate);
            pedidoRealizado.setCliente(cliente);

            for (Estoque produto : carrinho.getEstoques()) {
                // Configurando os detalhes do pedido com base no produto atual
                pedidoRealizado.setNome(produto.getNome());
                pedidoRealizado.setPreco(produto.getPreco());
                pedidoRealizado.setQuantidade(carrinho.getQuantidade());
                pedidoRealizado.setEndereco(carrinho.getEndereco());
                pedidoRealizado.setTipoPagamento(carrinho.getTipoPagamento());
                pedidoRealizado.setFrete(carrinho.getFrete());
                pedidoRealizado.setValorTotal(valorTotalSessao);

                System.out.println("preço final " + pedidoRealizado.getValorTotal());
                pedidoRealizadoRepository.save(pedidoRealizado);
            }

            // Restante do código (se houver) para lidar com o carrinho
            carrinhoRepository.delete(carrinho);
        }

        if (cliente != null) {
            getNomeCliente = cliente.getNome();
        }

        mv.addObject("cliente", cliente);
        mv.addObject("idcliente", idCliente);
        mv.addObject("nomeUsuario", getNomeCliente);
        mv.addObject("codigoPedido", codigoPedidoUnico);
        session.removeAttribute("valorTotalPedido");

        mv.setViewName("clientes/PedidoConcluido");

        return mv;
    }

    public ModelAndView verPedido(@PathVariable Long clienteId, @PathVariable Long codigoPedido) {
        ModelAndView mv = new ModelAndView();

        // Buscar pedidos realizados pelo cliente com base no código de pedido
        // selecionado
        List<PedidoRealizado> pedidosRealizados = pedidoRealizadoRepository.findByClienteIdAndCodigoPedido(clienteId,
                codigoPedido);

        mv.addObject("pedidosRealizados", pedidosRealizados);
        mv.setViewName("clientes/DetalhePedido");
        return mv;
    }

    public ModelAndView meusPedidos(@PathVariable Long clienteId) {
        ModelAndView mv = new ModelAndView();

        // Encontrar todos os códigos de pedido distintos associados ao cliente
        List<PedidoRealizado> pedidosRealizados = pedidoRealizadoRepository
                .findDistinctByClienteIdAndCodigoPedidoIsNotNullOrderByCodigoPedidoDesc(clienteId);

        // Criar uma lista de códigos de pedidos únicos
        List<Long> codigosPedidosUnicos = pedidosRealizados.stream()
                .map(PedidoRealizado::getCodigoPedido)
                .distinct()
                .collect(Collectors.toList());

        List<PedidoRealizado> todosOsPedidos = pedidoRealizadoRepository
                .findByClienteIdAndCodigoPedidoInOrderByDataPedidoDesc(clienteId,
                        codigosPedidosUnicos);

        // Associar a lista de códigos de pedidos às informações dos pedidos realizados
        mv.setViewName("clientes/MeusPedidos");
        mv.addObject("pedidos", todosOsPedidos);

        for (PedidoRealizado pedidoRealizado : todosOsPedidos) {
            System.out.println(pedidoRealizado.getNome());
            System.out.println(pedidoRealizado.getId());
        }
        mv.addObject("codigosPedidos", codigosPedidosUnicos);
        return mv;
    }

    public ModelAndView pedidoDetalhe(Long codigoProduto, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        List<PedidoRealizado> pedido = pedidoRealizadoRepository.findByCodigoPedido(codigoProduto);

        Cliente clienteSession = (Cliente) session.getAttribute("cliente");
        Long idCliente = clienteSession.getId();
        if (pedido.size() > 0) {
            mv.setViewName("clientes/PedidoInfo");
            mv.addObject("pedido", pedido);
            mv.addObject("idCliente", idCliente);
            return mv;
        }
        return null;
    }

}
