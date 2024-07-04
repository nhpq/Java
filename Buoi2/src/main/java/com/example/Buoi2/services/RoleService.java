package com.example.Buoi2.services;

import com.example.Buoi2.entity.Role;
import com.example.Buoi2.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final IRoleRepository roleRepository;

    @Autowired
    public RoleService(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getRolesByIds(List<Long> roleIds) {
        return roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }}
