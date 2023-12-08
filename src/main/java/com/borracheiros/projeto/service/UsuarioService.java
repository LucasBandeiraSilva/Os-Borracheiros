package com.borracheiros.projeto.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.PedidoRealizado;
import com.borracheiros.projeto.dto.UserDto;
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

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        Role role = roleRepository.findById(usuarioDto.getRole()).orElse(null);

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
                bindingResult.rejectValue("nome", "error.nome", "não pode ser vazio");
            }
            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.userDto", "As senhas não coincidem");
            }

            return "usuarios/cadastro"; // Retorna a página de cadastro com os erros
        }

        if (role != null) {
            usuario.setRole(role);
            String senhaCriptografada = encoder.encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
            usuarioRepository.save(usuario);
            System.out.println("Usuário criado com sucesso");
            return "redirect:/login"; // Redireciona para a página inicial
        } else {
            return "Erro";
        }
    }

    public ModelAndView updadteUser(Long id, @ModelAttribute Usuario usuario) {
        ModelAndView mv = new ModelAndView();
        Optional<Usuario> user = usuarioRepository.findById(id);
        if (user.isPresent()) {
            Usuario usuarioExistente = user.get();
            usuarioExistente.setRole(usuario.getRole());
            usuarioExistente.setNome(usuario.getNome());
            String senhaCriptografada = encoder.encode(usuario.getSenha());
            usuarioExistente.setSenha(senhaCriptografada);
            usuarioExistente.setCpf(usuario.getCpf());

            // Verifica se o CPF foi alterado durante a atualização do usuário
            if (!usuario.getCpf().equals(usuarioExistente.getCpf())) {
                // Se o CPF foi alterado e já existe outro usuário com o novo CPF, redireciona
                // para a página de erro
                if (usuarioRepository.existsByCpf(usuario.getCpf())) {
                    mv.setViewName("usuarios/Edit");
                    mv.addObject("cpfMismatch", true);
                    return mv;
                }
            }
            if (usuario.getSenha().equals("")) {
                mv.setViewName("usuarios/Edit");
                mv.addObject("passwordMismatch", true);
                return mv;
            }

            this.usuarioRepository.save(usuarioExistente);
            List<Usuario> usuarios = this.usuarioRepository.findAll();
            mv.addObject("usuarios", usuarios);
            return new ModelAndView("redirect:/ListaUsuario");
        }
        return null;
    }

    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha,
            UserDto usuarioDto, HttpSession session, Model model) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && encoder.matches(senha, usuario.getSenha())) {
            usuarioDto.setRole(usuario.getRole().getId());

            session.setAttribute("roleId", usuario.getRole().getId());
            Long roleId = (Long) session.getAttribute("roleId");

            if (roleId == 1) {
                return "redirect:/admin";
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

    // estoquista
    public ModelAndView listarTodosPedidos(HttpSession session) {
        ModelAndView mv = new ModelAndView();

        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null || roleId != 2) {
            return new ModelAndView("aviso");
        }

        // Encontrar todos os códigos de pedido distintos
        List<Long> codigosPedidosUnicos = pedidoRealizadoRepository
                .findDistinctByCodigoPedidoIsNotNullOrderByCodigoPedidoDesc()
                .stream()
                .map(PedidoRealizado::getCodigoPedido)
                .distinct()
                .collect(Collectors.toList());

        // Buscar os pedidos associados a esses códigos
        List<PedidoRealizado> todosOsPedidos = pedidoRealizadoRepository
                .findByCodigoPedidoInOrderByDataPedidoDesc(codigosPedidosUnicos);

        if (todosOsPedidos.isEmpty()) {
            return new ModelAndView("usuarios/ListaPedidosVazia");
        }

        mv.setViewName("usuarios/ListaPedido");
        mv.addObject("pedidos", todosOsPedidos);
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
