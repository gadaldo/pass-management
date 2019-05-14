package com.gadaldo.leisure.pass.rest.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import com.gadaldo.leisure.pass.rest.model.CustomerPassResourceO;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

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
				.body(containsString("No pass found"));
	}

	@Test
	public void getPassesShouldReturnPassForGivenCustomer() {
		Customer customer = customerRepository.save(newCustomer());
		Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

		CustomerPassResourceO expected = CustomerPassResourceO.builder()
				.customer(CustomerResourceO.builder()
						.id(customer.getId())
						.name(customer.getName())
						.surname(customer.getSurname())
						.homeCity(customer.getHomeCity())
						.build())
				.passes(singletonList(PassResourceO.builder()
						.city(pass.getCity())
						.length(pass.getLength())
						.id(pass.getId())
						.build()))
				.build();

		assertThat(when().get("pass-management/customers/" + customer.getId() + "/passes")
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.as(CustomerPassResourceO.class),
				is(expected));
	}

	@Test
	public void addPassesShouldReturnNewPassForGivenCustomer() {
		Customer customer = customerRepository.save(newCustomer());

		PassResourceI requestBody = new PassResourceI();
		requestBody.setCity("Milan");
		requestBody.setLenght(2);

		PassResourceO returned = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(requestBody).post("pass-management/customers/" + customer.getId() + "/passes")
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
		PassResourceI requestBody = new PassResourceI();
		requestBody.setCity("Milan");
		requestBody.setLenght(2);

		given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(requestBody).post("pass-management/customers/404/passes")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(Matchers.containsString("Customer Not Found"));
	}

	@Test
	public void updatePassesShouldReturnPassNotFoundWhenCustomerDoesNotExists() {
		PassResourceI requestBody = new PassResourceI();
		requestBody.setCity("Milan");
		requestBody.setLenght(2);

		given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(requestBody).put("pass-management/customers/404/passes/303")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(Matchers.containsString("Pass Not Found"));
	}

	@Test
	public void updatePassesShouldReturnPass() {
		Customer customer = customerRepository.save(newCustomer());
		Pass pass = passRepository.save(Pass.builder().city("Naples").createdAt(new Date()).length(3).customer(customer).build());

		PassResourceI requestBody = new PassResourceI();
		requestBody.setCity("Naples");
		requestBody.setLenght(10);

		PassResourceO expected = PassResourceO.builder()
				.city("Naples")
				.id(pass.getId())
				.length(10)
				.build();

		assertThat(given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(requestBody).put("pass-management/customers/" + customer.getId() + "/passes/" + pass.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.as(PassResourceO.class), equalTo(expected));
	}

	@Test
	@Ignore
	public void updatePassesShouldCreateNewPassWhenItDoesNotExist() {
		Customer customer = customerRepository.save(newCustomer());

		PassResourceI requestBody = new PassResourceI();
		requestBody.setCity("Naples");
		requestBody.setLenght(10);

		given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
				.body(requestBody).put("pass-management/customers/" + customer.getId() + "/passes/100")
				.then().assertThat()
				.statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void deletePassesShouldReturnNoContent() {
		Customer customer = customerRepository.save(newCustomer());
		Pass pass = passRepository.save(Pass.builder().city("Chicago").createdAt(new Date()).length(3).customer(customer).build());

		when().delete("pass-management/customers/" + customer.getId() + "/passes/" + pass.getId())
				.then().assertThat()
				.statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void deletePassesShouldReturnNoCustomerFound() {
		Customer customer = customerRepository.save(newCustomer());

		when().delete("pass-management/customers/" + customer.getId() + "/passes/404")
				.then().assertThat()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(containsString("Pass Not Found"));
	}

	private Customer newCustomer() {
		return Customer.builder().name("John").surname("Smith").homeCity("Brighton").build();
	}
}
