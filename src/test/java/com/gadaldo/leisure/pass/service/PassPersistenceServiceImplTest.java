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
import com.gadaldo.leisure.pass.rest.model.PassResourceI;

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

		when(passRepositoryMock.save(any(Pass.class))).thenReturn(newPass(1l, "London", 2, new Date()));

		when(customerRepositoryMock.findById(1l)).thenReturn(Optional.of(Customer.builder().id(1l).build()));

		Long passId = testObj.addPassToCustomer(1l, to);

		assertThat(passId, equalTo(1l));
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
		List<Pass> expectedPasses = new ArrayList<>();
		expectedPasses.add(newPass(3l, "London", 3, new Date(), customer));
		expectedPasses.add(newPass(4l, "Milan", 2, new Date(), customer));
		expectedPasses.add(newPass(5l, "Madrid", 3, new Date(), customer));

		when(testObj.findByCustomerId(1l)).thenReturn(expectedPasses);

		List<Pass> retrievedPasses = testObj.findByCustomerId(1l);

		assertEquals(expectedPasses, retrievedPasses);
		assertEquals(customer, retrievedPasses.get(0).getCustomer());
		verify(passRepositoryMock, times(1)).findByCustomerId(anyLong());
	}

	@Test
	public void shouldReturnEmptyListWhenCustomerHasNoPasses() {
		when(passRepositoryMock.findByCustomerId(1l)).thenReturn(emptyList());

		List<Pass> retrievedPasses = testObj.findByCustomerId(1l);

		assertEquals(emptyList(), retrievedPasses);
		verify(passRepositoryMock, times(1)).findByCustomerId(anyLong());
	}

	@Test
	public void shouldUpdateCustomerPass() {
		PassResourceI to = new PassResourceI();
		to.setCity("London");
		to.setLenght(10);

		Pass pass = newPass(10l, "London", 3, new Date(), Customer.builder().id(1l).build());

		when(passRepositoryMock.findByIdAndCustomerId(10l, 1l)).thenReturn(Optional.of(pass));

		when(passRepositoryMock.save(pass)).thenReturn(pass);

		Pass updatedPass = testObj.updateCustomerPass(1l, 10l, to);

		assertEquals(10, updatedPass.getLenght());

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
	public void shouldThrowResorceNotFoundExceptionOnValidatingWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		when(passRepositoryMock.findById(1l)).thenReturn(Optional.empty());

		testObj.isValid(1l);
	}

	private Pass newPass(Long id, String city, int lenght, Date date) {
		return newPass(id, city, lenght, date, null);
	}

	private Pass newPass(Long id, String city, int lenght, Date date, Customer customer) {
		return Pass.builder()
				.id(id)
				.lenght(lenght)
				.createdAt(date)
				.city(city)
				.customer(customer)
				.build();
	}

}
