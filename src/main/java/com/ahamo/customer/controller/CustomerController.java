package com.ahamo.customer.controller;

import com.ahamo.customer.dto.*;
import com.ahamo.customer.model.Customer;
import com.ahamo.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerInfoResponse> createCustomer(
            @Valid @RequestBody CustomerInfoRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Creating customer with contract number: {}", request.getContractNumber());
        
        try {
            Customer customer = request.toEntity();
            Customer savedCustomer = customerService.createCustomer(customer, httpRequest);
            CustomerInfoResponse response = CustomerInfoResponse.fromEntity(savedCustomer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error creating customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerInfoResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerInfoRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Updating customer with ID: {}", id);
        
        try {
            Customer customerUpdate = request.toEntity();
            Customer updatedCustomer = customerService.updateCustomer(id, customerUpdate, httpRequest);
            CustomerInfoResponse response = CustomerInfoResponse.fromEntity(updatedCustomer);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error updating customer: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerInfoResponse> getCustomer(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        log.info("Getting customer with ID: {}", id);
        
        Optional<Customer> customer = customerService.findById(id, httpRequest);
        
        if (customer.isPresent()) {
            CustomerInfoResponse response = CustomerInfoResponse.fromEntity(customer.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerInfoResponse>> getCustomers(
            @RequestParam(required = false) String contractNumber,
            @RequestParam(required = false) String phone,
            HttpServletRequest httpRequest) {
        
        log.info("Getting customers with contractNumber: {}, phone: {}", contractNumber, phone);
        
        List<Customer> customers;
        
        if (contractNumber != null || phone != null) {
            customers = customerService.searchCustomers(contractNumber, phone, httpRequest);
        } else {
            customers = customerService.findAll(httpRequest);
        }
        
        List<CustomerInfoResponse> responses = customers.stream()
            .map(CustomerInfoResponse::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/verify")
    public ResponseEntity<CustomerVerificationResponse> verifyCustomer(
            @Valid @RequestBody CustomerVerificationRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Verifying customer with contractNumber: {}, phone: {}", 
                request.getContractNumber(), request.getPhone());
        
        if (request.getContractNumber() == null && request.getPhone() == null) {
            CustomerVerificationResponse response = CustomerVerificationResponse.builder()
                .verified(false)
                .message("Contract number or phone number is required")
                .build();
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean verified = customerService.verifyCustomer(
            request.getContractNumber(), 
            request.getPhone(), 
            httpRequest
        );
        
        CustomerVerificationResponse response = CustomerVerificationResponse.builder()
            .verified(verified)
            .message(verified ? "Customer verified successfully" : "Customer not found")
            .build();
        
        return ResponseEntity.ok(response);
    }
}
