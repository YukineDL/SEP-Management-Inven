package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductDTO;
import com.inventorymanagement.dto.ProductSearchDTO;
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
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ProductDTO> findAllBySearchRequest(ProductSearchDTO productSearchDTO, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        sql.append(""" 
                select code
                ,name
                ,description
                ,selling_price
                ,category_code
                ,brand_code from product where 1=1
                """);
        addingWhereClause(sql,productSearchDTO,parameters);
        addingPageQuery(sql,pageable);

        Query query = entityManager.createNativeQuery(sql.toString(),ProductDTO.class);
        setParameters(parameters,query);

        var results = query.getResultList();
        return new PageImpl<>(results,pageable,results.size());
    }
    private void addingWhereClause(StringBuilder sql, ProductSearchDTO productSearchDTO, Map<String, Object> parameters) {
        if(StringUtils.isNotBlank(productSearchDTO.getCode())) {
            sql.append(" and code = :code");
            parameters.put("code", productSearchDTO.getCode());
        }
        if(StringUtils.isNotBlank(productSearchDTO.getName())) {
            sql.append(" and name like ")
                    .append("'%").append(productSearchDTO.getName()).append("%'");
        }
        if(StringUtils.isNotBlank(productSearchDTO.getCategoryCode())){
            sql.append(" and category_code = :categoryCode");
            parameters.put("categoryCode", productSearchDTO.getCategoryCode());
        }
        if(StringUtils.isNotBlank(productSearchDTO.getBrandCode())){
            sql.append(" and brand_code = :brandCode");
            parameters.put("brandCode", productSearchDTO.getBrandCode());
        }
    }
    private void addingPageQuery(StringBuilder sql, Pageable pageable) {
        sql.append(" limit ")
                .append(pageable.getPageSize()).append(" offset ").append(pageable.getOffset());
    }
    private void setParameters(Map<String, Object> parameters, Query sql) {
        parameters.forEach(sql::setParameter);
    }
}
