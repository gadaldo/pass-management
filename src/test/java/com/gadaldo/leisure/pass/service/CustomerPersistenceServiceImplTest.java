package com.gadaldo.leisure.pass.service;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static com.gadaldo.leisure.pass.util.CustomerUtil.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerPersistenceServiceImplTest {

    @Rule
    public ExpectedException ee = ExpectedException.none();

    @Mock
    private CustomerRepository customerRepositoryMock;

    @Mock
    private PassRepository passRepositoryMock;

    @InjectMocks
    private CustomerPersistenceServiceImpl testObj;

    @Test
    public void shouldSaveCustomerAndNotSavingPassWhenGivenPassesIsEmpty() {
        CustomerResourceI to = newCustomerResourceI("Alex", "Wood", "London");

        when(customerRepositoryMock.save(any(Customer.class)))
                .thenReturn(newCustomer(1L, "Alex", "Wood", "London"));

        Customer customer = testObj.save(to);

        assertEquals("Alex", customer.getName());
        assertEquals("Wood", customer.getSurname());
        assertEquals("London", customer.getHomeCity());
        assertNotNull(customer.getId());

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(passRepositoryMock, never()).saveAll(anyIterable());
        verify(passRepositoryMock, never()).save(any(Pass.class));
    }

    @Test
    public void shouldThrowNPEWhenCustomerIsNull() {
        ee.expect(NullPointerException.class);

        testObj.save(null);
    }

    @Test
    public void shouldFindCustomerById() {
        when(customerRepositoryMock.findById(1L))
                .thenReturn(Optional.of(newCustomer(1L, "Alex", "Wood", "London")));

        CustomerResourceO customer = testObj.findById(1L);

        assertEquals("Alex", customer.getName());
        assertEquals("Wood", customer.getSurname());
        assertEquals("London", customer.getHomeCity());
        assertNotNull(customer.getId());

        verify(customerRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
        ee.expect(ResourceNotFoundException.class);
        ee.expectMessage("Customer not found");

        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        testObj.findById(1L);
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
        existingCustomers.add(newCustomer(1L, "John", "Dooh", "Milan"));
        existingCustomers.add(newCustomer(2L, "Alex", "Ferguson", "Paris"));
        existingCustomers.add(newCustomer(3L, "Sarah", "Williams", "Madrid"));

        List<CustomerResourceO> expectedCustomers = new ArrayList<>();
        expectedCustomers.add(newCustomerResourceO(1L, "John", "Dooh", "Milan"));
        expectedCustomers.add(newCustomerResourceO(2L, "Alex", "Ferguson", "Paris"));
        expectedCustomers.add(newCustomerResourceO(3L, "Sarah", "Williams", "Madrid"));

        when(customerRepositoryMock.findAll()).thenReturn(existingCustomers);

        List<CustomerResourceO> retrievedCustomers = testObj.findAll();

        assertEquals(expectedCustomers, retrievedCustomers);
        verify(customerRepositoryMock, times(1)).findAll();
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenNoCustomers() {
        ee.expect(ResourceNotFoundException.class);
        ee.expectMessage("No customer found");

        when(customerRepositoryMock.findAll()).thenReturn(emptyList());

        testObj.findAll();
    }

    @Test
    public void shouldDeleteCustomer() {
        doNothing().when(customerRepositoryMock).deleteById(1L);

        testObj.deleteCustomer(1L);

        verify(customerRepositoryMock, times(1)).deleteById(1L);
        verify(customerRepositoryMock, never()).delete(any(Customer.class));
    }


    @Test
    public void shouldSaveCustomerWithPass() {
        Set<PassResourceI> passes = new HashSet<>();
        passes.add(PassResourceI.builder()
                .city("Naples")
                .length(6)
                .build());

        CustomerResourceI to = newCustomerResourceI("Alex", "Wood", "London", passes);

        when(customerRepositoryMock.save(any(Customer.class)))
                .thenReturn(newCustomer(1L, "Alex", "Wood", "London"));

        Customer customer = testObj.save(to);

        assertEquals("Alex", customer.getName());
        assertEquals("Wood", customer.getSurname());
        assertEquals("London", customer.getHomeCity());
        assertNotNull(customer.getId());

        verify(customerRepositoryMock, times(1)).save(any(Customer.class));
        verify(passRepositoryMock, times(1)).saveAll(anyIterable());
        verify(passRepositoryMock, never()).save(any(Pass.class));
    }

}
