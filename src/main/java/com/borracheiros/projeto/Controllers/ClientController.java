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
            // mv.addObject("cliente", cliente); // Certifique-se de que 'cliente' não seja
            // nulo

            return mv;
        }
    }

    @GetMapping("/cadastrar")
    public ModelAndView clienteCadastro() {
        ModelAndView mv = new ModelAndView();
        ClientDto clientDto = new ClientDto();
        mv.addObject("clientDto", clientDto);
        mv.setViewName("clientes/CadastroCliente");
        return mv;
    }

    @PostMapping("/login")
    public String validacaoLogin(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        return clienteService.validacaoLogin(email, senha, session, model);
    }

    @GetMapping("/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        return clienteService.alterar(id, model);
    }

    @PostMapping("/catalogo")
    public String createUser(ClientDto clientDto, EnderecoDto enderecoDto, BindingResult bindingResult,
            Model model) {

        return clienteService.createUser(clientDto, enderecoDto, bindingResult, model);
    }

    @GetMapping("/excluir-endereco/{id}")
    public String deletarEnderecoPorId(@PathVariable Long id) {

        return endereçoService.deletarEnderecoPorId(id);
    }

    @GetMapping("adicionar-endereco/{id}")
    public ModelAndView updateEndereco(@PathVariable("id") Long id, Model model) {
        return endereçoService.updateEndereco(id, model);
    }

    @PostMapping("/adicionar-endereco")
    public String addEndereco(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        return endereçoService.addEndereco(enderecoDto, clienteId);
    }

    @GetMapping("/adicionar-carrinho/{id}")
    public ModelAndView adicionaCarrinho(@PathVariable("id") Long id, HttpSession session) {
        session.setAttribute("lastClickedButtonIndex", session.getAttribute("lastClickedButton"));
        return carrinhoService.adicionaProdutoCarrinho(id, session);
    }
    @GetMapping("/carrinho/{id}")
    public ModelAndView verCarrinho(@PathVariable Long id){
        return carrinhoService.verCarrinho(id);
    }

    @GetMapping("/endereco-padrao/{id}")
    public ModelAndView definirEnderecoPadrao(@PathVariable Long id){
        return endereçoService.definirEnderecoPadrao(id);
    }

}
