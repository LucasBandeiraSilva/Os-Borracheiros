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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.service.UsuarioService;
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
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ListaUsuario")
    public ModelAndView ListaUsuario() {

        ModelAndView mv = new ModelAndView();

        // Long roleId = (Long) session.getAttribute("roleId");

        // if (roleId == null || !roleId.equals(1L)) {
        // mv.setViewName("usuarios/TelaEstoquista");
        // return mv;
        // }

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

        // if (usuario != null && senha.equals(usuario.getSenha())) {

        System.out.println("roleId: " + usuarioDto.getRole());
        session.setAttribute("roleId", usuario.getRole().getId());
        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == 1) {

            return "redirect:/";
        } else if (roleId == 2) {
            return "redirect:/estoquista";
        }

        model.addAttribute("loginMismatch", true);
        return "index";
    }

    @PostMapping("/ListaUsuario")
    public String createUser(@Valid UserDto usuarioDto, BindingResult bindingResult, Model model) {

         Usuario usuario = usuarioDto.toUsuario();
        Long roleId = usuarioDto.getRole();
        Role role = roleRepository.findById(roleId).orElse(null);

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
                bindingResult.rejectValue("cpf", "error.userDto", "CPF já cadastrado");
                
            }

            if (usuario.getCpf() == null || usuario.getCpf().isEmpty()) {
                bindingResult.rejectValue("cpf", "error.userDto", "Campo CPF é obrigatório");
                
            }

        if (bindingResult.hasErrors()) {
            System.out.println("Formulário com erros");
            System.out.println(bindingResult.getAllErrors());
            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.userDto", "As senhas não coincidem");
            }
            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.userDto", "As senhas não coincidem");
            }
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                bindingResult.rejectValue("email", "error.userDto", "E-mail já cadastrado");
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

        model.addAttribute("usuario", optional.get());
        return "usuarios/Edit";
    }

    @GetMapping("usuarios/salvar")
    public String salvarPessoa(@ModelAttribute("usuario") Usuario usuario) {
        this.usuarioRepository.save(usuario);
        return "/ListaUsuario";
    }

    @PostMapping("/ListaUsuario/salvar")
    public String salvarUser(@ModelAttribute("usuario") @Valid Usuario usuario, Model model,
            BindingResult bindingResult, @RequestParam("StatusUsuario") String statusUsuario) {

        if (bindingResult.hasErrors()) {
            System.out.println("erro");
            return "Erro";
        }

        if (usuario.getCpf() == null || usuario.getCpf().isEmpty()) {
            model.addAttribute("cpfNull", true);
            return "usuarios/Edit";
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            model.addAttribute("cpfMismatch", true);
            bindingResult.rejectValue("cpf", "error.userDto", "CPF já cadastrado");

            return "usuarios/Edit";
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            model.addAttribute("EmailMismatch", true);
            return "usuarios/Edit";
        }

        // Se todas as verificações passarem, salve o usuário e redirecione
        this.usuarioRepository.save(usuario);
        return "redirect:/ListaUsuario";
    }

    // para atualizar o status do usuario
    @PostMapping("/usuario/status")
    @ResponseBody
    public void status(@RequestParam("id") Long id, @RequestParam("UsuarioStatus") boolean usuarioStatus) {
        usuarioService.status(id, usuarioStatus);
    }
}