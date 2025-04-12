package com.inventorymanagement.services.impl;

import com.inventorymanagement.dto.StatisticOrderDTO;
import com.inventorymanagement.repository.custom.StatisticCustomRepository;
import com.inventorymanagement.services.IStatisticServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServicesImpl implements IStatisticServices {
    private final StatisticCustomRepository statisticCustomRepository;
    @Override
    public List<StatisticOrderDTO> getDataStatistics(Integer year) {
        return statisticCustomRepository.getDataStatistics(year);
    }
}
