package com.gadaldo.leisure.pass.util;

import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

import java.util.Date;

public class PassUtil {

    public static PassResourceO newPassResourceO(Long id, String city, int length) {
        return PassResourceO.builder()
                .id(id)
                .city(city)
                .length(length)
                .build();
    }

    public static PassResourceI newPassResourceI(String city, int length) {
        return PassResourceI.builder()
                .city(city)
                .length(length)
                .build();
    }

    public static Pass newPass(Long id, String city, int length, Date date) {
        return newPass(id, city, length, date, null);
    }

    public static Pass newPass(Long id, String city, int length, Date date, Customer customer) {
        return Pass.builder()
                .id(id)
                .length(length)
                .createdAt(date)
                .city(city)
                .customer(customer)
                .build();
    }

}
