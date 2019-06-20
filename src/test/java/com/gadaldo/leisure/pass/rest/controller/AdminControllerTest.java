package com.gadaldo.leisure.pass.rest.controller;


import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class AdminControllerTest {

    @Before
    public void setup() {
        RestAssured.port = Integer.valueOf(port);
    }

    @After
    public void teardown() {
        customerRepository.deleteAll();
    }

    @LocalServerPort
    private String port;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void shouldNotCreateAnyCustomerWhenRequestingZero() {
        given()
                .headers("Content-Type", ContentType.JSON)
                .body(0)
                .post("/pass-management/__admin/random-customers")
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .body(containsString("No customer found"));

        assertThat(customerRepository.findAll().size(), is(0));
    }

    @Test
    public void shouldCreate10Customers() {
        given()
                .headers("Content-Type", ContentType.JSON)
                .body(10)
                .post("/pass-management/__admin/random-customers")
                .then()
                .assertThat()
                .statusCode(SC_CREATED);

        assertThat(customerRepository.findAll().size(), is(10));
    }

    @Test
    public void shouldDeleteAllCustomers() {
        customerRepository.save(Customer.builder().homeCity("someCity").name("aName").surname("aSurname").build());

        given()
                .delete("/pass-management/__admin/delete-customers")
                .then()
                .assertThat()
                .statusCode(SC_NO_CONTENT);

        assertThat(customerRepository.findAll().size(), is(0));
    }
}
