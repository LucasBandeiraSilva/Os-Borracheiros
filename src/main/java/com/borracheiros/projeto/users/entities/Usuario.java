package com.borracheiros.projeto.users.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
     @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @Column(nullable = false) 
    private String cpf;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false) 
    private String telefone;
    @Column(nullable = false)
    private String StatusUsuario;

    @ManyToMany
  private List<Role> roles;

   public Usuario(){
    
   }
}

