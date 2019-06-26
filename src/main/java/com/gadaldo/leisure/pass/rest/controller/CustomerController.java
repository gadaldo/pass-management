package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.service.CustomerPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerPersistenceService customerPersistenceService;

    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerResourceI newCustomer) {
        log.info("New customer: {}", newCustomer);

        return new ResponseEntity<>("{\"id\": " + customerPersistenceService.save(newCustomer).getId() + "}", CREATED);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerResourceO> getCustomer(@PathVariable Long customerId) {
        log.info("Get customer: {}", customerId);

        CustomerResourceO optCustomer = customerPersistenceService.findById(customerId);

        return new ResponseEntity<>(optCustomer, OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResourceO>> getAllCustomers() {
        log.info("List customers");

        List<CustomerResourceO> customers = customerPersistenceService.findAll();

        return new ResponseEntity<>(customers, OK);
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {
        log.info("Delete customer: {}", customerId);
        try {
            customerPersistenceService.deleteCustomer(customerId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("No customer found");
        }

        return new ResponseEntity<>(NO_CONTENT);
    }

}
