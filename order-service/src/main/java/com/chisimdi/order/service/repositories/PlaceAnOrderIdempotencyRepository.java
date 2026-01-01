package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.PlaceAnOrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceAnOrderIdempotencyRepository extends JpaRepository<PlaceAnOrderIdempotency,Integer> {
    PlaceAnOrderIdempotency findByIdempotencyKey(String idempotencyKey);
}
