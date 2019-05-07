package com.gadaldo.leisure.pass.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomerPersistenceServiceImpl implements CustomerPersistenceService {

	private final CustomerRepository customerRepository;

	@Override
	public Customer save(CustomerTO newCustomer) {
		Customer customer = Customer.builder()
				.name(newCustomer.getName())
				.surname(newCustomer.getSurname())
				.homeCity(newCustomer.getHomeCity())
				.build();

		return customerRepository.save(customer);
	}

	@Override
	public Optional<Customer> findById(Long customerId) {
		return customerRepository.findById(customerId);
	}

	@Override
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}

}
