package com.borracheiros.projeto.estoque.estoqueController;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.EstoqueDto;
import com.borracheiros.projeto.dto.UserDto;
import com.borracheiros.projeto.estoque.EstoqueRepository;
import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class EstoqueController {
    UserDto userDto = new UserDto();

    @Autowired
    EstoqueRepository estoqueRepository;

    @GetMapping("produto/cadastro")
    public String cadastroProduto(HttpSession session) {
        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null) {
            return "aviso";
        }
        return "produtos/CadastraProduto";
    }

    @GetMapping("/listasProdutos")
    public ModelAndView showProducts() {
        ModelAndView mv = new ModelAndView();
        List<Estoque> produtos = this.estoqueRepository.findAll();

        mv.setViewName("produtos/ListaProduto");
        mv.addObject("produtos", produtos);
        return mv;

    }

    @PostMapping("/listasProdutos")
    public String createProduct(@Valid EstoqueDto produtoDTO, BindingResult bindingResult,
            @RequestParam("fileProduto") MultipartFile file, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("form com erros");
            System.out.println(bindingResult.getAllErrors());
            return "Erro";
        }

        try {
            produtoDTO.setImagem(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Estoque estoque = produtoDTO.toEstoque();
        System.out.println(estoque);
        estoqueRepository.save(estoque);

        return "redirect:/listasProdutos";
    }

    @GetMapping("/imagem/{id}")
    @ResponseBody
    public byte[] exibirImagem(Model model, @PathVariable("id") long id) {
        Estoque estoque = this.estoqueRepository.getOne(id);
        return estoque.getImagem();
    }

    @GetMapping("/produto/{id}")
    @ResponseBody
    public ModelAndView obterProdutoPorId(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Estoque produto = estoqueRepository.findById(id).orElse(null);
        mv.setViewName("produtos/VisualizarProduto");
        return mv.addObject("produto", produto);

    }

    @GetMapping("/produto/editar/{id}")
    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Estoque> optional = this.estoqueRepository.findById(id);

        model.addAttribute("estoque", optional.get());
        return "produtos/EditProduto";
    }

    @PostMapping("produto/salvar")
    public String salvarUser(@ModelAttribute("estoque") Estoque estoque, Model model,
            BindingResult bindingResult, @RequestParam("fileProduto") MultipartFile file) {

        if (estoque.getQuantidadeEstoque() == null || estoque.getQuantidadeEstoque() <= 0) {
            model.addAttribute("estoqueError", true);
            return "produtos/EditProduto";

        }
        if ( estoque.getPreco() == null||estoque.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("precoError", true);
            return "produtos/EditProduto";

        }
        if (file == null ||file.isEmpty()) {
            model.addAttribute("imgError", true);
            return "produtos/EditProduto";

        }
        try {

            if (!file.isEmpty()) {
                byte[] imagemBytes = file.getBytes();
                estoque.setImagem(imagemBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.estoqueRepository.save(estoque);
        return "redirect:/listasProdutos";
    }
}
