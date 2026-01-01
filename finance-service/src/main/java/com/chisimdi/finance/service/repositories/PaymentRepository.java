package com.chisimdi.finance.service.repositories;

import com.chisimdi.finance.service.models.Payment;
import com.chisimdi.finance.service.models.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    List<Payment>findByAccountUserIdAndPaymentStatus(int userId, PaymentStatus paymentStatus);
    List<Payment>findByAccountId(int accountId);
    Page<Payment> findByAccountUserId(int userId, Pageable pageable);
}
