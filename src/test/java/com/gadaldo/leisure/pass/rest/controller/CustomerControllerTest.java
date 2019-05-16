package com.gadaldo.leisure.pass.rest.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
	public void getCustomersShouldReturnCustomerList() {
		Customer customer1 = customerRepository.save(newCustomer());
		Customer customer2 = customerRepository.save(Customer.builder().name("Andrew").surname("Ray").homeCity("London").build());

		CustomerResourceO[] expected = new CustomerResourceO[] {
				newCustomerResourceO(customer1.getId(), "Sarah", "Wood", "London"),
				newCustomerResourceO(customer2.getId(), "Andrew", "Ray", "London") };

		assertThat(when().get("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.as(CustomerResourceO[].class),
				equalTo(expected));
	}

	@Test
	public void getCustomerShouldReturnNoCustomerFoundMessage() {
		when().get("/pass-management/customers/1")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(equalTo("Customer not found"));
	}

	@Test
	public void getCustomerByExistingIdShouldReturnExistingCustomer() {
		// given
		Customer createdCustomer = customerRepository.save(newCustomer());

		CustomerResourceO expected = CustomerResourceO.builder()
				.name("Sarah")
				.surname("Wood")
				.homeCity("London")
				.id(createdCustomer.getId())
				.build();

		assertThat(when().get("/pass-management/customers/" + createdCustomer.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.as(CustomerResourceO.class),
				is(expected));
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
	public void deleteCustomerShouldReturnEmptyBody() {
		// given
		Customer createdCustomer = customerRepository.save(newCustomer());

		when().delete("/pass-management/customers/" + createdCustomer.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_NO_CONTENT)
				.body(isEmptyOrNullString());
	}

	@Test
	public void deleteCustomerShouldReturn404WhenCustomerNotFound() {
		when().delete("/pass-management/customers/404")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(equalTo("No customer found"));
	}

	private Customer newCustomer() {
		return Customer.builder().homeCity("London").name("Sarah").surname("Wood").build();
	}

	private CustomerResourceI newCustomerResourceI() {
		CustomerResourceI customerResource = new CustomerResourceI();
		customerResource.setHomeCity("Naples");
		customerResource.setName("Alice");
		customerResource.setSurname("Wonderland");
		return customerResource;
	}

	private CustomerResourceO newCustomerResourceO(Long id, String name, String surname, String homeCity) {
		return CustomerResourceO.builder()
				.id(id)
				.homeCity(homeCity)
				.name(name)
				.surname(surname).build();
	}

}
