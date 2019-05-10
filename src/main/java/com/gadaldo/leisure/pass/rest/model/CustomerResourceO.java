package com.gadaldo.leisure.pass.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResourceO {

	private Long id;
	private String name;
	private String surname;
	private String homeCity;

}
