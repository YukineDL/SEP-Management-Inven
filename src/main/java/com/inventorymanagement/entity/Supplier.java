package com.inventorymanagement.entity;

import com.inventorymanagement.dto.SupplierDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "tax_number")
    private String taxNumber;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    public Supplier(SupplierDTO dto){
        if(StringUtils.isNotBlank(dto.getName())){
            this.name = dto.getName();
        }
        if(StringUtils.isNotBlank(dto.getAddress())){
            this.address = dto.getAddress();
        }
        if(StringUtils.isNotBlank(dto.getPhoneNumber())){
            this.phoneNumber = dto.getPhoneNumber();
        }
        if(StringUtils.isNotBlank(dto.getTaxNumber())){
            this.taxNumber = dto.getTaxNumber();
        }
    }
    public void updateSupplier(SupplierDTO dto){
        if(StringUtils.isNotBlank(dto.getName())){
            this.name = dto.getName();
        }
        if(StringUtils.isNotBlank(dto.getAddress())){
            this.address = dto.getAddress();
        }
        if(StringUtils.isNotBlank(dto.getPhoneNumber())){
            this.phoneNumber = dto.getPhoneNumber();
        }
        if(StringUtils.isNotBlank(dto.getTaxNumber())){
            this.taxNumber = dto.getTaxNumber();
        }
    }
}
