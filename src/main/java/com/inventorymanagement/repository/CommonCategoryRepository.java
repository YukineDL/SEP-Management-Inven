package com.inventorymanagement.repository;
 
 import com.inventorymanagement.entity.CommonCategory;
 import org.springframework.data.jpa.repository.JpaRepository;
 
 import java.util.List;
 
 public interface CommonCategoryRepository extends JpaRepository<CommonCategory, Integer> {
     List<CommonCategory> findByParentCode(String parentCode);
 }