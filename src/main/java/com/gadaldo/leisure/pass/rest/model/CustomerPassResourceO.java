package com.gadaldo.leisure.pass.rest.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerPassResourceO {

	private final CustomerResourceO customer;
	private final List<PassResourceO> passes;
}
