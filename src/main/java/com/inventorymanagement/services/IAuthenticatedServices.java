package com.inventorymanagement.services;

import com.inventorymanagement.dto.AuthenDTO;
import com.inventorymanagement.dto.RegisterDTO;
import com.inventorymanagement.dto.response.AuthenResponseDTO;
import com.inventorymanagement.exception.InventoryException;

public interface IAuthenticatedServices {
    AuthenResponseDTO login(AuthenDTO authenDTO) throws InventoryException;
    void register(RegisterDTO registerDTO) throws InventoryException;
}
