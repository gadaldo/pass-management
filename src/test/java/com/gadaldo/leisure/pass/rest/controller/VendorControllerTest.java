package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import io.restassured.RestAssured;
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

import java.util.Calendar;
import java.util.Date;

import static io.restassured.RestAssured.when;
import static java.util.Calendar.DATE;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")


public class VendorControllerTest {

    @LocalServerPort
    private String port;

    @Autowired
    private PassRepository passRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Before
    public void setup() {
        RestAssured.port = Integer.valueOf(port);
    }

    @After
    public void teardown() {
        customerRepository.deleteAll();
        passRepository.deleteAll();
    }

    @Test
    public void validatePassShouldReturnValid() {
        Customer customer = customerRepository.save(newCustomer());
        Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

        when().get("pass-management/vendors/some_vendor/passes/" + pass.getId() + "/validate")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Pass is valid"));
    }

    @Test
    public void validatePassShouldReturnPassNotFoundWhenPassNotFound() {
        Customer customer = customerRepository.save(newCustomer());
        passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

        when().get("pass-management/vendors/some_vendor/passes/404/validate")
                .then().assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(containsString("Pass Not Found"));
    }

    @Test
    public void validatePassShouldReturnNotValidWhenPassIsExpired() {
        Calendar created5DaysAgo = Calendar.getInstance();
        created5DaysAgo.set(DATE, created5DaysAgo.get(DATE) - 5);

        Customer customer = customerRepository.save(newCustomer());
        Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(created5DaysAgo.getTime()).length(3).customer(customer).build());

        when().get("pass-management/vendors/some_vendor/passes/" + pass.getId() + "/validate")
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(containsString("Pass is expired"));
    }

    private Customer newCustomer() {
        return Customer.builder().name("John").surname("Smith").homeCity("Brighton").build();
    }
}
