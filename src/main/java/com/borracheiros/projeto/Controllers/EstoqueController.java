package com.borracheiros.projeto.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.EstoqueRepository;
import com.borracheiros.projeto.service.EstoqueService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class EstoqueController {
    UserDto userDto = new UserDto();

    @Autowired
    EstoqueRepository estoqueRepository;

    @Autowired
    EstoqueService estoqueService;

    @GetMapping("produto/cadastro")
    public String cadastroProduto(HttpSession session) {
        return estoqueService.cadastraProduto(session);
    }

    @GetMapping("/listasProdutos")
    public ModelAndView mostrarProdutos(HttpSession session) {

        return estoqueService.mostrarProdutos(session);

    }

    @PostMapping("/listasProdutos")
    public String criarProduto(@Valid EstoqueDto produtoDTO, BindingResult bindingResult,
            @RequestParam("fileProduto") List<MultipartFile> files, Model model) {

        return estoqueService.criarProduto(produtoDTO, bindingResult, files, model);
    }

    @GetMapping("/imagem/{id}")
    @ResponseBody
    public byte[] exibirImagem(Model model, @PathVariable("id") long id) {
        return estoqueService.exibirImagem(model, id);
    }

    @GetMapping("/produto/{id}")
    @ResponseBody
    public ModelAndView obterProdutoPorId(@PathVariable Long id) {
        return estoqueService.obterProdutoPorId(id);
    }

    @GetMapping("/produto/editar/{id}")
    public ModelAndView alterar(@PathVariable("id") Long id) {
        
        return estoqueService.editarPedido(id);
    }

    @PostMapping("produto/salvar")
    public ModelAndView salvarProduto(@ModelAttribute("estoque") Estoque estoque, Model model,
            BindingResult bindingResult, @RequestParam("fileProduto") MultipartFile file) {

       return estoqueService.salvarProduto(estoque, bindingResult, file);
    }
}
