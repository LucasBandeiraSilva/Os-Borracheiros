package com.borracheiros.projeto.Controllers;

import java.util.List;
import java.util.Optional;

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

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.service.ClienteService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cliente")
public class ClientController {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/catalogo")
    public String visualizarProduto(Model model) {
        List<Estoque> produtos = estoqueRepository.findAll();
        model.addAttribute("produtos", produtos);
        return "clientes/catalogo"; // Alterado o retorno para o caminho correto
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
        // return "clientes/catalogo";
        // return "clientes/CatalogoClienteLogado";
    }

    @GetMapping("/verProduto/{id}")
    private ModelAndView descProduto(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Estoque produto = estoqueRepository.findById(id).orElse(null);
        mv.setViewName("clientes/DescProduto");
        mv.addObject("produto", produto);
        return mv;
    }

    @GetMapping("/cadastrar")
    public String clienteCadastro(Model model) {
        ClientDto clientDto = new ClientDto();
        model.addAttribute("clientDto", clientDto);
        return "clientes/CadastroCliente";
    }

    @PostMapping("/login")
    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente != null && encoder.matches(senha, cliente.getSenha())) {
            session.setAttribute("idCliente", cliente.getId());
            session.setAttribute("cliente", cliente);
            session.setAttribute("nomeUsuario", cliente.getNome());
            System.out.println("ID do cliente: " + cliente.getId());
            System.out.println("Nome do cliente: " + cliente.getNome());
            return "redirect:/cliente/logado";
        } else {
            model.addAttribute("loginMismatch", true);
            return "clientes/LoginCliente";
        }
    }

    @GetMapping("/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Cliente> optional = this.clienteRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("cliente", optional.get());
            return "clientes/EditCliente";
        }
        return "Erro";
    }

    @PostMapping("/catalogo")
    public String createUser(ClientDto clientDto, EnderecoDto enderecoDto, BindingResult bindingResult,
            Model model) {

        return clienteService.createUser(clientDto, enderecoDto, bindingResult, model);
    }

    @GetMapping("/excluir-endereco/{id}")
    public String destroyAdress(@PathVariable Long id) {

        Optional<Endereco> optional = this.enderecoRepository.findById(id);
        if (optional.isPresent()) {
            Endereco endereco = optional.get();
            Cliente cliente = endereco.getCliente();
            if (cliente.getEnderecos().size() == 1) {
                return "clientes/AvisoEndereco";
            }
            enderecoRepository.deleteById(id);
            return "clientes/EnderecoExcluido";
        }
        return null;
    }

    @GetMapping("adicionar-endereco/{id}")
    public ModelAndView updateEndereco(@PathVariable("id") Long id, Model model) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            Endereco endereco = new Endereco();
            endereco.setCliente(cliente);
            mv.addObject("endereco", endereco);
            mv.addObject("clienteId", cliente.getId()); // Adicione o clienteId ao modelo
            mv.setViewName("clientes/adicionarEndereco");
            return mv;
        }

        return mv;
    }

    @PostMapping("/adicionar-endereco")
    public String addEndereco(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            Endereco endereco = enderecoDto.toEndereco();

            endereco.setCliente(cliente);

            enderecoRepository.save(endereco);
        }

        return "redirect:/cliente/editar/" + clienteId;
    }

}
