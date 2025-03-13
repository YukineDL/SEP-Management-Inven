package com.inventorymanagement.services.impl;

import com.inventorymanagement.utils.SecurityUtils;
import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.dto.AuthenDTO;
import com.inventorymanagement.dto.RegisterDTO;
import com.inventorymanagement.dto.response.AuthenResponseDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.services.IAuthenticatedServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticatedServices implements IAuthenticatedServices {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    @Override
    public AuthenResponseDTO login(AuthenDTO authenDTO) throws InventoryException {
        Employee employee = employeeRepository.findByUsername(authenDTO.getUsername());
        if(Objects.isNull(employee)){
            throw new InventoryException(
                    ExceptionMessage.USERNAME_INCORRECT,
                    ExceptionMessage.messages.get(ExceptionMessage.USERNAME_INCORRECT)
            );
        }
        if(employee.getIsBlock()){
            throw new InventoryException(
                    ExceptionMessage.ACCOUNT_BANNED,
                    ExceptionMessage.messages.get(ExceptionMessage.ACCOUNT_BANNED)
            );
        }
        boolean isCorrect = passwordEncoder.matches(authenDTO.getPassword(), employee.getPassword() );
        if(!isCorrect){
            throw new InventoryException(
                    ExceptionMessage.USERNAME_INCORRECT,
                    ExceptionMessage.messages.get(ExceptionMessage.USERNAME_INCORRECT)
            );
        }
        return AuthenResponseDTO.builder()
                .token(securityUtils.generate(employee))
                .info(employee).build();
    }

    @Override
    public void register(RegisterDTO registerDTO) throws InventoryException {
        if(employeeRepository.existsByUsername(registerDTO.getUsername())){
            throw new InventoryException(
                    ExceptionMessage.EMPLOYEE_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_EXISTED));
        }
        Employee newEmployee = new Employee();
        newEmployee.setUsername(registerDTO.getUsername());
        newEmployee.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newEmployee.setRoleCode(registerDTO.getRoleCode());
        newEmployee.setName(registerDTO.getName());
        newEmployee.setInventoryCode(registerDTO.getInventoryCode());
        newEmployee.setCode(getNewEmployeeCode());
        newEmployee.setPhoneNumber(registerDTO.getPhoneNumber());
        newEmployee.setIsBlock(Constants.UNLOCK);
        employeeRepository.save(newEmployee);
    }
    private String getNewEmployeeCode(){
        int max = employeeRepository.findAll().size();
        return "E" + (max + 1);
    }
}
