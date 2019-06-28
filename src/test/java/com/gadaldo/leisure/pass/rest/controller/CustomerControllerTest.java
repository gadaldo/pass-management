package com.gadaldo.leisure.pass.rest.controller;

import static com.gadaldo.leisure.pass.util.CustomerUtil.newCustomer;
import static com.gadaldo.leisure.pass.util.CustomerUtil.newCustomerResourceI;
import static com.gadaldo.leisure.pass.util.CustomerUtil.newCustomerResourceO;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

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
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.util.PassUtil;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class CustomerControllerTest {

	private static final Customer SARAH_WOOD = newCustomer(1L, "Sarah", "Wood", "London");

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

	@Autowired
	private PassRepository passRepository;

	@Test
	public void getCustomersShouldReturnNoCustomerFoundMessage() {
		when().get("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(containsString("No customer found"));
	}

	@Test
	public void getCustomersShouldReturnCustomerList() {
		Customer customer1 = customerRepository.save(SARAH_WOOD);
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
		Customer createdCustomer = customerRepository.save(SARAH_WOOD);

		CustomerResourceO expected = CustomerResourceO.builder()
				.name("Sarah")
				.surname("Wood")
				.homeCity("London")
				.passes(emptySet())
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
				.body(newCustomerResourceI("Sarah", "Wood", "London"))
				.when().post("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.path("id");

		assertThat(customerId, notNullValue());
	}

	@Test
	public void createCustomerWithPassShouldReturnCreatedCustomerId() {
		Set<PassResourceI> passesRes = new HashSet<>();
		passesRes.add(PassUtil.newPassResourceI("New York", 7));
		Integer customerId = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(newCustomerResourceI("Sarah", "Wood", "London", passesRes))
				.when().post("/pass-management/customers")
				.then().assertThat()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.path("id");

		assertThat(customerId, notNullValue());

		Pass pass = passRepository.findByCustomerId(customerId.longValue()).iterator().next();
		assertThat(pass.getLength(), is(7));
		assertThat(pass.getCity(), is("New York"));
		assertThat(pass.getCustomer().getId(), is(customerId.longValue()));
	}

	@Test
	public void deleteCustomerShouldReturnEmptyBody() {
		// given
		Customer createdCustomer = customerRepository.save(SARAH_WOOD);

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

}
