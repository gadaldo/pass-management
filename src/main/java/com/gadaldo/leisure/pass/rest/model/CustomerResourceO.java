package com.gadaldo.leisure.pass.rest.model;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResourceO {

    private final Long id;
    private final String name;
    private final String surname;
    private final String homeCity;
    private final Set<PassResourceO> passes;

}
