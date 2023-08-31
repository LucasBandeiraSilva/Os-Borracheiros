package com.borracheiros.projeto.users.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name="Usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
     @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @Column(nullable = false, unique=true) 
    private String cpf;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false) 
    private Long telefone;
    @Column(nullable = false)
    private String StatusUsuario;
    @Column(nullable = false, unique=true)
    private String senha;

   @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


}

