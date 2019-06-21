package com.gadaldo.leisure.pass.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassResourceI {

	private String city;
	private int length;

}
