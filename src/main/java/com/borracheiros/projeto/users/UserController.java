package com.borracheiros.projeto.users;

import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.Usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class UserController {
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @PostMapping("/ListaUsuario")
    public String Create(UserDto userDto) {
        Long roleId = userDto.getRole(); // Valor selecionado no formulário
         Usuario usuario = userDto.toUsuario();

        if (roleId != null) {
            Role role;

            if (roleId == 1) {
                role = new Role(1L); // Crie um objeto Role correspondente a "ADMIN"
            } else if (roleId == 2) {
                role = new Role(2L); // Crie um objeto Role correspondente a "Estoquista"
            }
        }
        this.usuarioRepository.save(usuario);
        return "redirect:/ListaUsuario";

    }
}
