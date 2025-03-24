package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByPhoneNumber(String email);
    @Query(value = """
        SELECT * FROM customer c WHERE c.phone_number LIKE CONCAT('%', :phoneNumber, '%')
""", nativeQuery = true)
    List<Customer> findByPhoneNumberLike(String phoneNumber);

}
