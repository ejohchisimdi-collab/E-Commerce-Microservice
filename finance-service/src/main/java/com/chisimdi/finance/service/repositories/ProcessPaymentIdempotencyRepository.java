package com.chisimdi.finance.service.repositories;

import com.chisimdi.finance.service.models.ProcessPaymentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessPaymentIdempotencyRepository extends JpaRepository<ProcessPaymentIdempotency,String> {
}
