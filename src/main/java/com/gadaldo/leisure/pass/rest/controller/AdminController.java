package com.gadaldo.leisure.pass.rest.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.stream.IntStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.service.CustomerPersistenceService;
import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final CustomerPersistenceService customerPersistenceService;

    private static final Faker faker = new Faker();

    @PostMapping
    @RequestMapping("__admin/random-customers")
    public ResponseEntity<String> createRandomCustomers(@RequestBody int nCustomers) {
        log.info("Creating {} random customers", nCustomers);

        customerPersistenceService.insertAll(
                IntStream.rangeClosed(1, nCustomers)
                        .mapToObj(i -> createRandomCustomer())
                        .collect(toList()));

        return new ResponseEntity<>(CREATED);
    }

    @DeleteMapping
    @RequestMapping("__admin/delete-customers")
    public ResponseEntity<String> deleteAllCustomers() {
        customerPersistenceService.deleteAll();

        return new ResponseEntity<>(NO_CONTENT);
    }

    private static CustomerResourceI createRandomCustomer() {
        return CustomerResourceI.builder()
                .homeCity(faker.address().city())
                .name(faker.name().firstName())
                .surname(faker.name().lastName())
                .build();
    }

}