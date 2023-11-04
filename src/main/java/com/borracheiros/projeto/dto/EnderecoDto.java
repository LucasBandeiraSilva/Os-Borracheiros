package com.borracheiros.projeto.dto;

import com.borracheiros.projeto.client.endereco.Endereco;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EnderecoDto {
    private int cep;
    private String logradouro;
    private String complemento;
    private String Bairro;
    private int numero;
    private String cidade;
    private String endereco;
    private boolean statusEndereco;
    private boolean enderecoPadrao;
    private String tipoEndereco;


    public Endereco toEndereco(){
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setLogradouro(this.logradouro);
        endereco.setComplemento(this.complemento);
        endereco.setBairro(this.Bairro);
        endereco.setNumero(this.numero);
        endereco.setCidade(this.cidade);
        endereco.setEndereco(this.endereco);
        endereco.setStatusEndereco(this.statusEndereco);
        endereco.setEnderecoPadrao(this.enderecoPadrao);
        endereco.setTipoEndereco(this.tipoEndereco);
        return endereco;
    }
    // public void fromEndereco(Endereco endereco){
    //     this.cep = getCep();
    //     this.logradouro = getLogradouro();
    //     this.complemento = getComplemento();
    //     this.Bairro = getBairro();
    //     this.numero = getNumero();
    //     this.cidade = getCidade();
    //     this.endereco = getEndereco();
    //     this.statusEndereco = isStatusEndereco();
    //     this.enderecoPadrao = getEnderecoPadrao();
    //     this.enderecoFaturamento = getEnderecoFaturamento();
    // }
}
