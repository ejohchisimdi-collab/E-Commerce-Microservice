package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.ReservationFailedIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationFailedIdempotencyRepository extends JpaRepository<ReservationFailedIdempotency,String> {
}
