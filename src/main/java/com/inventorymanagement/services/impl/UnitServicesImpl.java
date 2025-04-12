package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.UnitCreateReqDTO;
import com.inventorymanagement.dto.UnitSearchReqDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.entity.Unit;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.UnitRepository;
import com.inventorymanagement.repository.custom.UnitCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IUnitServices;
import com.inventorymanagement.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UnitServicesImpl implements IUnitServices {
    private final IEmployeeServices employeeService;
    private final UnitRepository unitRepository;
    private final UnitCustomRepository unitCustomRepository;
    @Override
    public void createUnit(String authHeader, UnitCreateReqDTO dto) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        String code = Utils.convertToCode(dto.getUnitName());
        if(unitRepository.existsByCode(code)){
            throw new InventoryException(
                    ExceptionMessage.UNIT_CODE_SAME,
                    ExceptionMessage.messages.get(ExceptionMessage.UNIT_CODE_SAME)
            );
        }
        Unit unit = Unit.builder()
                .code(code)
                .isDeleted(false)
                .name(dto.getUnitName())
                .build();
        unitRepository.save(unit);
    }
    @Override
    public void updateUnit(String authHeader, UnitCreateReqDTO dto, String code) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        var unit = this.getUnit(code);
        String codeNew = Utils.convertToCode(dto.getUnitName());
        if(unitRepository.existsByCodeAndCodeNotLike(codeNew,code)){
            throw new InventoryException(
                    ExceptionMessage.UNIT_CODE_SAME,
                    ExceptionMessage.messages.get(ExceptionMessage.UNIT_CODE_SAME)
            );
        }
        unit.setCode(codeNew);
        unit.setName(dto.getUnitName());
        unitRepository.save(unit);
    }

    @Override
    public void deleteUnit(String authHeader, String code) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        var unit = this.getUnit(code);
        unit.setIsDeleted(true);
        unitRepository.save(unit);
    }

    @Override
    public Unit getUnit(String code) throws InventoryException {
        var unitOp = unitRepository.findByCode(code);
        if(unitOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.UNIT_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.UNIT_NOT_EXISTED)
            );
        }
        return unitOp.get();
    }

    @Override
    public Page<Unit> getAllUnits(UnitSearchReqDTO dto, Pageable pageable){
        if(Objects.isNull(dto)){
            dto = new UnitSearchReqDTO();
        }
        return unitCustomRepository.findBySearchReq(dto,pageable);
    }
}
