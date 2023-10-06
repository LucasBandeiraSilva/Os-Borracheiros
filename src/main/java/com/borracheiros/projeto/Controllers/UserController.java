package com.borracheiros.projeto.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.repositories.RoleRepository;
import com.borracheiros.projeto.repositories.UsuarioRepository;
import com.borracheiros.projeto.service.UsuarioService;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller

public class UserController {

    UserDto userDto = new UserDto();
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ListaUsuario")
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

    @GetMapping("/estoquista")
    public String telaEstoquista() {
        return "usuarios/TelaEstoquista";
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

    @PostMapping("/ListaUsuario")
    public String createUser(@Valid UserDto usuarioDto, BindingResult bindingResult, Model model) {

        Usuario usuario = usuarioDto.toUsuario();
        Long roleId = usuarioDto.getRole();
        Role role = roleRepository.findById(roleId).orElse(null);

        if (usuarioDto.getId() != null) {
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioDto.getId());

            // Se o usuário existir, atualize os campos com os valores do formulário
            if (optionalUsuario.isPresent()) {
                Usuario usuarioExistente = optionalUsuario.get();

                usuarioExistente.setRole(role);

                if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                    usuarioExistente.setEmail(usuario.getEmail());
                }
                if (usuario.getNome() != null && !usuario.getNome().isEmpty()) {
                    usuarioExistente.setNome(usuario.getNome());
                }
                if (usuario.getCpf() != null && !usuario.getCpf().isEmpty()) {
                    usuarioExistente.setCpf(usuario.getCpf());

                }
                if(usuario.getSenha() != usuarioDto.getConfirmPassword()){
                    return "Erro";
                }

                if (usuario.getStatusUsuario() != null) {
                    usuarioExistente.setStatusUsuario(usuario.getStatusUsuario());
                }
                if (!usuarioExistente.getCpf().equals(usuario.getCpf())) {
                    if (usuarioRepository.existsByCpf(usuario.getCpf())) {
                        return "usuarios/ErroCPF";
                    }
                }
                usuarioRepository.save(usuarioExistente);
                return "redirect:/";
            }

        }

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
            return "redirect:/"; // Redireciona para a página inicial
        }
        return null;
    }

    @GetMapping("/usuarios/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> optional = this.usuarioRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("usuario", optional.get());
            return "usuarios/Edit";
        }
        return "Erro";
    }

    // para atualizar o status do usuario
    @PostMapping("/usuario/status")
    @ResponseBody
    public void status(@RequestParam("id") Long id, @RequestParam("UsuarioStatus") boolean usuarioStatus) {
        usuarioService.status(id, usuarioStatus);
    }
}