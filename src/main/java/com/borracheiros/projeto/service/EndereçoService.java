package com.borracheiros.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.borracheiros.projeto.client.Cliente;
import com.borracheiros.projeto.client.endereco.Endereco;
import com.borracheiros.projeto.dto.EnderecoDto;
import com.borracheiros.projeto.repositories.ClienteRepository;
import com.borracheiros.projeto.repositories.EnderecoRepository;

@Service
public class EndereçoService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;

    public String addEndereco(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            Endereco endereco = enderecoDto.toEndereco();

            endereco.setCliente(cliente);

            enderecoRepository.save(endereco);
        }

        return "redirect:/cliente/editar/" + clienteId;
    }

    public String addEnderecoEntrega(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            Endereco endereco = enderecoDto.toEndereco();

            endereco.setCliente(cliente);

            enderecoRepository.save(endereco);
            return "redirect:/cliente/AdicionaEnderecoEntrega/";
        }
        return null;
    }

    // caso o cliente clicque para adicionar mais um endereço no checkou esse metodo
    // é acionado
    public String addEnderecoEntregaCheckout(@ModelAttribute("enderecoDto") EnderecoDto enderecoDto,
            @RequestParam("clienteId") Long clienteId) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            Endereco endereco = enderecoDto.toEndereco();

            endereco.setCliente(cliente);

            enderecoRepository.save(endereco);
            return "redirect:/cliente/endereco/selecionar/" + cliente.getId();
        }
        return null;
    }

    public ModelAndView addEntrega(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            Endereco endereco = new Endereco();
            endereco.setCliente(cliente);
            mv.addObject("endereco", endereco);
            mv.addObject("clienteId", cliente.getId()); 
            mv.setViewName("clientes/AdicionaEnderecoEntrega");
            return mv;
        }

        return mv;
    }

    public ModelAndView updateEndereco(@PathVariable("id") Long id, Model model) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            Endereco endereco = new Endereco();
            endereco.setCliente(cliente);
            mv.addObject("endereco", endereco);
            mv.addObject("clienteId", cliente.getId()); // Adicione o clienteId ao modelo
            mv.setViewName("clientes/adicionarEndereco");
            return mv;
        }

        return mv;
    }

    public ModelAndView updateEnderecoCheckout(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            Endereco endereco = new Endereco();
            endereco.setCliente(cliente);
            mv.addObject("endereco", endereco);
            mv.addObject("clienteId", cliente.getId()); // Adicione o clienteId ao modelo
            mv.setViewName("clientes/addEnderecoCheckout");
            return mv;
        }

        return mv;
    }

    public String deletarEnderecoPorId(@PathVariable Long id) {

        Optional<Endereco> optional = this.enderecoRepository.findById(id);
        if (optional.isPresent()) {
            Endereco endereco = optional.get();
            Cliente cliente = endereco.getCliente();
            if (cliente.getEnderecos().size() == 1) {
                return "clientes/AvisoEndereco";
            }
            enderecoRepository.deleteById(id);
            return "redirect:/cliente/editar/" + cliente.getId();
        }
        return null;
    }

    public ModelAndView definirEnderecoPadrao(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView();
        Optional<Endereco> optional = this.enderecoRepository.findById(id);
        if (optional.isPresent()) {
            Endereco endereco = optional.get();
            Cliente cliente = endereco.getCliente();
            if (endereco.getCliente().getId() != cliente.getId()) {
                throw new IllegalArgumentException("O endereço não pertence ao usuário.");
            } else {

                for (Endereco e : cliente.getEnderecos()) {
                    if (e.isEnderecoPadrao()) {
                        e.setEnderecoPadrao(false);
                    }
                    /*
                     * verifica se o cliente ja possui um endereço como padrão,
                     * se sim o desativa para a definição de outro como padrão
                     */
                }

                endereco.setEnderecoPadrao(true);
                enderecoRepository.save(endereco);
                mv.setViewName("redirect:/cliente/editar/" + cliente.getId());
                return mv;
            }
        }
        return null;
    }
}
