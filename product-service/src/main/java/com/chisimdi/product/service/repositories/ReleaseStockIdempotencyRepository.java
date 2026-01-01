package com.chisimdi.product.service.repositories;

import com.chisimdi.product.service.models.ReleaseStocksIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseStockIdempotencyRepository extends JpaRepository<ReleaseStocksIdempotency,String> {
}
