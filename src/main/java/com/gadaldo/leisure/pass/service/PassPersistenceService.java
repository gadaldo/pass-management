package com.gadaldo.leisure.pass.service;

import com.gadaldo.leisure.pass.rest.model.CustomerPassResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

public interface PassPersistenceService {

	PassResourceO addPassToCustomer(Long customerId, PassResourceI pass);

	CustomerPassResourceO findByCustomerId(Long customerId);

	boolean isValid(Long passId);

	PassResourceO updateCustomerPass(Long customerId, Long passId, PassResourceI passTO);

	boolean deletePass(Long customerId, Long passId);
}
