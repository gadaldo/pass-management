package com.gadaldo.leisure.pass.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassResourceO {

    private final Long id;
    private final String city;
    private final Integer length;

}
