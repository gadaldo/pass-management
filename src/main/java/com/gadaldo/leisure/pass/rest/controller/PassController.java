package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;
import com.gadaldo.leisure.pass.service.PassPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PassController {

    private final PassPersistenceService passPersistenceService;

    @GetMapping("/customers/{customerId}/passes")
    public ResponseEntity<CustomerResourceO> getPasses(@PathVariable Long customerId) {
        log.info("Get passes for customer: {}", customerId);

        CustomerResourceO passes = passPersistenceService.findByCustomerId(customerId);

        return new ResponseEntity<>(passes, HttpStatus.OK);
    }

    @PostMapping("/customers/{customerId}/passes")
    public ResponseEntity<PassResourceO> addPass(@PathVariable Long customerId, @RequestBody PassResourceI addPass) {
        log.info("Add pass: {}", addPass);

        PassResourceO passId = passPersistenceService.addPassToCustomer(customerId, addPass);

        return new ResponseEntity<>(passId, HttpStatus.CREATED);
    }

    @PutMapping("/customers/{customerId}/passes/{passId}")
    public ResponseEntity<PassResourceO> updatePass(@PathVariable Long customerId, @PathVariable Long passId, @RequestBody PassResourceI passTO) {
        log.info("Update pass: {}", passId);

        PassResourceO updatedPass = passPersistenceService.updateCustomerPass(customerId, passId, passTO);

        return new ResponseEntity<>(updatedPass, HttpStatus.OK);
    }

    @DeleteMapping("/customers/{customerId}/passes/{passId}")
    public ResponseEntity<?> deletePost(@PathVariable Long customerId, @PathVariable Long passId) {
        log.info("Delete pass: {}", passId);

        passPersistenceService.deletePass(customerId, passId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
