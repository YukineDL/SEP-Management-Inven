package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.constant.PURCHASE_ORDER_APPROVE;
import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.dto.ProductSheetExportDTO;
import com.inventorymanagement.repository.custom.IProductSheetCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ProductSheetCustomRepositoryImpl implements IProductSheetCustomRepository {
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
}
