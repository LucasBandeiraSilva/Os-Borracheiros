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

    public ModelAndView edicaoCliente(@PathVariable Long ClienteId, @ModelAttribute("clientDto") ClientDto clientDto) {
        Optional<Cliente> optionalCliente = this.clienteRepository.findById(ClienteId);
        if (optionalCliente.isPresent()) {
            Cliente cliente = clientDto.toCliente();

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

            return new ModelAndView("redirect:/cliente/login");

        }
        return null;

    }

    public ModelAndView createUser(@Valid @ModelAttribute("clientDto") ClientDto clientDto,
            EnderecoDto enderecoDto,
            BindingResult bindingResult,
            @RequestParam("enderecoFaturamento") boolean enderecoFaturamento) {

        ModelAndView mv = new ModelAndView();
        Cliente cliente = clientDto.toCliente();
        Endereco endereco = enderecoDto.toEndereco();

        if (clienteRepository.existsByCpfOrEmail(cliente.getCpf(), cliente.getEmail())) {
            if (clienteRepository.existsByCpf(cliente.getCpf())) {
                mv.addObject("cpfMismatch", true);
            }
            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                mv.addObject("emailMismatch", true);
            }
            return new ModelAndView("clientes/CadastroCliente");
        }

        String senhaCriptografada = encoder.encode(cliente.getSenha());
        cliente.setSenha(senhaCriptografada);

        clienteRepository.save(cliente);
        endereco.setCliente(cliente);
        if (enderecoFaturamento) {
            endereco.setEnderecoFaturamento(endereco.getLogradouro());
        } else {
            endereco.setEnderecoFaturamento("Sem Faturamento");
        }
        enderecoRepository.save(endereco);

        return new ModelAndView("redirect:/cliente/login");
    }

    public ModelAndView alterar(Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> optional = this.clienteRepository.findById(id);
        if (optional.isPresent()) {
            mv.addObject("cliente", optional.get());
            mv.setViewName("clientes/EditCliente");  // Configura a view correta
            return mv;  // Retorna o ModelAndView configurado
        }
        return new ModelAndView("Erro");
    }
    

    @Transactional
    public String validacaoLogin(@RequestParam("email") String email, @RequestParam("senha") String senha,
            HttpSession session, Model model) {

        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente != null && encoder.matches(senha, cliente.getSenha())) {

            // Recupere todos os carrinhos não autenticados
            List<CarrinhoNaoAutenticado> carrinhosNaoAutenticados = carrinhoNaoAutenticadoRepository.findAll();

            if (!carrinhosNaoAutenticados.isEmpty()) {
                // verifica se o carrinho ta vazio, se tiver pega o primeiro (validação feita
                // para o cep)
                CarrinhoNaoAutenticado primeiroCarrinho = carrinhosNaoAutenticados.get(0);

                for (CarrinhoNaoAutenticado carrinhoNaoAutenticado : carrinhosNaoAutenticados) {
                    // Para cada carrinho não autenticado, crie um novo Carrinho e transfira as
                    // informações
                    Carrinho carrinho = new Carrinho();
                    carrinho.setCliente(cliente);
                    carrinho.setEstoques(new ArrayList<>(carrinhoNaoAutenticado.getEstoques()));
                    carrinho.setQuantidade(carrinhoNaoAutenticado.getQuantidade());
                    carrinho.setPreco(carrinhoNaoAutenticado.getPreco());
                    carrinho.setFrete(primeiroCarrinho.getFrete());
                    carrinho.setNome(carrinhoNaoAutenticado.getNome());

                    // Salve o carrinho no banco de dados
                    carrinhoRepository.save(carrinho);
                }

                // Remova todos os carrinhos não autenticados do banco de dados
                carrinhoNaoAutenticadoRepository.deleteAll();

                session.setAttribute("cliente", cliente);
                session.removeAttribute("carrinhoNaoAutenticadoID"); // Remove o ID do carrinho da sessão
                session.setAttribute("nomeUsuario", cliente.getNome());
                return "redirect:/cliente/logado";
            } else {
                // Lista de carrinhos vazia
                session.setAttribute("cliente", cliente);
                session.setAttribute("nomeUsuario", cliente.getNome());
                return "redirect:/cliente/logado";
            }
            // senha e/ou email incorretos
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
