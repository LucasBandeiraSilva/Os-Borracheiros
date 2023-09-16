package com.borracheiros.projeto.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.UserDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "TelaPrincipal";
    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @GetMapping("/cadastro")
    public String criarUsuario() {
        return "usuarios/cadastro";
    }

    @GetMapping("/sair")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Sair";
    }

    @GetMapping("/aviso")
    public ModelAndView Aviso() {

        ModelAndView mv = new ModelAndView();

        mv.setViewName("aviso");
        return mv;

    }
}
