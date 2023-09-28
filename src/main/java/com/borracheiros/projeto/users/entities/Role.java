package com.borracheiros.projeto.users.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "tipo_users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    

   
  
}
