package com.inventorymanagement.repository.custom.impl;

import com.inventorymanagement.dto.StatisticOrderDTO;
import com.inventorymanagement.repository.custom.StatisticCustomRepository;
import com.inventorymanagement.utils.RepositoryUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@RequiredArgsConstructor
@Repository
public class StatisticCustomRepositoryImpl implements StatisticCustomRepository {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<StatisticOrderDTO> getDataStatistics(Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                WITH cte_months AS (
                    SELECT 1 AS month UNION ALL
                    SELECT 2 UNION ALL
                    SELECT 3 UNION ALL
                    SELECT 4 UNION ALL
                    SELECT 5 UNION ALL
                    SELECT 6 UNION ALL
                    SELECT 7 UNION ALL
                    SELECT 8 UNION ALL
                    SELECT 9 UNION ALL
                    SELECT 10 UNION ALL
                    SELECT 11 UNION ALL
                    SELECT 12
                )
                SELECT
                    m.month,
                    COUNT(o.id) AS total_order,
                    COALESCE(SUM(o.total_amount), 0) AS total_amount
                FROM cte_months m
                LEFT JOIN inventory_delivery o
                    ON MONTH(o.create_at) = m.month
                    AND YEAR(o.create_at) = :year
                WHERE o.approve_status = 'APPROVED' OR  o.approve_status is null
                GROUP BY m.month
                ORDER BY m.month;
                """);
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("year", year);

        List<Object[]> resultList = query.getResultList();
        return this.setObjects(resultList);
    }
    private List<StatisticOrderDTO> setObjects(List<Object[]> objects){
        return objects.stream().map(row -> new StatisticOrderDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                RepositoryUtils.setValue(row[2],Double.class)
        )).toList();
    }

    @Override
    public List<StatisticOrderDTO> getDataReturnStatistics(Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                WITH cte_months AS (
                    SELECT 1 AS month UNION ALL
                    SELECT 2 UNION ALL
                    SELECT 3 UNION ALL
                    SELECT 4 UNION ALL
                    SELECT 5 UNION ALL
                    SELECT 6 UNION ALL
                    SELECT 7 UNION ALL
                    SELECT 8 UNION ALL
                    SELECT 9 UNION ALL
                    SELECT 10 UNION ALL
                    SELECT 11 UNION ALL
                    SELECT 12
                )
                SELECT
                    m.month,
                    COUNT(o.id) AS total_order,
                    COALESCE(SUM(o.total_amount), 0) AS total_amount
                FROM cte_months m
                LEFT JOIN return_form o
                    ON MONTH(o.create_at) = m.month
                    AND YEAR(o.create_at) = :year
                WHERE o.approve_status = 'APPROVED' OR  o.approve_status is null
                GROUP BY m.month
                ORDER BY m.month;
                """);
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("year", year);

        List<Object[]> resultList = query.getResultList();
        return this.setObjects(resultList);
    }

    @Override
    public List<StatisticOrderDTO> getDataReceiptStatistics(Integer year) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                WITH cte_months AS (
                    SELECT 1 AS month UNION ALL
                    SELECT 2 UNION ALL
                    SELECT 3 UNION ALL
                    SELECT 4 UNION ALL
                    SELECT 5 UNION ALL
                    SELECT 6 UNION ALL
                    SELECT 7 UNION ALL
                    SELECT 8 UNION ALL
                    SELECT 9 UNION ALL
                    SELECT 10 UNION ALL
                    SELECT 11 UNION ALL
                    SELECT 12
                )
                SELECT
                    m.month,
                    COUNT(o.id) AS total_order,
                    COALESCE(SUM(o.total_amount), 0) AS total_amount
                FROM cte_months m
                LEFT JOIN inventory_receipt o
                    ON MONTH(o.create_at) = m.month
                    AND YEAR(o.create_at) = :year
                WHERE o.approve = 'APPROVED' OR  o.approve is null
                GROUP BY m.month
                ORDER BY m.month;
                """);
        Query query = em.createNativeQuery(sql.toString());
        query.setParameter("year", year);

        List<Object[]> resultList = query.getResultList();
        return this.setObjects(resultList);
    }
}
