package com.gadaldo.leisure.pass.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.service.CustomerPersistenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerPersistenceService customerPersistenceService;

	@PostMapping("/customers")
	public ResponseEntity<String> createCustomer(@RequestBody CustomerResourceI newCustomer) {
		log.info("New customer: {}", newCustomer);

		try {
			return new ResponseEntity<>("Created customer: " + customerPersistenceService.save(newCustomer).getId(), HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("Error occurred", e);
			throw new ResourceNotFoundException("Error saving customer", e);
		}
	}

	@GetMapping("/customers/{customerId}")
	public ResponseEntity<CustomerResourceO> getCustomer(@PathVariable Long customerId) {
		log.info("Get customer: {}", customerId);

		CustomerResourceO optCustomer = customerPersistenceService.findById(customerId);

		return new ResponseEntity<>(optCustomer, HttpStatus.OK);
	}

	@GetMapping("/customers")
	public ResponseEntity<String> getAllCustomers() {
		log.info("List customers");

		List<Customer> customers = customerPersistenceService.findAll();

		if (customers != null && !customers.isEmpty())
			return new ResponseEntity<>("Customers found: " + customers, HttpStatus.OK);
		throw new ResourceNotFoundException("No existing customers found");
	}

	@DeleteMapping("/customers/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
		log.info("Delete customer: {}", customerId);

		customerPersistenceService.deleteCustomer(customerId);

		return new ResponseEntity<>("Customer deleted", HttpStatus.OK);
	}

}
