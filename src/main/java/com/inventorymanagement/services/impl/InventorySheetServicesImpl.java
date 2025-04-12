package com.inventorymanagement.services.impl;

import com.inventorymanagement.constant.Constants;
import com.inventorymanagement.constant.RoleEnum;
import com.inventorymanagement.dto.InventorySheetDTO;
import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.dto.ProductSheetSearchReqDTO;
import com.inventorymanagement.entity.*;
import com.inventorymanagement.exception.ExceptionMessage;
import com.inventorymanagement.exception.InventoryException;
import com.inventorymanagement.repository.InventorySheetRepository;
import com.inventorymanagement.repository.ProcessCheckRepository;
import com.inventorymanagement.repository.ProductRepository;
import com.inventorymanagement.repository.ProductSheetRepository;
import com.inventorymanagement.repository.custom.IProductSheetCustomRepository;
import com.inventorymanagement.repository.custom.InventorySheetCustomRepository;
import com.inventorymanagement.services.IEmployeeServices;
import com.inventorymanagement.services.IInventorySheetServices;
import com.inventorymanagement.services.IUnitServices;
import com.inventorymanagement.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventorySheetServicesImpl implements IInventorySheetServices {
    private final IProductSheetCustomRepository productSheetCustomRepository;
    private final IEmployeeServices employeeService;
    private final InventorySheetRepository inventorySheetRepository;
    private final ProcessCheckRepository processCheckRepository;
    private final ProductSheetRepository productSheetRepository;
    private final InventorySheetCustomRepository inventorySheetCustomRepository;
    private final IUnitServices unitServices;
    private final ProductRepository productRepository;
    @Override
    public String createInventorySheet(String authHeader, LocalDate startDate, LocalDate endDate) throws InventoryException {
        Employee me = employeeService.getFullInformation(authHeader);
        if(me == null || me.getRoleCode().equals(RoleEnum.SALE.name())){
            throw new InventoryException(
                    ExceptionMessage.NO_PERMISSION,
                    ExceptionMessage.messages.get(ExceptionMessage.NO_PERMISSION)
            );
        }
        List<InventorySheet> checkInventorySheets = inventorySheetRepository.findByStartDateAndEndDate(startDate, endDate);
        if(!checkInventorySheets.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_SHEET_EXISTED_TIME,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_SHEET_EXISTED_TIME)
            );
        }
        InventorySheet inventorySheet = InventorySheet.builder()
                .code(this.createCode())
                .startDate(startDate)
                .endDate(endDate)
                .createAt(LocalDate.now())
                .employeeCode(me.getCode())
                .build();
        inventorySheetRepository.save(inventorySheet);
        String key = UUID.randomUUID().toString();
        ProcessCheck processCheck = ProcessCheck.builder()
                .createAt(LocalDateTime.now())
                .status(Constants.PROCESSING)
                .checkSync(key)
                .build();
        processCheckRepository.save(processCheck);
        CompletableFuture.runAsync(() -> {
            try {
                List<ProductSheetDTO> dataSum = productSheetCustomRepository.getSumDataFromDateAndToDate(startDate, endDate);
                List<ProductSheet> results = new ArrayList<>();
                for(ProductSheetDTO dto : dataSum){
                    ProductSheet productSheet = new ProductSheet(dto,inventorySheet.getCode());
                    results.add(productSheet);
                }
                productSheetRepository.saveAll(results);
                processCheck.setStatus(Constants.SUCCESS);
                processCheckRepository.save(processCheck);
            } catch (Exception e){
                processCheck.setStatus(Constants.FAIL);
                processCheckRepository.save(processCheck);
            }
        }).join();
        return inventorySheet.getCode();
    }

    @Override
    public InventorySheetDTO getDetailInventorySheetBySearchRequest(Pageable pageable, ProductSheetSearchReqDTO dto) throws InventoryException {
        var inventorySheetOp = inventorySheetRepository.findByCode(dto.getCode());
        if(inventorySheetOp.isEmpty()){
            throw new InventoryException(
                    ExceptionMessage.INVENTORY_SHEET_NOT_EXISTED,
                    ExceptionMessage.messages.get(ExceptionMessage.INVENTORY_SHEET_NOT_EXISTED)
            );
        }
        Pageable fullPage = PageRequest.of(0, Integer.MAX_VALUE);
        var unitMap = unitServices.getAllUnits(null, fullPage).stream().collect(
                Collectors.toMap(Unit::getCode, Unit::getName)
        );
        var productMap = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode, Function.identity())
        );
        var data = productSheetCustomRepository.getDetailBySearchRequest(dto,pageable);
        for(ProductSheetDTO item : data.getContent()){
            var product = productMap.get(item.getProductCode());
            item.setProductName(product.getName());
            item.setProductUnit(product.getUnitCode());
            item.setProductUnitName(unitMap.get(product.getUnitCode()));
        }
        return InventorySheetDTO.builder()
                .data(data)
                .sheet(inventorySheetOp.get())
                .build();
    }

    @Override
    public Page<InventorySheet> findBySearchRequest(Pageable pageable, InventorySheetSearchDTO dto)   {
        return inventorySheetCustomRepository.findBySearchReq(dto,pageable);
    }

    @Override
    public byte[] exportExcel(ProductSheetSearchReqDTO dto) throws InventoryException {
        Pageable fullPage = PageRequest.of(0, Integer.MAX_VALUE);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        var unitMap = unitServices.getAllUnits(null, fullPage).stream().collect(
                Collectors.toMap(Unit::getCode, Unit::getName)
        );
        var productMap = productRepository.findAll().stream().collect(
                Collectors.toMap(Product::getCode, Function.identity())
        );
        var data = productSheetCustomRepository.getDetailBySearchRequest(dto,pageable);
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            var headerStyle = ExcelUtils.setCellStyle(
                    workbook,
                    IndexedColors.BLACK.getIndex(),
                    false,
                    false,
                    11,
                    IndexedColors.AQUA.getIndex()
            );
            Row headerRow = sheet.createRow(0);
            Row subHeaderRow = sheet.createRow(1);
            ExcelUtils.createCell(headerRow, 0,"Mã sản phẩm", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

            ExcelUtils.createCell(headerRow,1, "Tên hàng", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

            ExcelUtils.createCell(headerRow, 2, "ĐVT", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

            ExcelUtils.createCell(headerRow, 3, "Nhập kho", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));

            ExcelUtils.createCell(subHeaderRow,3, "Số lượng", headerStyle);
            ExcelUtils.createCell(subHeaderRow, 4, "Giá trị", headerStyle);

            ExcelUtils.createCell(headerRow, 5, "Xuất kho", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));
            ExcelUtils.createCell(subHeaderRow,5,"Số lượng", headerStyle);
            ExcelUtils.createCell(subHeaderRow,6, "Giá trị", headerStyle);

            ExcelUtils.createCell(headerRow, 7, "Trạng thái", headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 7, 7));

            ExcelUtils.applyBordersToMergedCells(sheet);
            var rowDataStyle = ExcelUtils.setCellStyle(workbook, IndexedColors.BLACK.getIndex(), false, false, 11, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
            int rowCol = 2;
            for (ProductSheetDTO item : data) {
                Row row = sheet.createRow(rowCol);
                AtomicInteger col = new AtomicInteger(0);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getProductCode(), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), productMap.get(item.getProductCode()), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), unitMap.get(item.getProductUnit()), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getQuantityShipped(), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getTotalImportAmount(), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getProductExportQuantity(), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getExportTotalAmount(), rowDataStyle);
                ExcelUtils.createCell(row, col.getAndIncrement(), item.getProductStatus(), rowDataStyle);
                rowCol++;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();

        } catch (IOException e) {
            throw new InventoryException(
                    e.getMessage());
        }
    }

    private String createCode(){
        return Constants.INVENTORY_SHEET_CODE +
               String.format("%05d", inventorySheetRepository.count() + 1);
    }

}
