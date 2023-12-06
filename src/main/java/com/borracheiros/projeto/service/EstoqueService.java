package com.borracheiros.projeto.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.dto.EstoqueDto;
import com.borracheiros.projeto.estoque.entities.Estoque;
import com.borracheiros.projeto.repositories.EstoqueRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class EstoqueService {

    @Autowired
    EstoqueRepository estoqueRepository;

    public String cadastraProduto(HttpSession session) {
        Long roleId = (Long) session.getAttribute("roleId");

        if (roleId == null || roleId == 2) {
            return "aviso";
        }
        return "produtos/CadastraProduto";
    }

    public ModelAndView mostrarProdutos(HttpSession session) {

        Long roleId = (Long) session.getAttribute("roleId");
        if (roleId == null || roleId == 3) {
            return new ModelAndView("aviso");
        }
        ModelAndView mv = new ModelAndView();
        List<Estoque> produtos = this.estoqueRepository.findAll();

        mv.setViewName("produtos/ListaProduto");
        mv.addObject("produtos", produtos);
        return mv;
    }

    public String criarProduto(@Valid EstoqueDto produtoDTO, BindingResult bindingResult,
            @RequestParam("fileProduto") List<MultipartFile> files, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("form com erros");
            System.out.println(bindingResult.getAllErrors());
            return "Erro";
        }

        try {
            List<byte[]> imagens = new ArrayList<>();
            for (MultipartFile file : files) {
                imagens.add(file.getBytes());
            }
            produtoDTO.setImagem(imagens);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Estoque estoque = produtoDTO.toEstoque();
        System.out.println(estoque);
        estoqueRepository.save(estoque);

        return "redirect:/listasProdutos";
    }

    public byte[] exibirImagem(Model model, @PathVariable("id") long id) {
        Estoque estoque = this.estoqueRepository.getReferenceById(id);
        return estoque.getImagem();
    }

    public ModelAndView obterProdutoPorId(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        // Estoque produto = estoqueRepository.findById(id).orElse(null);

        // mv.setViewName("produtos/VisualizarProduto");
        // return mv.addObject("produto", produto);
        Optional<Estoque> estoque = estoqueRepository.findById(id);
        if (estoque.isPresent()) {
            mv.addObject("produto", estoque.get());
            mv.setViewName("produtos/VisualizarProduto");
            return mv;
        }
        return null;

    }

    public ModelAndView editarPedido(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Estoque> optional = this.estoqueRepository.findById(id);
        if (optional.isPresent()) {
        mv.addObject("estoque", optional.get()); 
        mv.setViewName("produtos/EditProduto"); 
        return mv;

        }
        return null;

    }

    public ModelAndView salvarEstoque(@ModelAttribute("estoque") Estoque estoque,
            BindingResult bindingResult, @RequestParam("fileProduto") MultipartFile file) {
        ModelAndView mv = new ModelAndView();

        if (estoque.getQuantidadeEstoque() == null || estoque.getQuantidadeEstoque() <= 0) {
            mv.addObject("estoqueError", true);
            return new ModelAndView("produtos/EditProduto");

        }
        if (estoque.getPreco() == null || estoque.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            mv.addObject("precoError", true);
            return new ModelAndView("produtos/EditProduto");

        }
        if (file == null || file.isEmpty()) {
            mv.addObject("imgError", true);
            return new ModelAndView("produtos/EditProduto");
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
        return new ModelAndView("redirect:/listasProdutos");
    }

}
