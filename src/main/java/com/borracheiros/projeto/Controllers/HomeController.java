package com.borracheiros.projeto.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
        return "cadastro";
    }

    @GetMapping("/sair")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Sair";
    }
}
