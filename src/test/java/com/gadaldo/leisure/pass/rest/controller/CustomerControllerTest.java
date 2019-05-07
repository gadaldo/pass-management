package com.gadaldo.leisure.pass.rest.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTest {

    @Before
    public void setup() {
        RestAssured.port = Integer.valueOf(port);
    }

    @LocalServerPort
    private String port;

    @Test
    public void customersShouldReturnNoCustomerFoundMessage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/pass-management/customers")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(containsString("No existing customers found"));
    }

}
