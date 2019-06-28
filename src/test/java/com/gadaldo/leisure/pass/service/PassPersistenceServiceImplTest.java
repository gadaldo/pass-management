package com.gadaldo.leisure.pass.service;

import static com.gadaldo.leisure.pass.util.PassUtil.newPass;
import static com.gadaldo.leisure.pass.util.PassUtil.newPassResourceO;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

@RunWith(MockitoJUnitRunner.class)
public class PassPersistenceServiceImplTest {

	private static final PassResourceI LONDON_PASS = PassResourceI.builder()
			.city("London")
			.length(10)
			.build();

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
		PassResourceO expected = newPassResourceO(1L, "London", 2);

		when(passRepositoryMock.save(any(Pass.class))).thenReturn(newPass(1L, "London", 2, new Date()));

		when(customerRepositoryMock.findById(1L)).thenReturn(Optional.of(Customer.builder().id(1L).build()));

		PassResourceO passResO = testObj.addPassToCustomer(1L, LONDON_PASS);

		assertThat(passResO, equalTo(expected));
		verify(customerRepositoryMock, times(1)).findById(anyLong());
		verify(passRepositoryMock, times(1)).save(any(Pass.class));
	}

	@Test
	public void shouldReturnNullWhenCustomerNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Customer Not Found");

		when(customerRepositoryMock.findById(1L)).thenReturn(Optional.empty());

		testObj.addPassToCustomer(1L, LONDON_PASS);
	}

	@Test
	public void shouldFindPassesForGivenCustomer() {
		Customer customer = Customer.builder().id(1L).build();
		Set<Pass> existingPasses = new HashSet<>();
		existingPasses.add(newPass(3L, "London", 3, new Date(), customer));
		existingPasses.add(newPass(4L, "Milan", 2, new Date(), customer));
		existingPasses.add(newPass(5L, "Madrid", 3, new Date(), customer));

		Set<PassResourceO> expectedPasses = new HashSet<>();
		expectedPasses.add(newPassResourceO(3L, "London", 3));
		expectedPasses.add(newPassResourceO(4L, "Milan", 2));
		expectedPasses.add(newPassResourceO(5L, "Madrid", 3));

		CustomerResourceO expectedCustomer = CustomerResourceO.builder()
				.id(customer.getId())
				.passes(expectedPasses)
				.build();

		when(passRepositoryMock.findByCustomerId(1L)).thenReturn(existingPasses);

		CustomerResourceO returnedPasses = testObj.findByCustomerId(1L);

		assertThat(returnedPasses.getPasses(), containsInAnyOrder(expectedCustomer.getPasses().toArray()));
		verify(passRepositoryMock, times(1)).findByCustomerId(anyLong());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionWhenCustomerHasNoPasses() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("No pass found");
		when(passRepositoryMock.findByCustomerId(1L)).thenReturn(emptySet());

		testObj.findByCustomerId(1L);
	}

	@Test
	public void shouldUpdateCustomerPass() {
		Pass pass = newPass(10L, "London", 3, new Date(), Customer.builder().id(1L).build());

		when(passRepositoryMock.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.of(pass));

		when(passRepositoryMock.save(pass)).thenReturn(pass);

		PassResourceO updatedPass = testObj.updateCustomerPass(1L, 10L, LONDON_PASS);

		assertThat(updatedPass.getLength(), equalTo(10));

		verify(passRepositoryMock, times(1)).findByIdAndCustomerId(anyLong(), anyLong());
		verify(passRepositoryMock, times(1)).save(any(Pass.class));
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnUpdateWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		PassResourceI to = PassResourceI.builder()
				.city("London")
				.length(10)
				.build();

		when(passRepositoryMock.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.empty());

		testObj.updateCustomerPass(1L, 10L, to);
	}

	@Test
	public void shouldDeletePassForGivenCustomer() {
		when(passRepositoryMock.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.of(newPass(10L, "London", 2, new Date())));
		assertTrue(testObj.deletePass(1L, 10L));

		verify(passRepositoryMock, times(1)).findByIdAndCustomerId(anyLong(), anyLong());
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnDeleteWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		when(passRepositoryMock.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.empty());

		assertTrue(testObj.deletePass(1L, 10L));
	}

	@Test
	public void shouldReturnPassIsValid() {
		when(passRepositoryMock.findById(1L)).thenReturn(Optional.of(newPass(1L, "Oxford", 3, new Date())));
		assertTrue(testObj.isValid(1L));
	}

	@Test
	public void shouldReturnPassIsNotValid() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 10);

		when(passRepositoryMock.findById(1L)).thenReturn(Optional.of(newPass(1L, "Oxford", 3, calendar.getTime())));
		assertFalse(testObj.isValid(1L));
	}

	@Test
	public void shouldThrowResourceNotFoundExceptionOnValidatingWhenPassNotFound() {
		ee.expect(ResourceNotFoundException.class);
		ee.expectMessage("Pass Not Found");

		when(passRepositoryMock.findById(1L)).thenReturn(Optional.empty());

		testObj.isValid(1L);
	}

}
