package com.gadaldo.leisure.pass.service;

import java.util.List;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;

public interface CustomerPersistenceService {

	Customer save(CustomerResourceI customerTO);

	CustomerResourceO findById(Long customerId);

	List<Customer> findAll();

	void deleteCustomer(Long id);
}
