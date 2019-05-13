package com.gadaldo.leisure.pass.rest.controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;
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

import java.util.Date;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class PassControllerTest {

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
                .body(containsString("No pass found for customer id: 1"));
    }

    @Test
    public void getPassesShouldReturnPassForGivenCustomer() {
        Customer customer = customerRepository.save(Customer.builder().name("John").surname("Smith").homeCity("Brighton").build());
        Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

        PassResourceO[] expected = new PassResourceO[]{PassResourceO.builder()
                .city("Chicago")
                .id(pass.getId())
                .length(3).build()};

        assertThat(when()
                        .get("pass-management/customers/" + customer.getId() + "/passes")
                        .then().assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .as(PassResourceO[].class),
                is(expected));
    }
}
