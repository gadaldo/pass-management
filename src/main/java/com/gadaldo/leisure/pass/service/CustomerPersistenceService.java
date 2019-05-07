package com.gadaldo.leisure.pass.service;

import java.util.List;
import java.util.Optional;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerTO;

public interface CustomerPersistenceService {

	Customer save(CustomerTO customerTO);

	Optional<Customer> findById(Long customerId);

	List<Customer> findAll();
	
	void deleteCustomer(Long id);
}
