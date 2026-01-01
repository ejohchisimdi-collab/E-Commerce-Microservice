package com.chisimdi.product.service.repositories;

import com.chisimdi.product.service.models.ReserveStockIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReserveStockIdempotencyRepository extends JpaRepository<ReserveStockIdempotency,String> {
}
