package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductSheetDTO;

import java.time.LocalDate;
import java.util.List;

public interface IProductSheetCustomRepository {
    List<ProductSheetDTO> getSumDataFromDateAndToDate(LocalDate from, LocalDate to);
}
