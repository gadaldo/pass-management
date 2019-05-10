package com.gadaldo.leisure.pass.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomerPersistenceServiceImpl implements CustomerPersistenceService {

	private final CustomerRepository customerRepository;

	@Override
	public Customer save(CustomerResourceI newCustomer) {
		Customer customer = Customer.builder()
				.name(newCustomer.getName())
				.surname(newCustomer.getSurname())
				.homeCity(newCustomer.getHomeCity())
				.build();

		return customerRepository.save(customer);
	}

	@Override
	public CustomerResourceO findById(Long customerId) {
		return toCustomerResource(customerRepository.findById(customerId));
	}

	@Override
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	@Override
	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}

	private CustomerResourceO toCustomerResource(Optional<Customer> customer) {
		return customer.map(c -> {
			return CustomerResourceO.builder()
					.id(c.getId())
					.name(c.getName())
					.surname(c.getSurname())
					.homeCity(c.getHomeCity())
					.build();
		}).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
	}

}
