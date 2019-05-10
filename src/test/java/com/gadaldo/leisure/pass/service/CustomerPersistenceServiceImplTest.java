package com.gadaldo.leisure.pass.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerPersistenceServiceImplTest {

	@Rule
	public ExpectedException ee = ExpectedException.none();

	@Mock
	private CustomerRepository customerRepositoryMock;

	@InjectMocks
	private CustomerPersistenceServiceImpl testObj;

	@Test
	public void shouldSaveCustomer() {
		CustomerResourceI to = new CustomerResourceI();
		to.setHomeCity("London");
		to.setName("Alex");
		to.setSurname("Wood");

		when(customerRepositoryMock.save(any(Customer.class))).thenReturn(newCustomer(1l, "Alex", "Wood", "London"));

		Customer customer = testObj.save(to);

		assertEquals("Alex", customer.getName());
		assertEquals("Wood", customer.getSurname());
		assertEquals("London", customer.getHomeCity());
		assertNotNull(customer.getId());
	}

	@Test
	public void shouldThrowNPEWhenCustomerIsNull() {
		ee.expect(NullPointerException.class);

		testObj.save(null);
	}

	@Test
	public void shouldFindCustomerById() {
		when(customerRepositoryMock.findById(1l))
				.thenReturn(Optional.of(newCustomer(1l, "Alex", "Wood", "London")));

		CustomerResourceO customer = testObj.findById(1l);

		assertEquals("Alex", customer.getName());
		assertEquals("Wood", customer.getSurname());
		assertEquals("London", customer.getHomeCity());
		assertNotNull(customer.getId());

		verify(customerRepositoryMock, times(1)).findById(1l);
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Customer not found");

		when(customerRepositoryMock.findById(1l)).thenReturn(Optional.empty());

		testObj.findById(1l);
	}

	@Test
	public void shouldThrowNPEWhenIdIsNull() {
		ee.expect(NullPointerException.class);

		when(customerRepositoryMock.findById(null)).thenThrow(new NullPointerException());

		testObj.findById(null);
	}

	@Test
	public void shouldFindListOfCustomers() {
		List<Customer> existingCustomers = new ArrayList<>();
		existingCustomers.add(newCustomer(1l, "John", "Dooh", "Milan"));
		existingCustomers.add(newCustomer(2l, "Alex", "Ferguson", "Paris"));
		existingCustomers.add(newCustomer(3l, "Sarah", "Williams", "Madrid"));

		List<CustomerResourceO> expectedCustomers = new ArrayList<>();
		expectedCustomers.add(newCustomerResource(1l, "John", "Dooh", "Milan"));
		expectedCustomers.add(newCustomerResource(2l, "Alex", "Ferguson", "Paris"));
		expectedCustomers.add(newCustomerResource(3l, "Sarah", "Williams", "Madrid"));

		when(customerRepositoryMock.findAll()).thenReturn(existingCustomers);

		List<CustomerResourceO> retrievedCustomers = testObj.findAll();

		assertEquals(expectedCustomers, retrievedCustomers);
		verify(customerRepositoryMock, times(1)).findAll();
	}

	@Test
	public void shouldReturnEmptyListWhenNoCustomers() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("No customer found");

		when(customerRepositoryMock.findAll()).thenReturn(Collections.emptyList());

		testObj.findAll();
	}

	@Test
	public void shouldDeleteCustomer() {
		doNothing().when(customerRepositoryMock).deleteById(1l);

		testObj.deleteCustomer(1l);

		verify(customerRepositoryMock, times(1)).deleteById(1l);
		verify(customerRepositoryMock, never()).delete(any(Customer.class));
	}

	private Customer newCustomer(Long id, String name, String surname, String homeCity) {
		return Customer.builder()
				.id(id)
				.name(name)
				.surname(surname)
				.homeCity(homeCity)
				.build();
	}

	private CustomerResourceO newCustomerResource(Long id, String name, String surname, String homeCity) {
		return CustomerResourceO.builder()
				.id(id)
				.name(name)
				.surname(surname)
				.homeCity(homeCity)
				.build();
	}
}
