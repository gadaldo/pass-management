package com.gadaldo.leisure.pass.service;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gadaldo.leisure.pass.service.PassMapper.toPassListResource;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

class CustomerMapper {

    static CustomerResourceO toCustomerResource(Optional<Customer> customer) {
        return customer
                .map(CustomerMapper::toCustomerResource)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    static List<CustomerResourceO> toCustomerResourceList(List<Customer> customers) {
        if (isNull(customers) || customers.isEmpty())
            throw new ResourceNotFoundException("No customer found");

        return customers.stream()
                .map(CustomerMapper::toCustomerResource)
                .collect(toList());
    }

    static CustomerResourceO toCustomerResource(Customer customer) {
        return toCustomerResource(customer, null);
    }

    static CustomerResourceO toCustomerResource(Customer customer, Set<Pass> passes) {
        return CustomerResourceO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .homeCity(customer.getHomeCity())
                .passes(toPassListResource(passes))
                .build();
    }

    static Customer toCustomer(CustomerResourceI customerResourceI) {
        return Customer.builder()
                .name(customerResourceI.getName())
                .surname(customerResourceI.getSurname())
                .homeCity(customerResourceI.getHomeCity())
                .build();
    }
}
