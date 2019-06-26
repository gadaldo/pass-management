package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;
import com.gadaldo.leisure.pass.util.CustomerUtil;
import com.gadaldo.leisure.pass.util.PassUtil;
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

import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class PassControllerTest {

    private static final Customer JOHN_SMITH = CustomerUtil.newCustomer(1L, "John", "Smith", "Brighton");

    private static final PassResourceI MILAN_PASS = PassResourceI.builder()
            .city("Milan")
            .length(2)
            .build();

    @Before
    public void setup() {
        RestAssured.port = Integer.valueOf(port);
    }

    @After
    public void teardown() {
        customerRepository.deleteAll();
        passRepository.deleteAll();
    }

    @LocalServerPort
    private String port;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PassRepository passRepository;

    @Test
    public void getPassesShouldReturnNoPassFoundWhenThereAreNoPassesForGivenCustomer() {
        when()
                .get("pass-management/customers/1/passes")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(containsString("No pass found"));
    }

    @Test
    public void getPassesShouldReturnPassForGivenCustomer() {
        Customer customer = customerRepository.save(JOHN_SMITH);
        Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

        CustomerResourceO expected = CustomerResourceO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .homeCity(customer.getHomeCity())
                .passes(singleton(PassResourceO.builder()
                        .city(pass.getCity())
                        .length(pass.getLength())
                        .id(pass.getId())
                        .build()))
                .build();

        assertThat(when().get("pass-management/customers/" + customer.getId() + "/passes")
                        .then().assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .as(CustomerResourceO.class),
                is(expected));
    }

    @Test
    public void addPassesShouldReturnNewPassForGivenCustomer() {
        Customer customer = customerRepository.save(JOHN_SMITH);

        PassResourceO returned = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(MILAN_PASS).post("pass-management/customers/" + customer.getId() + "/passes")
                .then().assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(PassResourceO.class);

        assertThat(returned.getCity(), equalTo("Milan"));
        assertThat(returned.getLength(), equalTo(2));
        assertThat(returned.getId(), notNullValue());
    }

    @Test
    public void addPassesShouldReturnNoPassFoundWhenCustomerDoesNotExists() {
        given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(MILAN_PASS).post("pass-management/customers/404/passes")
                .then().assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(equalTo("Customer Not Found"));
    }

    @Test
    public void updatePassesShouldReturnPassNotFoundWhenCustomerDoesNotExists() {
        given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(MILAN_PASS).put("pass-management/customers/404/passes/303")
                .then().assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(equalTo("Pass Not Found"));
    }

    @Test
    public void updatePassesShouldReturnPass() {
        Customer customer = customerRepository.save(JOHN_SMITH);
        Pass pass = passRepository.save(Pass.builder().city("Naples").createdAt(new Date()).length(3).customer(customer).build());

        PassResourceO expected = PassResourceO.builder()
                .city("Naples")
                .id(pass.getId())
                .length(5)
                .build();

        assertThat(given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(PassUtil.newPassResourceI("Naples", 5)).put("pass-management/customers/" + customer.getId() + "/passes/" + pass.getId())
                .then().assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(PassResourceO.class), equalTo(expected));
    }

    @Test
    public void deletePassesShouldReturnNoContent() {
        Customer customer = customerRepository.save(JOHN_SMITH);
        Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

        when().delete("pass-management/customers/" + customer.getId() + "/passes/" + pass.getId())
                .then().assertThat()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void deletePassesShouldReturnNoCustomerFound() {
        Customer customer = customerRepository.save(JOHN_SMITH);

        when().delete("pass-management/customers/" + customer.getId() + "/passes/404")
                .then().assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(equalTo("Pass Not Found"));
    }

}
