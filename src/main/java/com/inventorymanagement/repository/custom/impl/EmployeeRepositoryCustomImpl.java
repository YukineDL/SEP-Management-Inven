package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.EmployeeSearchDTO;
import com.inventorymanagement.entity.Employee;
import com.inventorymanagement.repository.custom.EmployeeRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Page<Employee> findAllBySearch(EmployeeSearchDTO searchDTO, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        Map<String,Object> parameters = new HashMap<>();
        sql.append("""
                SELECT * FROM employee
                where 1=1
                """);
        sql.append(" and role_code <> '").append(RoleEnum.ADMIN.name()).append("'");
        addingWhereClause(sql,searchDTO,parameters);
        addingPaging(sql, pageable);
        Query query = entityManager.createNativeQuery(sql.toString(),Employee.class);
        setParameters(query,parameters);

        var results = query.getResultList();
        return new PageImpl<>(results,pageable,results.size());
    }
    private void addingWhereClause(StringBuilder sql,
                                   EmployeeSearchDTO searchDTO,
                                   Map<String, Object> parameters){
        if(StringUtils.isNotBlank(searchDTO.getName())){
            sql.append(" and name like ")
                    .append("'%").append(searchDTO.getName()).append("%'");
        }
        if(!Objects.isNull(searchDTO.getIsBlock())){
            sql.append(" and is_block = :isBlock ");
            parameters.put("isBlock",searchDTO.getIsBlock());
        }
    }
    private void addingPaging(StringBuilder sql, Pageable pageable){
        sql.append(" limit ")
                .append(pageable.getPageSize()).append(" offset ").append(pageable.getOffset());
    }
    private void setParameters(Query query, Map<String, Object> parameters){
        parameters.forEach(query::setParameter);
    }
}
