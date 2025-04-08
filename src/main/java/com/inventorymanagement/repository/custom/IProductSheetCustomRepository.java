package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.dto.ProductSheetSearchReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IProductSheetCustomRepository {
    List<ProductSheetDTO> getSumDataFromDateAndToDate(LocalDate from, LocalDate to);
    Page<ProductSheetDTO> getDetailBySearchRequest(ProductSheetSearchReqDTO dto, Pageable pageable);
}
