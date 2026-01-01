package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.PaymentFailedIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentFailedIdempotencyRepository extends JpaRepository<PaymentFailedIdempotency,String> {
}
