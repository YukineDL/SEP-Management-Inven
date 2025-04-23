package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.StatisticOrderDTO;

import java.util.List;

public interface StatisticCustomRepository {
    List<StatisticOrderDTO> getDataStatistics(Integer year);
    List<StatisticOrderDTO> getDataReturnStatistics(Integer year);
    List<StatisticOrderDTO> getDataReceiptStatistics(Integer year);
}
