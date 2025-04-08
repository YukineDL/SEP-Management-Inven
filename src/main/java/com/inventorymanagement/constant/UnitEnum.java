package com.inventorymanagement.constant;

import lombok.Getter;

@Getter
public enum UnitEnum {
    BAO("Bao"),
    BINH("Bình"),
    BO("Bộ"),
    CAI("Cái"),
    CAY("Cây"),
    CHIEC("Chiếc"),
    CUON("Cuộn"),
    GOI("Gói"),
    KG("Kg"),
    LIT("Lít"),
    LO("Lọ"),
    MET("Mét"),
    TAM("Tấm"),
    THUNG("Thùng"),
    TUI("Túi"),
    VI("Vỉ");
    private final String name;
    UnitEnum(String name){
        this.name = name;
    }
}
