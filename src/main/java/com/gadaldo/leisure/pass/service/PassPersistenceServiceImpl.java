package com.gadaldo.leisure.pass.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;

import com.gadaldo.leisure.pass.repository.CustomerRepository;
import com.gadaldo.leisure.pass.repository.PassRepository;
import com.gadaldo.leisure.pass.repository.model.Pass;
import com.gadaldo.leisure.pass.rest.controller.ResourceNotFoundException;
import com.gadaldo.leisure.pass.rest.model.PassResourceI;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PassPersistenceServiceImpl implements PassPersistenceService {

	private final PassRepository passRepository;

	private final CustomerRepository customerRepository;

	@Override
	public Long addPassToCustomer(Long customerId, PassResourceI passTO) {
		Pass pass = Pass.builder()
				.lenght(passTO.getLenght())
				.city(passTO.getCity())
				.createdAt(new Date())
				.build();

		return customerRepository.findById(customerId)
				.map(c -> {
					pass.setCustomer(c);
					return passRepository.save(pass).getId();
				}).orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));
	}

	@Override
	public List<Pass> findByCustomerId(Long customerId) {
		return passRepository.findByCustomerId(customerId);
	}

	@Override
	public Pass updateCustomerPass(Long customerId, Long passId, PassResourceI passTO) {
		Optional<Pass> passToUpdate = passRepository.findByIdAndCustomerId(passId, customerId);

		return passToUpdate.map(p -> {
			p.setLenght(passTO.getLenght());
			return passRepository.save(p);
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
				.map(p -> isPassExpired(p))
				.orElseThrow(() -> new ResourceNotFoundException("Pass Not Found"));
	}

	private static boolean isPassExpired(Pass pass) {
		Date createdAt = pass.getCreatedAt();
		Calendar createdAtCalendar = Calendar.getInstance();
		createdAtCalendar.setTime(createdAt);

		createdAtCalendar.add(Calendar.DATE, pass.getLenght());

		return createdAtCalendar.getTime().after(new Date());
	}

}
