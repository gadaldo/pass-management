package com.gadaldo.leisure.pass.service;

import static com.gadaldo.leisure.pass.service.PassMapper.toPassResource;
import static com.gadaldo.leisure.pass.service.PassMapper.toResource;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.CustomerResourceO;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;
import com.gadaldo.leisure.pass.rest.model.PassResourceO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PassPersistenceServiceImpl implements PassPersistenceService {

    private final PassRepository passRepository;

    private final CustomerRepository customerRepository;

    @Override
    public PassResourceO addPassToCustomer(Long customerId, PassResourceI passTO) {
        Pass pass = Pass.builder()
                .length(passTO.getLength())
                .city(passTO.getCity())
                .createdAt(new Date())
                .build();

        return customerRepository.findById(customerId)
                .map(c -> {
                    pass.setCustomer(c);
                    return toPassResource(passRepository.save(pass));
                }).orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));
    }

    @Override
    public CustomerResourceO findByCustomerId(Long customerId) {
        return toResource(passRepository.findByCustomerId(customerId));
    }

    @Override
    public PassResourceO updateCustomerPass(Long customerId, Long passId, PassResourceI passTO) {
        Optional<Pass> passToUpdate = passRepository.findByIdAndCustomerId(passId, customerId);

        return passToUpdate.map(p -> {
            p.setLength(passTO.getLength());
            return toPassResource(passRepository.save(p));
        }).orElseThrow(() -> new ResourceNotFoundException("Pass Not Found"));
    }

    @Override
    public boolean deletePass(Long customerId, Long passId) {
        Optional<Pass> passToUpdate = passRepository.findByIdAndCustomerId(passId, customerId);

        return passToUpdate.map(p -> {
            passRepository.delete(p);
            return true;
        }).orElseThrow(() -> new ResourceNotFoundException("Pass Not Found"));
    }

    @Override
    public boolean isValid(Long passId) {
        return passRepository.findById(passId)
                .map(PassPersistenceServiceImpl::isPassExpired)
                .orElseThrow(() -> new ResourceNotFoundException("Pass Not Found"));
    }

    private static boolean isPassExpired(Pass pass) {
        Date createdAt = pass.getCreatedAt();
        Calendar createdAtCalendar = Calendar.getInstance();
        createdAtCalendar.setTime(createdAt);

        createdAtCalendar.add(Calendar.DATE, pass.getLength());

        return createdAtCalendar.getTime().after(new Date());
    }

}
