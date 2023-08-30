package com.borracheiros.projeto.users;


import com.borracheiros.projeto.users.entities.Usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller

public class UserController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
     @GetMapping("/")
     public String home(){
        return "TelaPrincipal";
     }

    @GetMapping("/cadastro")
    public String showCreateUserPage(){
        return "cadastro";
    }
    
    @GetMapping("/ListaUsuario")
    public ModelAndView ListaUsuario(){
        List<Usuario> usuarios = this.usuarioRepository.findAll();
       ModelAndView mv = new ModelAndView("ListaUsuario");
       mv.addObject("usuarios", usuarios);
       return mv;
        
    }

    
}
