package com.borracheiros.projeto.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.RoleRepository;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.validation.Valid;

@Controller

public class UserController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String home() {
        return "TelaPrincipal";
    }

    @GetMapping("/cadastro")
    public String showCreateUserPage() {
        return "cadastro";
    }

    @GetMapping("/ListaUsuario")
    public ModelAndView ListaUsuario() {
        List<Usuario> usuarios = this.usuarioRepository.findAll();
        ModelAndView mv = new ModelAndView("ListaUsuario");
        mv.addObject("usuarios", usuarios);
        return mv;

    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @PostMapping("/login")
    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null) {

            if (senha.equals(usuario.getSenha())) {

                return "redirect:/ListaUsuario";
            }
        }

        return null;
    }

    @PostMapping("/ListaUsuario")
    public String createUser(@Valid UserDto userDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Formulário com erros");
            return "Erro"; // Substitua pelo nome da sua página
        } else {
            Usuario usuario = userDto.toUsuario();

            Long roleId = userDto.getRole();

            // Procura a função (role) com base no ID fornecido
            Role role = roleRepository.findById(roleId).orElse(null);

            if (!userDto.getSenha().equals(userDto.getConfirmPassword())
                    || usuarioRepository.existsByCpf(userDto.getCpf())) {
                model.addAttribute("passwordMismatch", true);
                model.addAttribute("cpfMismatch", true);
                return "cadastro";
            }

            if (role != null) {

                usuario.setRole(role);

                usuarioRepository.save(usuario);

                return "redirect:/ListaUsuario";
            }
            return "Erro";
        }

    }

}
