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
                select p.code
                                ,p.name
                                ,p.unit
                                ,p.description
                                ,p.selling_price
                                ,p.category_code
                                ,c.name
                                ,p.brand_code
                                ,b.name from product p
                                join brand b on b.code = p.brand_code
                                join category c on c.code = p.category_code where 1=1
                """);
        addingWhereClause(sql,productSearchDTO,parameters);
        Query countQuery = entityManager.createNativeQuery(sql.toString());
        setParameters(parameters,countQuery);
        addingPageQuery(sql,pageable);
        Query query = entityManager.createNativeQuery(sql.toString(),ProductDTO.class);
        setParameters(parameters,query);

        var results = query.getResultList();
        var counts = countQuery.getResultList();
        return new PageImpl<>(results,pageable,counts.size());
    }
    private void addingWhereClause(StringBuilder sql, ProductSearchDTO productSearchDTO, Map<String, Object> parameters) {
        if(StringUtils.isNotBlank(productSearchDTO.getCode())) {
            sql.append(" and p.code = :code");
            parameters.put("code", productSearchDTO.getCode());
        }
        if(StringUtils.isNotBlank(productSearchDTO.getName())) {
            sql.append(" and p.name like ")
                    .append("'%").append(productSearchDTO.getName()).append("%'");
        }
        if(StringUtils.isNotBlank(productSearchDTO.getCategoryCode())){
            sql.append(" and p.category_code = :categoryCode");
            parameters.put("categoryCode", productSearchDTO.getCategoryCode());
        }
        if(StringUtils.isNotBlank(productSearchDTO.getBrandCode())){
            sql.append(" and p.brand_code = :brandCode");
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
