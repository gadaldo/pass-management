package com.gadaldo.leisure.pass.service;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Customer;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceI;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.gadaldo.leisure.pass.service.CustomerMapper.toCustomer;
import static com.gadaldo.leisure.pass.service.CustomerMapper.toCustomerResource;
import static com.gadaldo.leisure.pass.service.PassMapper.toPassSet;
import static java.util.stream.Collectors.toList;

@Controller
@RequiredArgsConstructor
public class CustomerPersistenceServiceImpl implements CustomerPersistenceService {

    private final CustomerRepository customerRepository;
    private final PassRepository passRepository;

    @Override
    public List<CustomerResourceO> insertAll(List<CustomerResourceI> customerTO) {
        return CustomerMapper.toCustomerResourceList(
                customerRepository.saveAll(
                        customerTO.stream()
                                .map(CustomerMapper::toCustomer)
                                .collect(toList())));
    }

    @Override
    public void deleteAll() {
        customerRepository.deleteAllInBatch();
    }

    @Override
    public Customer save(CustomerResourceI newCustomer) {
        Customer customer = customerRepository.save(toCustomer(newCustomer));
        toPassSet(newCustomer.getPasses(), customer)
                .ifPresent(passRepository::saveAll);
        return customer;
    }

    @Override
    public CustomerResourceO findById(Long customerId) {
        return toCustomerResource(customerRepository.findById(customerId));
    }

    @Override
    public List<CustomerResourceO> findAll() {
        return CustomerMapper.toCustomerResourceList(customerRepository.findAll());
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }


}
