package com.inventorymanagement.services.impl;

import com.inventorymanagement.entity.Role;
import com.inventorymanagement.repository.RoleRepository;
import com.inventorymanagement.services.IRoleServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RoleServicesImpl implements IRoleServices {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
