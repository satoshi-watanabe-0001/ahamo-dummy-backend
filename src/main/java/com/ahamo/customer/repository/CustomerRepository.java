package com.ahamo.customer.repository;

import com.ahamo.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByContractNumber(String contractNumber);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    List<Customer> findByContractNumberStartingWith(String contractNumber);

    List<Customer> findByPhoneStartingWith(String phone);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:contractNumber IS NULL OR c.contractNumber LIKE :contractNumber%) AND " +
           "(:phone IS NULL OR c.phone LIKE :phone%)")
    List<Customer> findByContractNumberOrPhoneStartingWith(
        @Param("contractNumber") String contractNumber,
        @Param("phone") String phone);

    boolean existsByContractNumber(String contractNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
