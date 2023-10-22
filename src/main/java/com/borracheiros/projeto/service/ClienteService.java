package com.borracheiros.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.ClientDto;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public String createUser(ClientDto clientDto, EnderecoDto enderecoDto, BindingResult bindingResult,
            Model model) {

        Cliente cliente = clientDto.toCliente();
        Endereco endereco = enderecoDto.toEndereco();

        if (clientDto.getId() != null) {
            System.out.println("editando.....");
            Optional<Cliente> optionalCliente = clienteRepository.findById(clientDto.getId());

            if (optionalCliente.isPresent()) {
                Cliente clienteExistente = optionalCliente.get();
                // return"Erro";
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
                
                

                clienteRepository.save(clienteExistente);
                //     System.out.println("\n \n \n usuario atualizado.................");
                // endereco.setCliente(clienteExistente); // Atualize o cliente no endereço também
                // enderecoRepository.save(endereco);
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
        return "redirect:/cliente/login";
    }
}
