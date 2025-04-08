package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.dto.ProductSheetExportDTO;
import com.inventorymanagement.dto.ProductSheetSearchReqDTO;
import com.inventorymanagement.entity.InventorySheet;
import com.inventorymanagement.repository.custom.IProductSheetCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ProductSheetCustomRepositoryImpl extends BaseCustomRepository implements IProductSheetCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<ProductSheetDTO> getSumDataFromDateAndToDate(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlExportData = new StringBuilder();
        sql.append("""
        WITH cte_date AS (
            SELECT
                bn.product_code,
                CAST(SUM(COALESCE(bn.quantity_shipped, 0)) AS SIGNED) AS quantity_shipped,
                bn.status,
                CAST(SUM(COALESCE(bn.export_quantity, 0)) AS SIGNED) AS export_quantity ,
                SUM(bn.import_price * bn.quantity_shipped) as total_import_amount
            FROM
                batch_number bn
            WHERE
                bn.create_at >= :fromDate AND bn.create_at <= :toDate
            GROUP BY
                bn.product_code, bn.status
        )
        SELECT
            p.code,
            p.name,
            p.unit,
            cte_date.quantity_shipped,
            cte_date.total_import_amount,
            cte_date.status,
            cte_date.export_quantity
        FROM
            product p
        JOIN
            cte_date
        ON
            p.code = cte_date.product_code
        """);
        sqlExportData.append("""
                select bn.product_code,bn.status ,SUM(pd.price_export) total_export_amount from inventory_delivery id
                join product_delivery pd on pd.inventory_delivery_code = id.code
                join batch_number bn on bn.id = pd.batch_number_id
                where id.create_at >= :fromDate and id.create_at <= :toDate and id.approve_status <> '
                """).append(PURCHASE_ORDER_APPROVE.REJECTED.name()).append("'")
                .append(" group by bn.product_code, bn.status ");


        Query exportQuery = em.createNativeQuery(sqlExportData.toString());
        exportQuery.setParameter("fromDate", from);
        exportQuery.setParameter("toDate", to);
        List<ProductSheetExportDTO> exportList = new ArrayList<>();
        List<Object[]> resExport = exportQuery.getResultList();
        for (Object[] row : resExport) {
            AtomicInteger index = new AtomicInteger(0);
            ProductSheetExportDTO productSheetExportDTO = new ProductSheetExportDTO();
            productSheetExportDTO.setProductCode(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetExportDTO.setProductStatus(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetExportDTO.setTotalExportPrice(RepositoryUtils.setValue(row[index.getAndIncrement()],Double.class));
            productSheetExportDTO.createKeyMap();
            exportList.add(productSheetExportDTO);
        }
        var productExportMap = exportList.stream().collect(Collectors.toMap(
                ProductSheetExportDTO::getKeyMap, productSheetExportDTO -> productSheetExportDTO
        ));

        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("fromDate", from);
        query.setParameter("toDate", to);
        List<Object[]> results = query.getResultList();
        List<ProductSheetDTO> productSheetDTOS = new ArrayList<>();
        for (Object[] row : results) {
            AtomicInteger index = new AtomicInteger(0);
            ProductSheetDTO productSheetDTO = new ProductSheetDTO();
            productSheetDTO.setProductCode(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductName(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductUnit(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setQuantityShipped(RepositoryUtils.setValue(row[index.getAndIncrement()],Long.class));
            productSheetDTO.setTotalImportAmount(RepositoryUtils.setValue(row[index.getAndIncrement()],Double.class));
            productSheetDTO.setProductStatus(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductExportQuantity(RepositoryUtils.setValue(row[index.getAndIncrement()],Long.class));
            productSheetDTO.createKeyMap();
            if(productExportMap.containsKey(productSheetDTO.getKeyMap())){
                productSheetDTO.setExportTotalAmount(productExportMap.get(productSheetDTO.getKeyMap()).getTotalExportPrice());
            }else{
                productSheetDTO.setExportTotalAmount((double) 0);
            }
            productSheetDTOS.add(productSheetDTO);
        }

        return productSheetDTOS;
    }

    @Override
    public Page<ProductSheetDTO> getDetailBySearchRequest(ProductSheetSearchReqDTO dto, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder pageSql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();
        selectSql.append("""
                SELECT ps.product_code, ps.product_name, ps.product_unit, ps.product_status,ps.quantity_shipped ,
                ps.total_import_amount, ps.product_export_quantity, ps.total_export_amount
                """);
        whereSql.append("""
                FROM product_sheet ps
                WHERE 1=1
                """);
        var params = new HashMap<String, Object>();
        this.addParams(params,whereSql,dto);
        this.addingPageQuery(pageSql,pageable);
        countSql.append("""
                SELECT COUNT(*)
                """);
        countSql.append(whereSql);

        sql.append(selectSql).append(whereSql).append(pageSql);
        Query query = em.createNativeQuery(sql.toString());
        Query count = em.createNativeQuery(countSql.toString());
        this.setParams(query,params);
        this.setParams(count,params);
        List<Object[]> results = query.getResultList();
        List<ProductSheetDTO> productSheetDTOS = new ArrayList<>();
        for (Object[] row : results) {
            AtomicInteger index = new AtomicInteger(0);
            ProductSheetDTO productSheetDTO = new ProductSheetDTO();
            productSheetDTO.setProductCode(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductName(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductUnit(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setProductStatus(RepositoryUtils.setValue(row[index.getAndIncrement()],String.class));
            productSheetDTO.setQuantityShipped(
                    Long.valueOf(RepositoryUtils.setValue(row[index.getAndIncrement()],Integer.class)));
            productSheetDTO.setTotalImportAmount(RepositoryUtils.setValue(row[index.getAndIncrement()],Double.class));
            productSheetDTO.setProductExportQuantity(
                    Long.valueOf(RepositoryUtils.setValue(row[index.getAndIncrement()],Integer.class)));
            productSheetDTO.setExportTotalAmount(RepositoryUtils.setValue(row[index.getAndIncrement()],Double.class));
            productSheetDTOS.add(productSheetDTO);
        }
        return new PageImpl<>(productSheetDTOS, pageable, (Long)count.getSingleResult());
    }

    private void addParams(Map<String, Object> params, StringBuilder sql, ProductSheetSearchReqDTO dto){
        if(StringUtils.isNotEmpty(dto.getCode())){
            sql.append("""
                    and ps.inventory_sheet_code = :code
                    """);
            params.put("code", dto.getCode());
        }
        if(StringUtils.isNotEmpty(dto.getProductCode())){
            sql.append(" and ps.product_code like ")
                    .append("'%").append(dto.getProductCode()).append("%'");
        }
        if(StringUtils.isNotEmpty(dto.getProductName())){
            sql.append(" and ps.product_name like ")
                    .append("'%").append(dto.getProductName()).append("%'");
        }
        if(StringUtils.isNotEmpty(dto.getProductUnit())){
            sql.append(" and ps.product_unit like ")
                    .append("'%").append(dto.getProductUnit()).append("%'");
        }
        if(StringUtils.isNotEmpty(dto.getProductStatus())){
            sql.append(" and ps.product_status = :status ");
            params.put("status", dto.getProductStatus());
        }
        if(!Objects.isNull(dto.getImportQuantityProduct())){
            sql.append(" and ps.quantity_shipped <= :quantityShipped ");
            params.put("quantityShipped", dto.getImportQuantityProduct());
        }
        if(!Objects.isNull(dto.getExportQuantityProduct())){
            sql.append(" and ps.product_export_quantity <= :productExportQuantity ");
            params.put("productExportQuantity", dto.getExportQuantityProduct());
        }
        if(!Objects.isNull(dto.getImportPrice())){
            sql.append(" and ps.total_import_amount <= :totalImportAmount ");
            params.put("totalImportAmount", dto.getImportPrice());
        }
    }
}
