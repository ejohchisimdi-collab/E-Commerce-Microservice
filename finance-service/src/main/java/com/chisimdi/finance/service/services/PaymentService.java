package com.chisimdi.finance.service.services;

import com.chisimdi.events.Location;
import com.chisimdi.events.OrderEvent;
import com.chisimdi.finance.service.models.*;
import com.chisimdi.finance.service.repositories.AccountRepository;
import com.chisimdi.finance.service.repositories.PaymentRepository;
import com.chisimdi.finance.service.repositories.ProcessPaymentIdempotencyRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    private AccountRepository accountRepository;
    private PaymentRepository paymentRepository;
    private KafkaTemplate<String,Object>kafkaTemplate;
    private ProcessPaymentIdempotencyRepository processPaymentIdempotencyRepository;
private final static Logger log= LoggerFactory.getLogger(PaymentService.class);
    public PaymentService(AccountRepository accountRepository,PaymentRepository paymentRepository,KafkaTemplate<String,Object> kafkaTemplate,ProcessPaymentIdempotencyRepository processPaymentIdempotencyRepository){
        this.accountRepository=accountRepository;
        this.paymentRepository=paymentRepository;
        this.kafkaTemplate=kafkaTemplate;
        this.processPaymentIdempotencyRepository=processPaymentIdempotencyRepository;

    }

    public PaymentDTO toPaymentDTO(Payment payment){
        PaymentDTO paymentDTO=new PaymentDTO();
        if(payment.getPaymentStatus()!=null){
            paymentDTO.setPaymentStatus(payment.getPaymentStatus());
        }
        if(payment.getAccount()!=null){
            paymentDTO.setAccountId(payment.getAccount().getId());
        }
        if(payment.getAmount()!=null){
            paymentDTO.setAmount(payment.getAmount());
        }
        if(payment.getLocation()!=null){
            paymentDTO.setLocation(payment.getLocation());
        }
        if(payment.getWarnings()!=null){
            paymentDTO.setWarnings(payment.getWarnings());
        }
        if(payment.getLocalDate()!=null){
            paymentDTO.setLocalDate(payment.getLocalDate());
        }
        return paymentDTO;
    }

    @Retryable(retryFor = OptimisticLockException.class,maxAttempts = 5,backoff = @Backoff(delay = 200,multiplier = 2))
    @Transactional
    @KafkaListener(topics = "inventory-reserved")
    public void processPayment(OrderEvent orderEvent){

        ProcessPaymentIdempotency processPaymentIdempotency1=processPaymentIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(processPaymentIdempotency1!=null){
            return;
        }

        Account merchantAccount= accountRepository.findByAccountType(AccountType.MERCHANT);
        Account account= accountRepository.findById(orderEvent.getAccountId()).orElse(null);
        if(account==null){
            kafkaTemplate.send("payment-failed",orderEvent);
            return;
        }
        Payment payment=new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(orderEvent.getTotalPrice());
        payment.setWarnings(new ArrayList<>());
        payment.setAccount(account);
        payment.setLocation(orderEvent.getLocation());
        payment.setLocalDate(LocalDateTime.now());

        int fraudScore=0;

        List<Payment> paymentsList = paymentRepository.findByAccountUserIdAndPaymentStatus(orderEvent.getUserId(),PaymentStatus.SUCCESS);

        if (paymentsList.size() > 1) {
            if (!paymentsList.get(paymentsList.size() -1).getLocation().equals(payment.getLocation())) {
                log.warn("Payment was made from a different location, updating fraud score");
                fraudScore += 1;
                String reason = "A payment from a different location was made";
                payment.getWarnings().add(reason);
            }
        }

        if (paymentsList.size() >= 3) {
            if (ChronoUnit.MINUTES.between(paymentsList.get(paymentsList.size() - 3).getLocalDate(), paymentsList.get(paymentsList.size() - 1).getLocalDate()) < 1) {
                log.warn("Three consecutive payments were made in one minute. Updating fraud score");
                fraudScore += 1;
                String reason = " Three consecutive payments were made in less than one minute";
                payment.getWarnings().add(reason);
            }


        }
        List<Payment> paymentsThatFailed = paymentRepository.findByAccountId(orderEvent.getAccountId());
        if (paymentsThatFailed.size() >= 3) {

            if (paymentsThatFailed.get(paymentsThatFailed.size() - 1).getPaymentStatus().equals(PaymentStatus.FAILED) && paymentsThatFailed.get(paymentsThatFailed.size() - 2).getPaymentStatus().equals(PaymentStatus.FAILED) && paymentsThatFailed.get(paymentsThatFailed.size() - 3).getPaymentStatus().equals(PaymentStatus.FAILED)) {
                log.warn("Three consecutive failed payments were mad. Updating fraud score ");
                String reason = "Three consecutive failed payments were previously made";
                payment.getWarnings().add(reason);
                fraudScore += 1;
            }
        }
        if (account.getBalance().compareTo(orderEvent.getTotalPrice()) < 0) {
            log.warn("Insufficient funds updating payment status to failed ");
            String reason = "Insufficient funds";
            payment.getWarnings().add(reason);
        }
        if (fraudScore >= 3 || account.getBalance().compareTo(orderEvent.getTotalPrice()) < 0) {
            log.warn("Fraud score is greater than or equal to three / insufficient funds. Updating fraud score to failed");
            payment.setPaymentStatus(PaymentStatus.FAILED);
            kafkaTemplate.send("payment-failed",orderEvent);
        } else {
            account.setBalance(account.getBalance().subtract(orderEvent.getTotalPrice()));
            merchantAccount.setBalance(merchantAccount.getBalance().add(orderEvent.getTotalPrice()));
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            accountRepository.save(merchantAccount);
            accountRepository.save(account);
            kafkaTemplate.send("payment-succeeded",orderEvent);

        }
        paymentRepository.save(payment);
        accountRepository.save(account);
        accountRepository.save(merchantAccount);

        ProcessPaymentIdempotency processPaymentIdempotency=new ProcessPaymentIdempotency();
        processPaymentIdempotency.setId(orderEvent.getId());
        processPaymentIdempotency.setLocalDateTime(LocalDateTime.now());
        processPaymentIdempotencyRepository.save(processPaymentIdempotency);

    }
    public List<PaymentDTO> findAllPayments(int pageNumber,int size){
        Page<Payment>payments=paymentRepository.findAll(PageRequest.of(pageNumber,size));
        List<PaymentDTO>paymentDTOS=new ArrayList<>();
        for(Payment p:payments){
            paymentDTOS.add(toPaymentDTO(p));
        }
        return paymentDTOS;

    }
    public List<PaymentDTO>findPaymentsByUser(int userId,int pageNumber,int size){
        Page<Payment>payments=paymentRepository.findByAccountUserId(userId,PageRequest.of(pageNumber,size));
        List<PaymentDTO>paymentDTOS=new ArrayList<>();
        for(Payment p:payments){
            paymentDTOS.add(toPaymentDTO(p));
        }
        return paymentDTOS;


    }

}
