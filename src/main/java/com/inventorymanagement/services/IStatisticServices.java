package com.inventorymanagement.services;

import com.inventorymanagement.dto.StatisticOrderDTO;

import java.util.List;

public interface IStatisticServices {
    List<StatisticOrderDTO> getDataDeliveryStatistics(Integer year);
    List<StatisticOrderDTO> getDataReceiptStatistics(Integer year);

    List<StatisticOrderDTO> getDataReturnStatistics(Integer year);

}
