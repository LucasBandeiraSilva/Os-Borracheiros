package com.borracheiros.projeto.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.repositories.RoleRepository;
import com.borracheiros.projeto.repositories.UsuarioRepository;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cliente")
public class ClientController {

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;

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
            UserDto usuarioDto, HttpSession session, Model model) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && senha.equals(usuario.getSenha())) {
            usuarioDto.setRole(usuario.getRole().getId());

            System.out.println("roleId: " + usuarioDto.getRole());
            session.setAttribute("roleId", usuario.getRole().getId());
            Long roleId = (Long) session.getAttribute("roleId");

            if (roleId == 3) {
                System.out.println(usuario.getNome());
                // model.addAttribute("nomeUsuario", true);
                model.addAttribute("nomeUsuario", usuario.getNome()); // Adiciona o nome do usuário ao modelo

                return "redirect:/cliente/catalogo";

            }
        }
        model.addAttribute("loginMismatch", true);
        return "clientes/LoginCliente";
    }

    @PostMapping("/catalogo")
    public String createUser(@Valid ClientDto clientDto, @Valid EnderecoDto enderecoDto, BindingResult bindingResult,
            Model model) {

        Cliente cliente = clientDto.toCliente();
        Endereco endereco = enderecoDto.toEndereco();

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            return "ErroCPF";

        }

        if (cliente.getCpf() == null) {
            System.out.println(cliente.getCpf());
            return "ErroCPF";
        }

        
            System.out.println("Formulário com erros");
            System.out.println(bindingResult.getAllErrors());
            if (cliente.getNome() == null) {
                bindingResult.rejectValue("nome", "error.nome", "");
            }

            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                bindingResult.rejectValue("email", "error.userDto", "E-mail já cadastrado");
            }
            // return "clientes/CadastroCliente"; // Retorna a página de cadastro com os erros
        
        clienteRepository.save(cliente);
        endereco.setCliente(cliente);
        enderecoRepository.save(endereco);
        return "/login";
    }
}
