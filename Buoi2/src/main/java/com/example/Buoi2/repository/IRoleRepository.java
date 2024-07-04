package com.example.Buoi2.repository;


import com.example.Buoi2.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
    Role findRoleById(Long id);
    Optional<Role> findByName(String name);
}
