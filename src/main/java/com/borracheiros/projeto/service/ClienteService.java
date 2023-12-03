package com.borracheiros.projeto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.carrinho.Carrinho;
import com.borracheiros.projeto.carrinho.CarrinhoNaoAutenticado;
import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.repositories.CarrinhoNaoAutenticadoRepository;
import com.borracheiros.projeto.repositories.CarrinhoRepository;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private CarrinhoNaoAutenticadoRepository carrinhoNaoAutenticadoRepository;

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

    @Transactional
    public String validacaoLogin(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente != null && encoder.matches(senha, cliente.getSenha())) {

            // Recupere todos os carrinhos não autenticados
            List<CarrinhoNaoAutenticado> carrinhosNaoAutenticados = carrinhoNaoAutenticadoRepository.findAll();

            for (CarrinhoNaoAutenticado carrinhoNaoAutenticado : carrinhosNaoAutenticados) {
                // Para cada carrinho não autenticado, crie um novo Carrinho e transfira as
                // informações
                Carrinho carrinho = new Carrinho();
                carrinho.setCliente(cliente);
                carrinho.setEstoques(new ArrayList<>(carrinhoNaoAutenticado.getEstoques()));
                carrinho.setQuantidade(carrinhoNaoAutenticado.getQuantidade());
                carrinho.setPreco(carrinhoNaoAutenticado.getPreco());
                carrinho.setNome(carrinhoNaoAutenticado.getNome());

                // Salve o carrinho no banco de dados
                carrinhoRepository.save(carrinho);

                // Remova o carrinho não autenticado do banco de dados
                carrinhoNaoAutenticadoRepository.deleteAll();
            }

            // Restante do código para cliente autenticado
            session.setAttribute("cliente", cliente);
            session.removeAttribute("carrinhoNaoAutenticadoID"); // Remova o ID do carrinho da sessão
            session.setAttribute("nomeUsuario", cliente.getNome());
            return "redirect:/cliente/logado";
        } else {
            model.addAttribute("loginMismatch", true);
            return "clientes/LoginCliente";
        }
    }

    public ModelAndView selecionarEndereco(@PathVariable Long id) {

        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            ModelAndView mv = new ModelAndView("clientes/EnderecoCheckout");
            mv.addObject("cliente", cliente);
            mv.addObject("carrinhos", cliente.getCarrinho());
            return mv;
        }

        return new ModelAndView("erro");
    }

}
