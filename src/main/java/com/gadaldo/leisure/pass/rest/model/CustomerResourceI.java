package com.gadaldo.leisure.pass.rest.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CustomerResourceI {

    private String name;
    private String surname;
    private String homeCity;
    private Set<PassResourceI> passes;

}
