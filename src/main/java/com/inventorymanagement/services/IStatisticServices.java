package com.inventorymanagement.services;

import com.inventorymanagement.dto.StatisticOrderDTO;

import java.util.List;

public interface IStatisticServices {
    List<StatisticOrderDTO> getDataStatistics(Integer year);
}
