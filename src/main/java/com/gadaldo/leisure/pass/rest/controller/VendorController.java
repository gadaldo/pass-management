package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.service.PassPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VendorController {

    private final PassPersistenceService passPersistenceService;

    @GetMapping("/vendors/{vendorId}/passes/{passId}/validate")
    public ResponseEntity<String> validatePass(@PathVariable String vendorId, @PathVariable Long passId) {
        log.info("Validate Pass: {}", passId);
        String responseString = passPersistenceService.isValid(passId) ? "Pass is valid" : "Pass is expired";

        return new ResponseEntity<>(responseString, HttpStatus.OK);
    }

}
