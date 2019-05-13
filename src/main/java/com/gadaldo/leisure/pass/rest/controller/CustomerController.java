package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.service.CustomerPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerPersistenceService customerPersistenceService;

    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerResourceI newCustomer) {
        log.info("New customer: {}", newCustomer);

        try {
            return new ResponseEntity<>("{\"id\": " + customerPersistenceService.save(newCustomer).getId() + "}", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new ResourceNotFoundException("Error saving customer", e);
        }
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerResourceO> getCustomer(@PathVariable Long customerId) {
        log.info("Get customer: {}", customerId);

        CustomerResourceO optCustomer = customerPersistenceService.findById(customerId);

        return new ResponseEntity<>(optCustomer, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResourceO>> getAllCustomers() {
        log.info("List customers");

        List<CustomerResourceO> customers = customerPersistenceService.findAll();

        if (customers != null && !customers.isEmpty())
            return new ResponseEntity<>(customers, HttpStatus.OK);
        throw new ResourceNotFoundException("No existing customers found");
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {
        log.info("Delete customer: {}", customerId);
        try {
            customerPersistenceService.deleteCustomer(customerId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("No customer found");
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity handleException(ResourceNotFoundException resourceNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resourceNotFoundException.getMessage());
    }

}
