package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;
import com.gadaldo.leisure.pass.service.PassPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PassController {

    private final PassPersistenceService passPersistenceService;

    @GetMapping("/customers/{customerId}/passes")
    public ResponseEntity<List<PassResourceO>> getPasses(@PathVariable Long customerId) {
        log.info("Get passes for customer: {}", customerId);

        List<PassResourceO> passes = passPersistenceService.findByCustomerId(customerId);

        if (passes != null && !passes.isEmpty())
            return new ResponseEntity<>(passes, HttpStatus.OK);
        throw new ResourceNotFoundException("No pass found for customer id: " + customerId);
    }

    @PostMapping("/customers/{customerId}/passes")
    public ResponseEntity<String> addPass(@PathVariable Long customerId, @RequestBody PassResourceI addPass) {
        log.info("Add pass: {}", addPass);

        Long passId = passPersistenceService.addPassToCustomer(customerId, addPass);

        return new ResponseEntity<>("Pass added: " + passId, HttpStatus.CREATED);
    }

    @PutMapping("/customers/{customerId}/passes/{passId}")
    public ResponseEntity<String> updatePass(@PathVariable Long customerId, @PathVariable Long passId, @RequestBody PassResourceI passTO) {
        log.info("Update pass: {}", passId);

        Pass updatedPass = passPersistenceService.updateCustomerPass(customerId, passId, passTO);

        return new ResponseEntity<>("Pass updated: " + updatedPass, HttpStatus.CREATED);
    }

    @DeleteMapping("/customers/{customerId}/passes/{passId}")
    public ResponseEntity<String> deletePost(@PathVariable Long customerId, @PathVariable Long passId) {
        log.info("Delete pass: {}", passId);

        boolean updatedPass = passPersistenceService.deletePass(customerId, passId);

        return new ResponseEntity<>("Pass deleted: " + updatedPass, HttpStatus.OK);
    }

}
