package com.gadaldo.leisure.pass.rest.model;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResourceI {

    private String name;
    private String surname;
    private String homeCity;
    private Set<PassResourceI> passes;

}
