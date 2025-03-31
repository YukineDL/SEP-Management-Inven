package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.dto.response.PurchaseOrderDTO;
import com.inventorymanagement.repository.custom.PurchaseOrderCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Slf4j
public class PurchaseOrderCustomRepositoryImpl implements PurchaseOrderCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public Page<PurchaseOrderDTO> findBySearchRequest(PurchaseOrderReqDTO searchReq) {
        StringBuilder sql = new StringBuilder();
        StringBuilder countSQL = new StringBuilder();
        StringBuilder selectSQL = new StringBuilder();
        StringBuilder whereSQL = new StringBuilder();
        StringBuilder pageSQl = new StringBuilder();
        StringBuilder orderSql = new StringBuilder();
        selectSQL.append("""
                SELECT po.code, po.approve, po.delivery_status, po.delivery_date, po.create_at,
                po.employee_code, e.name,
                po.supplier_id, s.name,
                po.username, po.action_time, po.delivery_at, po.create_at_date_time, po.total_quantity, po.is_used
                """);
        Map<String, Object> params = new HashMap<>();
        whereSQL.append("""
                FROM purchase_order po
                LEFT JOIN employee e ON e.code = po.employee_code
                LEFT JOIN supplier s on s.id = po.supplier_id
                WHERE 1=1
                """);
        addingWhereClause(searchReq, whereSQL, params);
        addingPaging(searchReq,pageSQl);
        orderSql.append("""
                order by po.create_at_date_time desc
                """);
        sql.append(selectSQL)
                .append(whereSQL)
                .append(orderSql)
                .append(pageSQl);
        String countAll = """
                SELECT COUNT(1)
                """;
        countSQL.append(countAll)
                .append(whereSQL);
        Query query = em.createNativeQuery(sql.toString());
        Query countQuery = em.createNativeQuery(countSQL.toString());
        setParams(params,query);
        setParams(params,countQuery);
        List<Object[]> objects = query.getResultList();
        List<PurchaseOrderDTO> results = new ArrayList<>();
        for (Object[] row : objects) {
            PurchaseOrderDTO dto = new PurchaseOrderDTO();
            AtomicInteger index = new AtomicInteger(0);
            // Assuming the result set columns correspond to fields in PurchaseOrderDTO
            // Adjust the indexes (0, 1, 2, ...) according to the column order in your SQL query.
            dto.setCode(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));  // Replace with actual field mapping
            dto.setApprove(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));  // Replace with actual field mapping
            dto.setDeliveryStatus(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));  // Replace with actual field mapping
            try {
                dto.setDeliveryDate(RepositoryUtils.setValue(
                        row[index.getAndIncrement()], LocalDate.class));
                dto.setCreateAt(RepositoryUtils.setValue(
                        row[index.getAndIncrement()], LocalDate.class));
            } catch (ClassCastException exception){
                log.info(exception.toString());
            }
            dto.setEmployeeCode(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));
            dto.setName(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));
            dto.setSupplierId(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], Integer.class));
            dto.setSupplierName(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));
            dto.setUsername(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], String.class));
            dto.setActionTime(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], LocalDateTime.class));
            dto.setDeliveryAt(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], LocalDateTime.class
            ));
            dto.setCreateAtDateTime(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], LocalDateTime.class
            ));
            dto.setTotalQuantity(RepositoryUtils.setValue(
                    row[index.getAndIncrement()], Integer.class
            ));
            // Add the mapped DTO to the results list
            results.add(dto);
        }
        return new PageImpl<>(results,searchReq.getPageable(),(Long)countQuery.getSingleResult());
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
            sqlWhere.append(" and po.create_at = :createAt ");
            params.put("createAt", reqDTO.getCreateAt());
        }
        if(!Objects.isNull(reqDTO.getSupplierId())){
            sqlWhere.append(" and po.supplier_id = :supplierId ");
            params.put("supplierId", reqDTO.getSupplierId());
        }
        if(!Objects.isNull(reqDTO.getDeliveryDate())){
            sqlWhere.append(" and po.delivery_date = :deliveryDate ");
            params.put("deliveryDate", reqDTO.getDeliveryDate());
        }
        if(StringUtils.isNotEmpty(reqDTO.getCode())){
            sqlWhere.append(" and po.code like ")
                    .append("'%").append(reqDTO.getCode()).append("%'");
        }
        if(!Objects.isNull(reqDTO.getFromDate()) && !Objects.isNull(reqDTO.getToDate())){
            sqlWhere.append(" and ( po.create_at >= :fromDate and po.create_at <= :toDate ) ");
            params.put("fromDate",reqDTO.getFromDate());
            params.put("toDate",reqDTO.getToDate());
        }else if(!Objects.isNull(reqDTO.getFromDate())){
            sqlWhere.append(" and po.create_at >= :fromDate ");
            params.put("fromDate",reqDTO.getFromDate());
        }else if(!Objects.isNull(reqDTO.getToDate())){
            sqlWhere.append(" and po.create_at <= :toDate ");
            params.put("toDate",reqDTO.getToDate());
        }
        if(!Objects.isNull(reqDTO.getIsUsed())){
            sqlWhere.append(" and po.is_used = :isUsed ");
            params.put("isUsed", reqDTO.getIsUsed());
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
