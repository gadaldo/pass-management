package com.gadaldo.leisure.pass.util;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;

import java.util.Set;

public class CustomerUtil {

    public static CustomerResourceI newCustomerResourceI(String name, String surname, String homeCity) {
        return newCustomerResourceI(name, surname, homeCity, null);
    }

    public static CustomerResourceI newCustomerResourceI(String name, String surname, String homeCity, Set<PassResourceI> passes) {
        return CustomerResourceI.builder()
                .name(name)
                .surname(surname)
                .homeCity(homeCity)
                .passes(passes)
                .build();
    }

    public static Customer newCustomer(Long id, String name, String surname, String homeCity) {
        return Customer.builder()
                .id(id)
                .name(name)
                .surname(surname)
                .homeCity(homeCity)
                .build();
    }

    public static CustomerResourceO newCustomerResourceO(Long id, String name, String surname, String homeCity) {
        return CustomerResourceO.builder()
                .id(id)
                .name(name)
                .surname(surname)
                .homeCity(homeCity)
                .build();
    }
}
