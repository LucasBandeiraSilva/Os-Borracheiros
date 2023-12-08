package com.borracheiros.projeto.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.service.UsuarioService;
import com.borracheiros.projeto.users.entities.Usuario;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    UserDto userDto = new UserDto();

    @Autowired
    private UsuarioService usuarioService;

    //listar todos os usuarios
    @GetMapping("/ListaUsuario")
    public ModelAndView ListaUsuario(HttpSession session) {

        return usuarioService.ListaUsuario(session);
    }

    //rota estoquista
    @GetMapping("/estoquista")
    public String telaEstoquista(HttpSession session) {
        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null) {
            return "aviso";
        } else if (roleId == 2) {

            return "usuarios/TelaEstoquista";
        }
        return null;
    }

    // rota admin
    @GetMapping("/admin")
    public String telaAdmin(HttpSession session) {
        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null) {
            return "aviso";
        } else if (roleId == 1) {

            return "usuarios/TelaAdmin";
        }
        return null;
    }

    //rota do form de login
    @PostMapping("/login")
    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha,
            UserDto usuarioDto, HttpSession session, Model model) {

        return usuarioService.validation(email, senha, usuarioDto, session, model);
    }

    //rota do form de criação
    @PostMapping("/ListaUsuario")
    public String createUser(@Valid UserDto usuarioDto, BindingResult bindingResult, Model model) {

        return usuarioService.createUser(usuarioDto, bindingResult, model);
    }

    // salvr a alteração do usuario
    @PostMapping("usuario/{id}")
    public ModelAndView updateUser(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        return usuarioService.updadteUser(id, usuario);
    }

    //pagina de alterar o usuario
    @GetMapping("/usuarios/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        return usuarioService.alterar(id, model);
    }

    // para atualizar o status do usuario
    @PostMapping("/usuario/status")
    @ResponseBody
    public void status(@RequestParam("id") Long id, @RequestParam("UsuarioStatus") boolean usuarioStatus) {
        usuarioService.status(id, usuarioStatus);
    }

    //lista todos os pedidos
    @GetMapping("/pedidos")
    public ModelAndView pedido(HttpSession session) {
        return usuarioService.listarTodosPedidos(session);
    }

    // formulario de edição do status
    @GetMapping("/editarStatus/{id}")
    public ModelAndView editarStatus(@PathVariable Long id) {
        return usuarioService.editarStatusPedido(id);
    }
        //editar status do pedido
    @PostMapping("/editar/pedido/{id}")
    public ModelAndView updateStatus(@PathVariable Long id, @RequestParam("StatusPagamento") String StatusPagamento) {
        return usuarioService.editarPedido(StatusPagamento, id);
    }
}