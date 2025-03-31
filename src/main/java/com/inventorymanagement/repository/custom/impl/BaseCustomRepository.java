package com.inventorymanagement.repository.custom.impl;

import jakarta.persistence.Query;
import org.springframework.data.domain.Pageable;


import java.util.Map;

public class BaseCustomRepository {
    protected void addingPageQuery(StringBuilder sql, Pageable pageable) {
        sql.append(" limit ")
                .append(pageable.getPageSize()).append(" offset ").append(pageable.getOffset());
    }
    protected void setParams(Query query, Map<String,Object> params){
        params.forEach(query::setParameter);
    }
}
