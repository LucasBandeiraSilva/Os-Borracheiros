package com.borracheiros.projeto.users.userController;

import java.util.List;
import java.util.Optional;

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
import com.borracheiros.projeto.users.UsuarioRepository;
import com.borracheiros.projeto.users.entities.Role;
import com.borracheiros.projeto.users.entities.RoleRepository;
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

    @GetMapping("/ListaUsuario")
    public ModelAndView ListaUsuario(HttpSession session) {

        ModelAndView mv = new ModelAndView();

        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null || !roleId.equals(1L)) {
            mv.setViewName("aviso");
            return mv;
        }

        List<Usuario> usuarios = this.usuarioRepository.findAll();
        mv.setViewName("usuarios/ListaUsuario");
        mv.addObject("usuarios", usuarios);
        return mv;

    }

    @PostMapping("/login")
    public String validation(@RequestParam("email") String email, @RequestParam("senha") String senha,
            UserDto usuarioDto, HttpSession session, Model model) {

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario != null && senha.equals(usuario.getSenha())) {

            System.out.println("roleId: " + usuarioDto.getRole());
            session.setAttribute("roleId", usuario.getRole().getId());

            return "redirect:/";

        }
        model.addAttribute("loginMismatch", true);
        return "index";
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
                return "usuarios/cadastro";
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
    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> optional = this.usuarioRepository.findById(id);

        model.addAttribute("usuario", optional.get());
        return "Edit";
    }

    @GetMapping("usuarios/salvar")
    public String salvarPessoa(@ModelAttribute("usuario") Usuario usuario) {
        this.usuarioRepository.save(usuario);
        return "/ListaUsuario";
    }

    @PostMapping("/ListaUsuario/salvar")
    public String salvarUser(@ModelAttribute("usuario") @Valid Usuario usuario, Model model,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("erro");
            return "Erro";
        }

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
