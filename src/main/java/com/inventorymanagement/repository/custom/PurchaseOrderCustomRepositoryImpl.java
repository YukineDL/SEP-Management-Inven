package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.entity.PurchaseOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository{
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<PurchaseOrder> findBySearchRequest(PurchaseOrderReqDTO searchReq) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSQL = new StringBuilder();
        StringBuilder selectSQL = new StringBuilder();
        StringBuilder whereSQL = new StringBuilder();
        StringBuilder pageSQl = new StringBuilder();
        selectSQL.append("""
                SELECT * FROM purchase_order po
                WHERE 1=1
                """);
        Map<String, Object> params = new HashMap<>();
        addingWhereClause(searchReq, whereSQL, params);
        addingPaging(searchReq,pageSQl);
        sql.append(selectSQL)
                .append(whereSQL)
                .append(pageSQl);

        countSQL.append(selectSQL)
                .append(whereSQL);
        Query query = em.createNativeQuery(sql.toString(), PurchaseOrder.class);
        Query countQuery = em.createNativeQuery(sql.toString());
        setParams(params,query);
        setParams(params,countQuery);
        return new PageImpl<>(query.getResultList(),searchReq.getPageable(),countQuery.getResultList().size());
    }
    private void addingWhereClause(PurchaseOrderReqDTO reqDTO, StringBuilder sqlWhere, Map<String, Object> params){
        if(StringUtils.isNotEmpty(reqDTO.getApproveStatus())){
            sqlWhere.append(" and po.approve = :approveStatus ");
            params.put("approveStatus", reqDTO.getApproveStatus());
        }
        if(StringUtils.isNotEmpty(reqDTO.getDeliveryStatus())){
            sqlWhere.append(" and po.delivery_status = :deliveryStatus ");
            params.put("deliveryStatus", reqDTO.getDeliveryStatus());
        }
        if(!Objects.isNull(reqDTO.getCreateAt())){
            sqlWhere.append(" and po.create_at = :createAt");
            params.put("createAt", reqDTO.getCreateAt());
        }
    }
    private void addingPaging(PurchaseOrderReqDTO reqDTO, StringBuilder pageSQL){
        pageSQL.append(" limit ")
                .append(reqDTO.getPageable()
                        .getPageSize())
                .append(" offset ")
                .append(reqDTO.getPageable().getOffset());
    }
    private void setParams(Map<String, Object> params, Query query){
        params.forEach(query::setParameter);
    }
}
