package com.borracheiros.projeto.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.PedidoRealizadoRepository;
import com.borracheiros.projeto.repositories.RoleRepository;
import com.borracheiros.projeto.repositories.UsuarioRepository;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UsuarioService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    

    @Autowired
    private PedidoRealizadoRepository pedidoRealizadoRepository;

    public void status(long id, boolean StatusUsuario) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setStatusUsuario(StatusUsuario);
            usuarioRepository.save(usuario);
        } else {
            throw new EntityNotFoundException("Impossivel mudar o status do usuario");
        }

    }

    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> optional = this.usuarioRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("usuario", optional.get());
            return "usuarios/Edit";
        }
        return "Erro";
    }

    public String createUser(@Valid UserDto usuarioDto, BindingResult bindingResult, Model model) {
        Usuario usuario = usuarioDto.toUsuario();
        Long roleId = usuarioDto.getRole();
        Role role = roleRepository.findById(roleId).orElse(null);
    
        if (usuarioDto.getId() != null) {
            return updateUser(usuarioDto, bindingResult, usuario, role,model);
        } else {
            return createUser(usuarioDto, bindingResult, usuario, role);
        }
    }
    
    private String updateUser(UserDto usuarioDto, BindingResult bindingResult, Usuario usuario, Role role,Model model) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDto.getId());
    
        if (optionalUsuario.isPresent()) {
            Usuario usuarioExistente = optionalUsuario.get();
    
            usuarioExistente.setRole(role);
            usuarioExistente.setNome(usuarioDto.getNome());
            usuarioExistente.setSenha(usuarioDto.getSenha());
            usuarioExistente.setCpf(usuarioDto.getCpf());

            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())) {
                model.addAttribute("passwordMismatch", true);
                bindingResult.rejectValue("confirmPassword", "error.userDto", "As senhas não coincidem");
                return "usuarios/cadastro"; // Retorna a página de cadastro com os erros
            }
            
            
            if (!usuarioExistente.getCpf().equals(usuario.getCpf())) {
                if (usuarioRepository.existsByCpf(usuario.getCpf())) {
                    return "usuarios/ErroCPF";
                }
            }
    
            usuarioRepository.save(usuarioExistente);
            return "redirect:/";
        }
    
        return handleUserErrors(usuario, bindingResult);
    }
    
    private String createUser(UserDto usuarioDto, BindingResult bindingResult, Usuario usuario, Role role) {
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            bindingResult.rejectValue("cpf", "error.userDto", "CPF já cadastrado");
        }
    
        if (usuario.getCpf() == null || usuario.getCpf().isEmpty()) {
            bindingResult.rejectValue("cpf", "error.userDto", "Campo CPF é obrigatório");
        }
    
        if (bindingResult.hasErrors()) {
            System.out.println("Formulário com erros");
            System.out.println(bindingResult.getAllErrors());
            if (usuario.getNome() == null) {
                bindingResult.rejectValue("nome", "error.nome", "");
            }
            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.userDto", "As senhas não coincidem");
            }
    
            return "usuarios/cadastro"; // Retorna a página de cadastro com os erros
        }
    
        if (role != null) {
            usuario.setRole(role);
            usuarioRepository.save(usuario);
            System.out.println("Usuário criado com sucesso");
            return "redirect:/"; // Redireciona para a página inicial
        } else {
            return "Erro";
        }
    }
    
    private String handleUserErrors(Usuario usuario, BindingResult bindingResult) {
        // Lógica comum para tratamento de erros do usuário
        if (bindingResult.hasErrors()) {
            System.out.println("Formulário com erros");
            System.out.println(bindingResult.getAllErrors());
            if (usuario.getNome() == null) {
                bindingResult.rejectValue("nome", "error.nome", "");
            }
            // Adicione outras validações conforme necessário
            return "usuarios/cadastro"; // Retorna a página de cadastro com os erros
        }
        return null;
    }
    
    

    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha,
            UserDto usuarioDto, HttpSession session, Model model) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && senha.equals(usuario.getSenha())) {
            usuarioDto.setRole(usuario.getRole().getId());

            System.out.println("roleId: " + usuarioDto.getRole());
            session.setAttribute("roleId", usuario.getRole().getId());
            Long roleId = (Long) session.getAttribute("roleId");

            if (roleId == 1) {

                return "redirect:/";
            } else if (roleId == 2) {
                return "redirect:/estoquista";
            }
        } else {
            model.addAttribute("loginMismatch", true);
            return "Login";
        }
        return null;
    }

    public ModelAndView ListaUsuario(HttpSession session) {

        ModelAndView mv = new ModelAndView();
        Long roleId = (Long) session.getAttribute("roleId");
        if (roleId == null) {
            mv.setViewName("aviso");
            return mv;
        }

        List<Usuario> usuarios = this.usuarioRepository.findAll();
        mv.setViewName("usuarios/ListaUsuario");
        mv.addObject("usuarios", usuarios);
        return mv;

    }

    public ModelAndView listaPedidos(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        List<PedidoRealizado> pedidoRealizado = this.pedidoRealizadoRepository.findAllByOrderByDataPedidoDesc();

        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null || roleId != 2) {
            return new ModelAndView("aviso");
        }

        if (pedidoRealizado.isEmpty()) {
            return new ModelAndView("usuarios/ListaPedidosVazia");
        }
        mv.setViewName("usuarios/ListaPedido");
        mv.addObject("carrinho", pedidoRealizado);
        return mv;
    }

    public ModelAndView editarStatusPedido(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<PedidoRealizado> pedidoRealizadoOptional = this.pedidoRealizadoRepository.findById(id);
        if (pedidoRealizadoOptional.isPresent()) {
            mv.addObject("status", pedidoRealizadoOptional.get());
            mv.setViewName("/usuarios/EditaStatus");
            return mv;
        }
        return null;
    }

    public ModelAndView editarPedido(@RequestParam("StatusPagamento") String StatusPagamento, @PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<PedidoRealizado> pedidoRealizadoOptional = this.pedidoRealizadoRepository.findById(id);
        if (pedidoRealizadoOptional.isPresent()) {
            PedidoRealizado pedidoRealizado = pedidoRealizadoOptional.get();
            pedidoRealizado.setStatusPagamento(StatusPagamento);
            mv.setViewName("redirect:/pedidos");
            this.pedidoRealizadoRepository.save(pedidoRealizado);
            return mv;
        }
        return null;
    }
}
