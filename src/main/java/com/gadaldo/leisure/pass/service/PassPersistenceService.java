package com.gadaldo.leisure.pass.service;

import java.util.List;

import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

public interface PassPersistenceService {

	Long addPassToCustomer(Long customerId, PassResourceI pass);

	List<PassResourceO> findByCustomerId(Long customerId);

	boolean isValid(Long passId);

	Pass updateCustomerPass(Long customerId, Long passId, PassResourceI passTO);

	boolean deletePass(Long customerId, Long passId);
}
