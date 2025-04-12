package com.inventorymanagement.controller;

import com.inventorymanagement.services.IStatisticServices;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final IStatisticServices statisticServices;
    @GetMapping()
    public ResponseEntity<Object> getStatistic(@RequestParam(required = false) Integer year) {
        if(year == null){
            year = LocalDate.now().getYear();
        }
        return new ResponseEntity<>(statisticServices.getDataStatistics(year), HttpStatus.OK);
    }
}
