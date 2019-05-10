package com.gadaldo.leisure.pass.rest.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

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

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

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
		when().get("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(containsString("No customer found"));
	}

	@Test
	public void getCustomerShouldReturnNoCustomerFoundMessage() {
		when().get("/pass-management/customers/1")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(containsString("Customer not found"));
	}

	@Test
	public void getCustomerByExistingIdShouldReturnExistingCustomer() {
		// given
		Customer createdCustomer = customerRepository.save(newCustomer());

		CustomerResourceO expected = CustomerResourceO.builder()
				.name("Sarah")
				.surname("Wood")
				.homeCity("London")
				.build();

		when().get("/pass-management/customers/" + createdCustomer.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.as(CustomerResourceO.class)
				.equals(expected);
	}

	@Test
	public void createCustomerShouldReturnCreatedCustomerId() {
		Integer customerId = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(newCustomerResourceI())
				.when().post("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.path("id");

		assertThat(customerId, notNullValue());
	}

	@Test
	public void deleteCustomerShouldReturnCreatedCustomerId() {
		// given
		Customer createdCustomer = customerRepository.save(newCustomer());

		when().delete("/pass-management/customers/" + createdCustomer.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_NO_CONTENT)
				.body(isEmptyOrNullString());
	}

	private Customer newCustomer() {
		return Customer.builder().homeCity("London").name("Sarah").surname("Wood").build();
	}

	private CustomerResourceI newCustomerResourceI() {
		CustomerResourceI customerResource = new CustomerResourceI();
		customerResource.setHomeCity("Alice");
		customerResource.setName("Wonderland");
		customerResource.setSurname("Naples");
		return customerResource;
	}

}
