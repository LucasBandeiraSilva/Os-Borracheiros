package com.borracheiros.projeto.users;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller

public class UserController {
    
     @GetMapping("/")
     public String home(){
        return "TelaPrincipal";
     }

    @GetMapping("/cadastro")
    public String showCreateUserPage(){
        return "cadastro";
    }
    

    
}
