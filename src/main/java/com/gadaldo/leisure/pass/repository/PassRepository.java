package com.gadaldo.leisure.pass.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gadaldo.leisure.pass.repository.model.Pass;

@Repository
public interface PassRepository extends JpaRepository<Pass, Long> {

	@Query("select p from Pass p where p.customer.id = :customerId")
	Set<Pass> findByCustomerId(@Param("customerId") Long customerId);

	@Query("select p from Pass p where p.customer.id = :customerId and p.id = :id")
	Optional<Pass> findByIdAndCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);

}
