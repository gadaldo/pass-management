package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
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
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class CustomerControllerTest {

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
    public void getCustomersShouldReturnNoCustomerFoundMessage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/pass-management/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(containsString("No existing customers found"));
    }

    @Test
    public void getCustomerShouldReturnNoCustomerFoundMessage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/pass-management/customers/1")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(containsString("Customer not found by id: 1"));
    }

    @Test
    public void getCustomerShouldReturnExistingCustomer() {
        // given
        Customer createdCustomer = customerRepository.save(newCustomer());

        //when
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/pass-management/customers/" + createdCustomer.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Sarah"))
                .body(containsString("Wood"))
                .body(containsString("London"));
    }

    @Test
    public void createCustomerShouldReturnCreatedCustomerId() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\":\"Alice\", \"surname\":\"McCarthy\", \"homeCity\":\"Naples\"}")
                .post("/pass-management/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body(containsString("Created customer: 1"));
    }

    @Test
    public void deleteCustomerShouldReturnCreatedCustomerId() {
        // given
        Customer createdCustomer = customerRepository.save(newCustomer());

        //when
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .delete("/pass-management/customers/" + createdCustomer.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Customer deleted"));
    }

    private Customer newCustomer() {
        return Customer.builder().homeCity("London").name("Sarah").surname("Wood").build();
    }

}
