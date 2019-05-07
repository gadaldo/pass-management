package com.gadaldo.leisure.pass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gadaldo.leisure.pass.repository.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
