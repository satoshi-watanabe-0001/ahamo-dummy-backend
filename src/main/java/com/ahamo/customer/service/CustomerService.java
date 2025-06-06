package com.ahamo.customer.service;

import com.ahamo.customer.model.Customer;
import com.ahamo.customer.repository.CustomerRepository;
import com.ahamo.security.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final SecurityAuditService securityAuditService;

    @Transactional
    public Customer createCustomer(Customer customer, HttpServletRequest request) {
        if (customer.getContractNumber() != null && 
            customerRepository.existsByContractNumber(customer.getContractNumber())) {
            throw new IllegalArgumentException("Contract number already exists");
        }

        if (customer.getEmail() != null && 
            customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (customer.getPhone() != null && 
            customerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        Customer savedCustomer = customerRepository.save(customer);
        
        String ipAddress = securityAuditService.getClientIpAddress(request);
        securityAuditService.logPersonalDataChange(
            "CUSTOMER_CREATED", 
            ipAddress, 
            "Customer created with ID: " + savedCustomer.getId(),
            savedCustomer.getId()
        );
        
        log.info("Customer created with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerUpdate, HttpServletRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));

        if (customerUpdate.getFirstName() != null) {
            existingCustomer.setFirstName(customerUpdate.getFirstName());
        }
        if (customerUpdate.getLastName() != null) {
            existingCustomer.setLastName(customerUpdate.getLastName());
        }
        if (customerUpdate.getFirstNameKana() != null) {
            existingCustomer.setFirstNameKana(customerUpdate.getFirstNameKana());
        }
        if (customerUpdate.getLastNameKana() != null) {
            existingCustomer.setLastNameKana(customerUpdate.getLastNameKana());
        }
        if (customerUpdate.getBirthDate() != null) {
            existingCustomer.setBirthDate(customerUpdate.getBirthDate());
        }
        if (customerUpdate.getGender() != null) {
            existingCustomer.setGender(customerUpdate.getGender());
        }
        if (customerUpdate.getPhone() != null) {
            existingCustomer.setPhone(customerUpdate.getPhone());
        }
        if (customerUpdate.getEmail() != null) {
            existingCustomer.setEmail(customerUpdate.getEmail());
        }
        if (customerUpdate.getAddress() != null) {
            existingCustomer.setAddress(customerUpdate.getAddress());
        }

        Customer savedCustomer = customerRepository.save(existingCustomer);
        
        String ipAddress = securityAuditService.getClientIpAddress(request);
        securityAuditService.logPersonalDataChange(
            "CUSTOMER_UPDATED", 
            ipAddress, 
            "Customer updated with ID: " + savedCustomer.getId(),
            savedCustomer.getId()
        );
        
        log.info("Customer updated with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    public Optional<Customer> findById(Long id, HttpServletRequest request) {
        Optional<Customer> customer = customerRepository.findById(id);
        
        if (customer.isPresent()) {
            String ipAddress = securityAuditService.getClientIpAddress(request);
            securityAuditService.logPersonalDataAccess(
                "CUSTOMER_ACCESSED", 
                ipAddress, 
                "Customer accessed with ID: " + id,
                id
            );
        }
        
        return customer;
    }

    public List<Customer> findAll(HttpServletRequest request) {
        List<Customer> customers = customerRepository.findAll();
        
        String ipAddress = securityAuditService.getClientIpAddress(request);
        securityAuditService.logPersonalDataAccess(
            "CUSTOMER_LIST_ACCESSED", 
            ipAddress, 
            "Customer list accessed, count: " + customers.size(),
            null
        );
        
        return customers;
    }

    public List<Customer> searchCustomers(String contractNumber, String phone, HttpServletRequest request) {
        List<Customer> customers;
        
        if (contractNumber != null && phone != null) {
            customers = customerRepository.findByContractNumberOrPhoneStartingWith(contractNumber, phone);
        } else if (contractNumber != null) {
            customers = customerRepository.findByContractNumberStartingWith(contractNumber);
        } else if (phone != null) {
            customers = customerRepository.findByPhoneStartingWith(phone);
        } else {
            customers = customerRepository.findAll();
        }
        
        String ipAddress = securityAuditService.getClientIpAddress(request);
        securityAuditService.logPersonalDataAccess(
            "CUSTOMER_SEARCH", 
            ipAddress, 
            "Customer search performed, results: " + customers.size(),
            null
        );
        
        return customers;
    }

    public boolean verifyCustomer(String contractNumber, String phone, HttpServletRequest request) {
        boolean exists = false;
        
        if (contractNumber != null) {
            exists = customerRepository.existsByContractNumber(contractNumber);
        }
        
        if (!exists && phone != null) {
            exists = customerRepository.existsByPhone(phone);
        }
        
        String ipAddress = securityAuditService.getClientIpAddress(request);
        securityAuditService.logPersonalDataAccess(
            "CUSTOMER_VERIFICATION", 
            ipAddress, 
            "Customer verification performed, result: " + exists,
            null
        );
        
        return exists;
    }
}
