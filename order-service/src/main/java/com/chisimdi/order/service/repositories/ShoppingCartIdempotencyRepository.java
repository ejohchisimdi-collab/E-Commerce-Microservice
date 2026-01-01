package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.ShoppingCartIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartIdempotencyRepository extends JpaRepository<ShoppingCartIdempotency,Integer> {
    ShoppingCartIdempotency findByIdempotencyKey(String idempotency);
}
