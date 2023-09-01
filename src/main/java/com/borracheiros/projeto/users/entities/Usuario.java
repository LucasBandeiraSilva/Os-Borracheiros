package com.borracheiros.projeto.users.entities;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank
  @NotNull
  private String nome;
  @NotNull
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private LocalDate dataNascimento;
  @NotBlank
  @NotNull
  @Column(unique = true)
  private String cpf;

  @NotBlank
  @NotNull
  @Column(nullable = false)
  private String email;
 
  @NotNull
  @Column(nullable = false)
  private Long telefone;

  @NotBlank
  @NotNull
  @Column(nullable = false)
  private String StatusUsuario;
    
  @NotBlank
  @NotNull
  @Column(unique = true)
  private String senha;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

}
