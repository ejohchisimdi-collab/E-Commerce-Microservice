package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.PaymentSucceededIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSucceededIdempotencyRepository extends JpaRepository<PaymentSucceededIdempotency,String> {
}
