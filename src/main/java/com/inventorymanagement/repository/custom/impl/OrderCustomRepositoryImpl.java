package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.OrderDTO;
import com.inventorymanagement.dto.OrderSearchReqDTO;
import com.inventorymanagement.repository.custom.OrderCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class OrderCustomRepositoryImpl implements OrderCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<OrderDTO> findOrderBySearchReq(OrderSearchReqDTO dto, Pageable pageable) {
        Map<String,Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlSelect = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder orderBySql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        sqlSelect.append("""
                Select o.code, o.approve_status, o.approve_by, o.approve_date, o.delivery_status, o.total_amount, o.create_at,
                          o.customer_id, o.employee_code, o.delivery_by, o.delivery_date
                """);
        whereSql.append("""
                from `order` o
                LEFT JOIN customer c on c.id = o.customer_id
                where 1=1
                """);
        orderBySql.append("""
                order by o.create_at desc
                """);
        countSql.append("""
                SELECT COUNT(1)
                """);
        pageSql.append(" limit ")
                .append(pageable.getPageSize())
                .append(" offset ")
                .append(pageable.getOffset());
        addParams(whereSql,params,dto);
        sql.append(sqlSelect).append(whereSql).append(orderBySql).append(pageSql);
        countSql.append(whereSql);
        Query sqlQuery = em.createNativeQuery(sql.toString());
        setParams(sqlQuery, params);
        Query sqlCountQuery = em.createNativeQuery(countSql.toString());
        setParams(sqlCountQuery, params);
        Long totalElement = (Long)sqlCountQuery.getSingleResult();
        List<Object[]> objects = sqlQuery.getResultList();
        List<OrderDTO> results = new ArrayList<>();
        for (Object[] row : objects){
            AtomicInteger index = new AtomicInteger(0);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            orderDTO.setApproveStatus(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            orderDTO.setApproveBy(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            orderDTO.setApproveDate(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDateTime.class));
            orderDTO.setDeliveryStatus(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            orderDTO.setTotalAmount(RepositoryUtils.setValue(row[index.getAndIncrement()], Double.class));
            orderDTO.setCreateAt(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDateTime.class));
            orderDTO.setCustomerId(RepositoryUtils.setValue(row[index.getAndIncrement()], Integer.class));
            orderDTO.setEmployeeCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            orderDTO.setDeliveryBy(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            orderDTO.setDeliveryDate(RepositoryUtils.setValue(row[index.getAndIncrement()], LocalDateTime.class));
            results.add(orderDTO);
        }
        return new PageImpl<>(results, pageable, totalElement);
    }
    private void addParams(StringBuilder whereClause, Map<String, Object> params, OrderSearchReqDTO reqDTO){
        if(StringUtils.isNotEmpty(reqDTO.getCode())){
            whereClause.append(" and o.code like ")
                    .append("'%").append(reqDTO.getCode()).append("%'");
        }
        if(StringUtils.isNotEmpty(reqDTO.getCustomerPhoneNumber())){
            whereClause.append(" and c.phone_number like ")
                    .append("'%").append(reqDTO.getCustomerPhoneNumber()).append("%'");
        }
        if(StringUtils.isNotEmpty(reqDTO.getApproveStatus())){
            whereClause.append(" and o.approve_status = :approveStatus ");
            params.put("approveStatus",reqDTO.getApproveStatus());
        }
        if(StringUtils.isNotEmpty(reqDTO.getDeliveryStatus())){
            whereClause.append(" and o.delivery_status = :deliveryStatus ");
            params.put("deliveryStatus", reqDTO.getDeliveryStatus());
        }
        if(StringUtils.isNotEmpty(reqDTO.getEmployeeCode())){
            whereClause.append(" and o.employee_code = :employeeCode ");
            params.put("employeeCode", reqDTO.getEmployeeCode());
        }
        if(!Objects.isNull(reqDTO.getFromDate()) && !Objects.isNull(reqDTO.getToDate())){
            whereClause.append(" and ( o.create_at >= :fromDate and o.create_at <= :toDate ) ");
            params.put("fromDate",reqDTO.getFromDate());
            params.put("toDate",reqDTO.getToDate());
        }else if(!Objects.isNull(reqDTO.getFromDate())){
            whereClause.append(" and o.create_at >= :fromDate ");
            params.put("fromDate",reqDTO.getFromDate());
        }else if(!Objects.isNull(reqDTO.getToDate())){
            whereClause.append(" and o.create_at <= :toDate ");
            params.put("toDate",reqDTO.getToDate());
        }
        if(!Objects.isNull(reqDTO.getTotalAmountTo())){
            whereClause.append(" and o.total_amount <= :totalAmountTo ");
            params.put("totalAmountTo", reqDTO.getTotalAmountTo());
        }
        if(!Objects.isNull(reqDTO.getTotalAmountFrom())){
            whereClause.append(" and o.total_amount >= :totalAmountFrom ");
            params.put("totalAmountFrom", reqDTO.getTotalAmountFrom());
        }
    }
    private void setParams(Query sql, Map<String, Object> params){
        params.forEach(sql::setParameter);
    }
}
