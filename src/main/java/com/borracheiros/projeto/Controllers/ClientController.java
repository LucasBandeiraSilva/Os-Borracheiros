package com.borracheiros.projeto.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.service.CarrinhoService;
import com.borracheiros.projeto.service.ClienteService;
import com.borracheiros.projeto.service.EndereçoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cliente")
public class ClientController {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private CarrinhoService carrinhoService;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private EndereçoService endereçoService;

    @GetMapping("/catalogo")
    public String visualizarProduto(Model model) {
        List<Estoque> produtos = estoqueRepository.findAll();
        model.addAttribute("produtos", produtos);
        return "clientes/catalogo";
    }

    @GetMapping("/login")
    public String loginCliente() {
        return "clientes/LoginCliente";
    }

    @GetMapping("/sair")
    public String sair(HttpSession session) {
        session.invalidate();
        return "clientes/Deslogar";
    }

    @GetMapping("/logado")
    private ModelAndView clienteLogado(HttpSession session) {

        ModelAndView mv = new ModelAndView();
        if (session.getAttribute("nomeUsuario") == null) {
            mv.setViewName("clientes/SecaoInvalida");
            return mv;
        }

        List<Estoque> produtos = estoqueRepository.findAll();

        mv.setViewName("clientes/CatalogoClienteLogado");
        mv.addObject("produtos", produtos);
        mv.addObject("cliente", session.getAttribute("cliente"));
        mv.addObject("nomeUsuario", session.getAttribute("nomeUsuario"));

        return mv;

    }

    @GetMapping("/verProduto/{id}")
    private ModelAndView descProduto(@PathVariable Long id, HttpSession session) {
        System.out.println("acessando produtos.......");
        ModelAndView mv = new ModelAndView();
        Estoque produto = estoqueRepository.findById(id).orElse(null);
        if (session.getAttribute("nomeUsuario") != null) {

            System.out.println("\n \n \n usuario possui sessão aqui");
            mv.setViewName("clientes/DescProduto_ClienteLogado");
            mv.addObject("cliente", session.getAttribute("cliente"));
            mv.addObject("nomeUsuario", session.getAttribute("nomeUsuario"));
            mv.addObject("produto", produto);
            return mv;
        } else {

            mv.setViewName("clientes/DescProduto");
            mv.addObject("produto", produto);

            return mv;
        }
    }

    @GetMapping("/cadastrar")
    public ModelAndView clienteCadastro() {
        ModelAndView mv = new ModelAndView();
        mv.addObject("clientDto", new ClientDto());
        mv.setViewName("clientes/CadastroCliente");
        return mv;
    }

    @PostMapping("/login")
    public String validacaoLogin(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        return clienteService.validacaoLogin(email, senha, session, model);
    }

    @GetMapping("/editar/{id}")
    public ModelAndView alterar(@PathVariable("id") Long id) {
        return clienteService.alterar(id);
    }

    @PostMapping("/validar-edicao/{id}")
    public ModelAndView validarEdicao(@PathVariable Long id, @ModelAttribute("clientDto") ClientDto clientDto) {
        return clienteService.edicaoCliente(id, clientDto);
    }

    @PostMapping("/catalogo")
    public ModelAndView createUser(ClientDto clientDto, EnderecoDto enderecoDto, BindingResult bindingResult,
            @RequestParam("enderecoFaturamento") boolean enderecoFaturamento) {

        return clienteService.createUser(clientDto, enderecoDto, bindingResult, enderecoFaturamento);
    }

    @GetMapping("/excluir-endereco/{id}")
    public String deletarEnderecoPorId(@PathVariable Long id) {

        return endereçoService.deletarEnderecoPorId(id);
    }

    @GetMapping("adicionar-endereco/{id}")
    public ModelAndView updateEndereco(@PathVariable("id") Long id, Model model) {
        return endereçoService.updateEndereco(id, model);
    }

    @GetMapping("adicionar-endereco-cehckout/{id}")
    public ModelAndView updateEnderecoCheckout(@PathVariable("id") Long id) {
        return endereçoService.updateEnderecoCheckout(id);
    }

    @GetMapping("/adicionar-enderecoEntrega/{id}")
    public ModelAndView addEnderecoEntreg(@PathVariable("id") Long id) {
        return endereçoService.addEntrega(id);
    }

    @PostMapping("/adicionar-endereco")
    public String addEndereco(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        return endereçoService.addEndereco(enderecoDto, clienteId);
    }

    @PostMapping("/adicionar-enderecoEntrega")
    public String salvarEnderecoEntrega(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        return endereçoService.addEnderecoEntrega(enderecoDto, clienteId);
    }

    @PostMapping("/adicionar-enderecoEntrega/checkout")
    public String salvarEnderecoEntregaCheckout(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        return endereçoService.addEnderecoEntregaCheckout(enderecoDto, clienteId);
    }

    @GetMapping("/adicionar-carrinho/{id}")
    public ModelAndView adicionaCarrinho(@PathVariable("id") Long id, HttpSession session) {
        session.setAttribute("lastClickedButtonIndex", session.getAttribute("lastClickedButton"));
        return carrinhoService.adicionaProdutoCarrinho(id, session);
    }

    @GetMapping("/carrinho/{id}")
    public ModelAndView verCarrinho(@PathVariable Long id, HttpSession session) {
        return carrinhoService.verCarrinho(id, session);
    }

    @GetMapping("/anonimo/carrinho/")
    public ModelAndView verCarrinhoNaoLogado(HttpSession session) {
        return carrinhoService.verCarrinhoNaoLogado(session);
    }

    @GetMapping("/endereco-padrao/{id}")
    public ModelAndView definirEnderecoPadrao(@PathVariable Long id) {
        return endereçoService.definirEnderecoPadrao(id);
    }

    @GetMapping("/adicionarUm/{id}")
    public ModelAndView adicionarUm(@PathVariable Long id, HttpSession session) {
        return carrinhoService.adcionaUm(id, session);
    }

    @GetMapping("/removeUm/{id}")
    public ModelAndView removeUm(@PathVariable Long id, HttpSession session) {
        return carrinhoService.removeUM(id, session);
    }

    @GetMapping("/deletar/{id}")
    public ModelAndView deletar(@PathVariable Long id, HttpSession session) {
        return carrinhoService.removeCarrinho(id, session);
    }

    @PostMapping("/frete/{id}")
    public ModelAndView calcularFrete(@PathVariable Long id, @RequestParam("frete") String freteSelecionado,
            HttpSession session) {
        return carrinhoService.calcularFrete(id, freteSelecionado, session);
    }

    @PostMapping("/frete/carrinhoNaoAutenticado/{id}")
    public ModelAndView calcularFretecarrinhoNaoAutenticado(@PathVariable Long id,
            @RequestParam("frete") String freteSelecionado,
            HttpSession session) {
        return carrinhoService.calcularFretecarrinhoNaoAutenticado(id, freteSelecionado, session);
    }

    @GetMapping("/endereco/selecionar/{id}")
    public ModelAndView selecionarEndereco(@PathVariable Long id) {
        return clienteService.selecionarEndereco(id);
    }

    @GetMapping("/endereco/associarCarrinho/{enderecoId}")
    public ModelAndView associarCarrinhoEndereco(@PathVariable Long enderecoId, HttpSession session) {
        return carrinhoService.associarEnderecoAoCarrinho(enderecoId, session);
    }

    @PostMapping("/resumo/{id}")
    public ModelAndView resumoPedido(@PathVariable Long id, @RequestParam("tipoPagamento") String tipoPagamento,
            HttpSession session) {
        return carrinhoService.resumoPedido(id, tipoPagamento, session);
    }

    @GetMapping("/finalizado/{id}")
    public ModelAndView concluirPedido(@PathVariable Long id, HttpSession session) {
        return carrinhoService.concluirPedido(id, session);
    }

    @GetMapping("/meusPedidos/{id}")
    public ModelAndView meusPedidos(@PathVariable Long id) {
        return carrinhoService.meusPedidos(id);
    }

    @GetMapping("/detalhes/pedido/{codigoProduto}")
    public ModelAndView pedidoDetalhe(@PathVariable Long codigoProduto, HttpSession session) {
        return carrinhoService.pedidoDetalhe(codigoProduto, session);
    }

    // Abaixo os metodos adiciona e remove +1, remove o produto do carrinho do
    // cliente não logado
    @GetMapping("/add/{id}")
    public ModelAndView addItem(@PathVariable Long id) {
        return carrinhoService.maisUmProduto(id);
    }

    @GetMapping("/menosUmProduto/{id}/delete")
    public ModelAndView removeItem(@PathVariable Long id) {
        return carrinhoService.menosUmProduto(id);
    }

    @GetMapping("/carrinho/{id}/remove")
    public ModelAndView removerProduto(@PathVariable Long id) {
        return carrinhoService.removerProduto(id);
    }

}
