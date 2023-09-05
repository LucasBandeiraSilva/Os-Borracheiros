package com.borracheiros.projeto.users;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/login")
    public String login() {
        return "index";
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

    @PostMapping("/login")
    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null) {

            if (senha.equals(usuario.getSenha())) {

                return "redirect:/";
            }
        }

        return null;
    }

    @PostMapping("/ListaUsuario")
    public String createUser(@Valid UserDto usuarioDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Formulário com erros");
            return "Erro"; // Substitua pelo nome da sua página
        } else {
            Usuario usuario = usuarioDto.toUsuario();

            Long roleId = usuarioDto.getRole();

            // Procura a função (role) com base no ID fornecido
            Role role = roleRepository.findById(roleId).orElse(null);

            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())
                    || usuarioRepository.existsByCpf(usuarioDto.getCpf())) {
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

    @GetMapping("/usuarios/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model){
        Optional <Usuario> optional = this.usuarioRepository.findById(id);
        // if(optional.isEmpty()){
        //     return "erro";
        // }
        model.addAttribute("usuario", optional.get());
        return "Edit";
    }

    @GetMapping("usuarios/salvar")
    public String salvarPessoa(@ModelAttribute("usuario") Usuario usuario){
        this.usuarioRepository.save(usuario);
        return "redirect:/ListaUsuario";
    }

    @PostMapping("/ListaUsuario/salvar")
    public String salvarUser(@ModelAttribute("usuario") Usuario usuario, Model model) {
    
       
    
        // Verifique se o CPF é nulo ou vazio
        if (usuario.getCpf() == null || usuario.getCpf().isEmpty()) {
            model.addAttribute("cpfNull", true);
            return "Edit";
        }
    
        // Verifique se a senha é nula ou vazia
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            model.addAttribute("emailNull", true);
            return "Edit";
        }
        if (usuario.getDataNascimento() == null) {
            model.addAttribute("dataNull", true);
            return "Edit";
        }
    
        // Verifique se o CPF já existe no banco de dados
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            model.addAttribute("cpfMismatch", true);
            return "Edit";
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            model.addAttribute("EmailMismatch", true);
            return "Edit";
        }
    
        // Se todas as verificações passarem, salve o usuário e redirecione
        this.usuarioRepository.save(usuario);
        return "redirect:/ListaUsuario";
    }
    
}
