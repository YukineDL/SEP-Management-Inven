package com.inventorymanagement.entity;

import com.inventorymanagement.dto.CustomerDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "website")
    private String website;

    public void updateCustomer(CustomerDTO dto) {
        this.name = dto.getName();
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.email = dto.getEmail();
        this.website = dto.getWebsite();
    }
}
