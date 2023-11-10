package com.borracheiros.projeto.users.entities;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  private String nome;

  @CPF
  @Column(unique = true)
  private String cpf;

  @Column(unique = false)
  private String email;

  private Boolean StatusUsuario;

  @Column(unique = false)
  private String senha;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

}
