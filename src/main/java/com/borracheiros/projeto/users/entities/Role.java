package com.borracheiros.projeto.users.entities;

import java.util.List;

import com.borracheiros.projeto.estoque.entities.Estoque;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Role(Long id) {
        this.id = id;
    }
     @OneToMany(mappedBy = "role")
    private List<Estoque> estoques;

}
