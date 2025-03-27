package com.inventorymanagement.services;

import com.inventorymanagement.entity.CommonCategory;

import java.util.List;

public interface ICommonServices {
    List<CommonCategory> getCommonsByParentCode(String parentCode);
}
