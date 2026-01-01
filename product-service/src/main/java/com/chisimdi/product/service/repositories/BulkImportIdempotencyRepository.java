package com.chisimdi.product.service.repositories;

import com.chisimdi.product.service.models.BulkImportIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkImportIdempotencyRepository extends JpaRepository<BulkImportIdempotency,Integer> {
    BulkImportIdempotency findByIdempotencyKey(String idempotencyKey);

}
