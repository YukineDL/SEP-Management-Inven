package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.UnitSearchReqDTO;
import com.inventorymanagement.entity.Unit;
import com.inventorymanagement.repository.custom.UnitCustomRepository;
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
public class UnitCustomRepositoryImpl extends BaseCustomRepository implements UnitCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Unit> findBySearchReq(UnitSearchReqDTO dto, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder sqlWhere = new StringBuilder();
        sqlSelect.append("SELECT * ");
        sqlWhere.append(" FROM unit u WHERE 1=1 ");
        this.addParams(params,sqlWhere,dto);
        sql.append(sqlSelect).append(sqlWhere);
        Query query = em.createNativeQuery(sql.toString(), Unit.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        this.setParams(query,params);
        countSql.append(" SELECT COUNT(*) ");
        countSql.append(sqlWhere);
        Query queryCount = em.createNativeQuery(countSql.toString());
        this.setParams(queryCount,params);
        return new PageImpl<>(query.getResultList(), pageable, (Long)queryCount.getSingleResult());
    }
    private void addParams(Map<String, Object> params, StringBuilder sql, UnitSearchReqDTO dto) {
        if(StringUtils.isNotEmpty(dto.getUnitName())){
            sql.append(" and u.name like ")
                    .append("'%").append(dto.getUnitName()).append("%' ");
        }
        if(!Objects.isNull(dto.getIsDeleted())){
            sql.append(" and u.is_deleted = :isDeleted ");
            params.put("isDeleted", dto.getIsDeleted());
        }
    }
}
