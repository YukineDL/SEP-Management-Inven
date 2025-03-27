package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.InventoryDeliveryDTO;
import com.inventorymanagement.dto.ProductDeliveryDTO;
import com.inventorymanagement.repository.custom.ProductDeliveryCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ProductDeliveryCustomRepositoryImpl implements ProductDeliveryCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<ProductDeliveryDTO> findByInventoryDeliveryCode(String inventoryCode) {
        StringBuilder selectSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder groupBy = new StringBuilder();
        selectSql.append("""
                SELECT bn.product_code, CAST(SUM(COALESCE(pd.export_quantity, 0)) AS SIGNED) AS export_quantity
                , sum(pd.price_export) as price_export
                FROM product_delivery pd
                JOIN batch_number bn on bn.id = pd.batch_number_id
                """);
        whereSql.append("""
                where pd.inventory_delivery_code = :inventoryDeliveryCode
                """);
        groupBy.append("""
                group by bn.product_code
                """);
        selectSql.append(whereSql).append(groupBy);
        Query query = em.createNativeQuery(selectSql.toString());
        query.setParameter("inventoryDeliveryCode",inventoryCode);
        List<Object[]> objects = query.getResultList();
        List<ProductDeliveryDTO> results = new ArrayList<>();
        for (Object[] row : objects){
            AtomicInteger index = new AtomicInteger(0);
            ProductDeliveryDTO item = new ProductDeliveryDTO();
            item.setProductCode(RepositoryUtils.setValue(row[index.getAndIncrement()], String.class));
            item.setExportQuantity(RepositoryUtils.setValue(row[index.getAndIncrement()], Long.class));
            item.setPriceExport(RepositoryUtils.setValue(row[index.getAndIncrement()], Double.class));
            results.add(item);
        }
        return results;
    }
}
