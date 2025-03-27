package com.inventorymanagement.services.impl;

import com.inventorymanagement.dto.EmployeeDTO;
import com.inventorymanagement.dto.EmployeeSearchDTO;
import com.inventorymanagement.repository.custom.EmployeeRepositoryCustom;
import com.inventorymanagement.utils.SecurityUtils;
import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.EmployeeUpdateDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.EmployeeRepository;
import com.inventorymanagement.services.IEmployeeServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServicesImpl implements IEmployeeServices {
    @Value("${jwt.signerKey}")
    private String signerKey;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;
    private final EmployeeRepositoryCustom employeeRepositoryCustom;
    private final List<String> LIST_MANAGER = new ArrayList<>(List.of(RoleEnum.ADMIN.name(),
            RoleEnum.MANAGER.name()));
    @Override
    public Page<Employee> getAll(String authHeader, Pageable pageable, EmployeeSearchDTO employeeSearchDTO) throws InventoryException {
        Employee employee = getFullInformation(authHeader);
        if(!LIST_MANAGER.contains(employee.getRoleCode())){
            throw new InventoryException(ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION));
        }

        return employeeRepositoryCustom.findAllBySearch(employeeSearchDTO,pageable);
    }

    @Override
    public Employee getFullInformation(String authHeader){
        String token = securityUtils.decode(authHeader);
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS256");

        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        Jwt jwt = nimbusJwtDecoder.decode(token);
        Long employeeId = (Long) jwt.getClaims().get("employee_id");
        Optional<Employee> employee = employeeRepository.findById(Math.toIntExact(employeeId));
        return employee.orElse(null);
    }

    @Override
    public Employee findByCode(String authHeader, String code) throws InventoryException {
        Employee employee = this.getFullInformation(authHeader);
        if(!LIST_MANAGER.contains(employee.getRoleCode()) && !employee.getCode().equals(code)){
                throw new InventoryException(ExceptionMessage.NO_PERMISSION,
                        ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION));
            }

        return employee;
    }

    @Override
    public void updateByCode(String authHeader, String employeeCode, EmployeeUpdateDTO employeeUpdateDTO) throws InventoryException {
        Employee employee = this.getFullInformation(authHeader);
        if(!LIST_MANAGER.contains(employee.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Employee> employeeUpdate = employeeRepository.findByCode(employeeCode);
        if(employeeUpdate.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.EMPLOYEE_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED)
            );
        }
        Employee employeeUpdated = employeeUpdate.get();
        employeeUpdated.setName(employeeUpdateDTO.getName());
        employeeUpdated.setRoleCode(employeeUpdateDTO.getRoleCode());
        employeeUpdated.setPhoneNumber(employeeUpdateDTO.getPhoneNumber());
        employeeRepository.save(employeeUpdated);
    }

    @Override
    public void lockAccount(String authHeader, String employeeCode) throws InventoryException {
        Employee me = this.getFullInformation(authHeader);
        if(!LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Employee> employee = employeeRepository.findByCode(employeeCode);
        if(employee.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.EMPLOYEE_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED)
            );
        }
        employee.get().setIsBlock(Constants.LOCK);
        employeeRepository.save(employee.get());
    }

    @Override
    public void unlockAccount(String authHeader, String employeeCode) throws InventoryException {
        Employee me = this.getFullInformation(authHeader);
        if(!LIST_MANAGER.contains(me.getRoleCode())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        Optional<Employee> employee = employeeRepository.findByCode(employeeCode);
        if(employee.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.EMPLOYEE_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED)
            );
        }
        employee.get().setIsBlock(Constants.UNLOCK);
        employeeRepository.save(employee.get());
    }

    @Override
    public EmployeeDTO findByCode(String code) throws InventoryException {
        Optional<Employee> employeeOP = employeeRepository.findByCode(code);
        if(employeeOP.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.EMPLOYEE_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.EMPLOYEE_NOT_EXISTED)
            );
        }
        return new EmployeeDTO(employeeOP.get());
    }
}
