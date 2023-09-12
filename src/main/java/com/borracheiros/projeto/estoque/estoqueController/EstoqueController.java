package com.borracheiros.projeto.estoque.estoqueController;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.EstoqueDto;
import com.borracheiros.projeto.estoque.EstoqueRepository;
import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.validation.Valid;

@Controller
public class EstoqueController {

    @Autowired
    EstoqueRepository estoqueRepository;

    @GetMapping("produto/cadastro")
    public String cadastroProduto() {
        return "CadastraProduto";
    }

    @GetMapping("/listasProdutos")
    public ModelAndView showProducts() {
        ModelAndView mv = new ModelAndView();
        List<Estoque> produtos = this.estoqueRepository.findAll();

        mv.setViewName("ListaProduto");
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

        DecimalFormat df = new DecimalFormat("#,00");
        String precoFormatado = df.format(produtoDTO.getPreco());
        produtoDTO.setPreco(new BigDecimal(precoFormatado));

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
    public byte [] exibirImagem(Model model, @PathVariable("id") long id){
        Estoque estoque = this.estoqueRepository.getOne(id);
        System.out.println("a imagem é " + estoque.getImagem());
        return estoque.getImagem();
    }

    @GetMapping("/produto/{id}")
    @ResponseBody
    public Estoque obterProdutoPorId(@PathVariable Long id) {
        return estoqueRepository.findById(id).orElse(null);
    }
    }


