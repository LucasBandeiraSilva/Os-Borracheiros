package com.borracheiros.projeto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.borracheiros.projeto.users.entities.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{
    
}
