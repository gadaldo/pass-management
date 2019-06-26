package com.gadaldo.leisure.pass.service;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

import java.util.*;

import static com.gadaldo.leisure.pass.service.CustomerMapper.toCustomerResource;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

class PassMapper {

    static CustomerResourceO toResource(List<Pass> passes) {
        if (isNull(passes) || passes.isEmpty())
            throw new ResourceNotFoundException("No pass found");

        return toCustomerResource(passes.get(0).getCustomer(), new HashSet<>(passes));
    }

    static PassResourceO toPassResource(Pass pass) {
        return PassResourceO.builder()
                .id(pass.getId())
                .length(pass.getLength())
                .city(pass.getCity())
                .build();
    }

    static Set<PassResourceO> toPassListResource(Set<Pass> passes) {
        return Objects.isNull(passes) ?
                null :
                passes.stream()
                        .map(PassMapper::toPassResource)
                        .collect(toSet());
    }

    static Optional<Set<Pass>> toPassSet(Set<PassResourceI> passes, Customer customer) {
        return isNull(passes) ?
                Optional.empty() :
                Optional.of(passes.stream()
                        .map(p -> toPass(p, customer))
                        .collect(toSet()));
    }

    private static Pass toPass(PassResourceI pass, Customer customer) {
        return Pass.builder()
                .customer(customer)
                .city(pass.getCity())
                .length(pass.getLength())
                .createdAt(new Date())
                .build();
    }

}
