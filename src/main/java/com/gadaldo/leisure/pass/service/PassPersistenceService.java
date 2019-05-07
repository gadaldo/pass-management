package com.gadaldo.leisure.pass.service;

import java.util.List;

import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.PassTO;

public interface PassPersistenceService {

	Long addPassToCustomer(Long customerId, PassTO pass);

	List<Pass> findByCustomerId(Long customerId);

	boolean isValid(Long passId);

	Pass updateCustomerPass(Long customerId, Long passId, PassTO passTO);

	boolean deletePass(Long customerId, Long passId);
}
