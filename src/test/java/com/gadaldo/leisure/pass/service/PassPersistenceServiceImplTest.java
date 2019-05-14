package com.gadaldo.leisure.pass.service;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerPassResourceO;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

@RunWith(MockitoJUnitRunner.class)
public class PassPersistenceServiceImplTest {

	@Rule
	public ExpectedException ee = ExpectedException.none();

	@Mock
	private PassRepository passRepositoryMock;

	@Mock
	private CustomerRepository customerRepositoryMock;

	@InjectMocks
	private PassPersistenceServiceImpl testObj;

	@Test
	public void shouldSavePassForGivenCustomer() {
		PassResourceI to = new PassResourceI();
		to.setCity("London");
		to.setLenght(2);

		PassResourceO expected = newPassResource(1l, "London", 2);

		when(passRepositoryMock.save(any(Pass.class))).thenReturn(newPass(1l, "London", 2, new Date()));

		when(customerRepositoryMock.findById(1l)).thenReturn(Optional.of(Customer.builder().id(1l).build()));

		PassResourceO passResO = testObj.addPassToCustomer(1l, to);

		assertThat(passResO, equalTo(expected));
		verify(customerRepositoryMock, times(1)).findById(anyLong());
		verify(passRepositoryMock, times(1)).save(any(Pass.class));
	}

	@Test
	public void shouldReturnNullWhenCustomerNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Customer Not Found");

		PassResourceI to = new PassResourceI();
		to.setCity("London");
		to.setLenght(2);

		when(customerRepositoryMock.findById(1l)).thenReturn(Optional.empty());

		testObj.addPassToCustomer(1l, to);
	}

	@Test
	public void shouldFindPassesForGivenCustomer() {
		Customer customer = Customer.builder().id(1l).build();
		List<Pass> existingPasses = new ArrayList<>();
		existingPasses.add(newPass(3L, "London", 3, new Date(), customer));
		existingPasses.add(newPass(4L, "Milan", 2, new Date(), customer));
		existingPasses.add(newPass(5L, "Madrid", 3, new Date(), customer));

		List<PassResourceO> expectedPasses = new ArrayList<>();
		expectedPasses.add(newPassResource(3L, "London", 3));
		expectedPasses.add(newPassResource(4L, "Milan", 2));
		expectedPasses.add(newPassResource(5L, "Madrid", 3));

		CustomerResourceO expectedCustomer = CustomerResourceO.builder()
				.id(customer.getId())
				.build();

		CustomerPassResourceO expectedResult = newCustomerPassResource(expectedPasses, expectedCustomer);

		when(passRepositoryMock.findByCustomerId(1L)).thenReturn(existingPasses);

		CustomerPassResourceO returnedPasses = testObj.findByCustomerId(1L);

		assertThat(returnedPasses, equalTo(expectedResult));
		assertEquals(expectedCustomer, returnedPasses.getCustomer());
		verify(passRepositoryMock, times(1)).findByCustomerId(anyLong());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenCustomerHasNoPasses() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("No pass found");
		when(passRepositoryMock.findByCustomerId(1L)).thenReturn(emptyList());

		testObj.findByCustomerId(1L);
	}

	@Test
	public void shouldUpdateCustomerPass() {
		PassResourceI inputRes = new PassResourceI();
		inputRes.setCity("London");
		inputRes.setLenght(10);

		Pass pass = newPass(10l, "London", 3, new Date(), Customer.builder().id(1l).build());

		when(passRepositoryMock.findByIdAndCustomerId(10l, 1l)).thenReturn(Optional.of(pass));

		when(passRepositoryMock.save(pass)).thenReturn(pass);

		PassResourceO updatedPass = testObj.updateCustomerPass(1l, 10l, inputRes);

		assertEquals(10, updatedPass.getLength());

		verify(passRepositoryMock, times(1)).findByIdAndCustomerId(anyLong(), anyLong());
		verify(passRepositoryMock, times(1)).save(any(Pass.class));
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnUpdateWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		PassResourceI to = new PassResourceI();
		to.setCity("London");
		to.setLenght(10);

		when(passRepositoryMock.findByIdAndCustomerId(10l, 1l)).thenReturn(Optional.empty());

		testObj.updateCustomerPass(1l, 10l, to);
	}

	@Test
	public void shouldDeletePassForGivenCustomer() {
		when(passRepositoryMock.findByIdAndCustomerId(10l, 1l)).thenReturn(Optional.of(newPass(10l, "London", 2, new Date())));
		assertTrue(testObj.deletePass(1l, 10l));

		verify(passRepositoryMock, times(1)).findByIdAndCustomerId(anyLong(), anyLong());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnDeleteWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		when(passRepositoryMock.findByIdAndCustomerId(10l, 1l)).thenReturn(Optional.empty());

		assertTrue(testObj.deletePass(1l, 10l));
	}

	@Test
	public void shouldReturnPassIsValid() {
		when(passRepositoryMock.findById(1l)).thenReturn(Optional.of(newPass(1l, "Oxford", 3, new Date())));
		assertTrue(testObj.isValid(1l));
	}

	@Test
	public void shouldReturnPassIsNotValid() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 10);

		when(passRepositoryMock.findById(1l)).thenReturn(Optional.of(newPass(1l, "Oxford", 3, calendar.getTime())));
		assertFalse(testObj.isValid(1l));
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnValidatingWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		when(passRepositoryMock.findById(1l)).thenReturn(Optional.empty());

		testObj.isValid(1l);
	}

	private CustomerPassResourceO newCustomerPassResource(List<PassResourceO> passes, CustomerResourceO customer) {
		return CustomerPassResourceO.builder()
				.customer(customer)
				.passes(passes)
				.build();
	}

	private PassResourceO newPassResource(Long id, String city, int length) {
		return PassResourceO.builder()
				.id(id)
				.city(city)
				.length(length)
				.build();
	}

	private Pass newPass(Long id, String city, int length, Date date) {
		return newPass(id, city, length, date, null);
	}

	private Pass newPass(Long id, String city, int length, Date date, Customer customer) {
		return Pass.builder()
				.id(id)
				.length(length)
				.createdAt(date)
				.city(city)
				.customer(customer)
				.build();
	}

}
