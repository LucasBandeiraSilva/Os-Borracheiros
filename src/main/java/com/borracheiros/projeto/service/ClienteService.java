package com.borracheiros.projeto.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String createUser(@Valid @ModelAttribute("clientDto") ClientDto clientDto, EnderecoDto enderecoDto,
            BindingResult bindingResult,
            Model model) {

        Cliente cliente = clientDto.toCliente();
        Endereco endereco = enderecoDto.toEndereco();

        if (clientDto.getId() != null) {
            System.out.println("editando.....");
            Optional<Cliente> optionalCliente = clienteRepository.findById(clientDto.getId());

            if (optionalCliente.isPresent()) {
                Cliente clienteExistente = optionalCliente.get();
                if (cliente.getCpf() != null && !cliente.getCpf().isEmpty()) {
                    clienteExistente.setCpf(cliente.getCpf());
                }
                if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
                    clienteExistente.setEmail(cliente.getEmail());
                }
                if (cliente.getNome() != null && !cliente.getNome().isEmpty()) {
                    clienteExistente.setNome(cliente.getNome());
                }
                if (cliente.getSenha() != null && !cliente.getSenha().isEmpty()) {
                    String senhaAtualizada = encoder.encode(cliente.getSenha());
                    clienteExistente.setSenha(senhaAtualizada);

                }

                clienteExistente.setDataAniversario(cliente.getDataAniversario());

                System.out.println(cliente.getDataAniversario());

                clienteRepository.save(clienteExistente);

                return "redirect:/cliente/login";

            }
        }

        if (clienteRepository.existsByCpf(cliente.getCpf()) || clienteRepository.existsByEmail(cliente.getEmail())) {
            if (clienteRepository.existsByCpf(cliente.getCpf())) {
                model.addAttribute("cpfMismatch", true);
            }
            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                model.addAttribute("emailMismatch", true);
            }
            return "clientes/CadastroCliente";
        }

        String senhaCriptografada = encoder.encode(cliente.getSenha());
        cliente.setSenha(senhaCriptografada);

        clienteRepository.save(cliente);
        endereco.setCliente(cliente);
        enderecoRepository.save(endereco);
        System.out.println(cliente.getDataAniversario());
        return "redirect:/cliente/login";
    }

    public String alterar(@PathVariable("id") Long id, Model model) {
        Optional<Cliente> optional = this.clienteRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("cliente", optional.get());
            return "clientes/EditCliente";
        }
        return "Erro";
    }

    public String validacaoLogin(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente != null && encoder.matches(senha, cliente.getSenha())) {
            Long carrinhoNaoAutenticadoID = (Long) session.getAttribute("carrinhoNaoAutenticadoID");

            if (carrinhoNaoAutenticadoID != null) {
                System.out.println("vOU SALVAR O CARRINHO!");
                // Recupere o carrinho não autenticado pelo ID
                Carrinho carrinhoNaoAutenticado = carrinhoRepository.findById(carrinhoNaoAutenticadoID).orElse(null);

                if (carrinhoNaoAutenticado != null) {
                    // Defina o cliente associado ao carrinho como o cliente logado
                    carrinhoNaoAutenticado.setCliente(cliente);

                    // Salve o carrinho atualizado no banco de dados
                    carrinhoRepository.save(carrinhoNaoAutenticado);

                    // Remova o ID do carrinho não autenticado da sessão
                    session.removeAttribute("carrinhoNaoAutenticadoID");
                }
            }
            System.out.println("vOU SALVAR O CARRINHO!");

            session.setAttribute("idCliente", cliente.getId());
            session.setAttribute("cliente", cliente);
            session.setAttribute("nomeUsuario", cliente.getNome());
            System.out.println("ID do cliente: " + cliente.getId());
            System.out.println("Nome do cliente: " + cliente.getNome());
            return "redirect:/cliente/logado";
        } else {
            model.addAttribute("loginMismatch", true);
            return "clientes/LoginCliente";
        }
    }
}
