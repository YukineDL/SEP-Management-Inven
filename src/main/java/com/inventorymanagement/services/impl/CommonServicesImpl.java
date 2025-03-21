package com.inventorymanagement.services.impl;
 
 import com.inventorymanagement.entity.CommonCategory;
 import com.inventorymanagement.repository.CommonCategoryRepository;
 import com.inventorymanagement.services.ICommonServices;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 
 import java.util.List;
 
 @Service
 @RequiredArgsConstructor
 public class CommonServicesImpl implements ICommonServices {
     private final CommonCategoryRepository commonCategoryRepository;
 
     @Override
     public List<CommonCategory> getCommonsByParentCode(String parentCode) {
         return commonCategoryRepository.findByParentCode(parentCode);
     }
 }