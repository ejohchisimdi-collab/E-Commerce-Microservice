package com.chisimdi.finance.service;

import com.chisimdi.events.Location;
import com.chisimdi.events.OrderEvent;
import com.chisimdi.finance.service.models.Account;
import com.chisimdi.finance.service.models.AccountType;
import com.chisimdi.finance.service.models.Payment;
import com.chisimdi.finance.service.models.PaymentStatus;
import com.chisimdi.finance.service.repositories.AccountRepository;
import com.chisimdi.finance.service.repositories.PaymentRepository;
import com.chisimdi.finance.service.repositories.ProcessPaymentIdempotencyRepository;
import com.chisimdi.finance.service.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(topics = {"payment-succeeded","payment-failed"})
public class PaymentServiceIntegrationTest {
    @MockitoBean
    private AccountRepository accountRepository;
    @MockitoBean
    private PaymentRepository paymentRepository;
    @MockitoBean
    private ProcessPaymentIdempotencyRepository processPaymentIdempotencyRepository;
    @Autowired
    PaymentService paymentService;

    BlockingDeque<Object>blockingDeque=new LinkedBlockingDeque<>();

    @KafkaListener(topics = {"payment-succeeded","payment-failed"})
    public void addEvent(OrderEvent orderEvent){
        blockingDeque.add(orderEvent);
    }

    @Test
    void ProcessPaymentTest()throws Exception{
        Payment payment=new Payment();


        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);
        orderEvent.setLocation(Location.Albania);
        orderEvent.setAmount(2);
        orderEvent.setId("1");
        orderEvent.setId("15");
        orderEvent.setUserId(1);
        orderEvent.setTotalPrice(BigDecimal.valueOf(200));

        Account customerAccount=new Account();
        customerAccount.setAccountType(AccountType.CUSTOMER);
        customerAccount.setBalance(BigDecimal.valueOf(2000));
        customerAccount.setUserId(1);

        Account merchantAccount=new Account();
        merchantAccount.setBalance(BigDecimal.valueOf(2000));

        ArgumentCaptor<Payment> captor=ArgumentCaptor.forClass(Payment.class);

        Payment payment1=new Payment();
        Payment payment2=new Payment();
        Payment payment3=new Payment();
        payment1.setLocalDate(LocalDateTime.now().plusMinutes(3));
        List<Payment> paymentList=List.of(payment3,payment2,payment1);
        payment3.setLocalDate(LocalDateTime.now());
        payment1.setLocation(Location.Albania);
        Payment payment4=new Payment();
        payment4.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment payment5=new Payment();
        payment5.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment payment6=new Payment();
        payment6.setPaymentStatus(PaymentStatus.SUCCESS);
        List<Payment>paymentsThatFailed=List.of(payment5,payment4,payment6);

        when(processPaymentIdempotencyRepository.findById(orderEvent.getId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountType(AccountType.MERCHANT)).thenReturn(merchantAccount);
        when(accountRepository.findById(orderEvent.getAccountId())).thenReturn(Optional.of(customerAccount));
        when(paymentRepository.findByAccountUserIdAndPaymentStatus(orderEvent.getUserId(),PaymentStatus.SUCCESS)).thenReturn(paymentList);
        when(paymentRepository.findByAccountId(orderEvent.getAccountId())).thenReturn(paymentsThatFailed);

        when(accountRepository.save(customerAccount)).thenReturn(customerAccount);
        when(accountRepository.save(merchantAccount)).thenReturn(merchantAccount);


        paymentService.processPayment(orderEvent);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert (object instanceof OrderEvent);



    }
    @Test
    void ProcessPaymentTest_InsufficientFunds()throws Exception{
        Payment payment=new Payment();


        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);
        orderEvent.setLocation(Location.Albania);
        orderEvent.setAmount(2);
        orderEvent.setId("15");
        orderEvent.setUserId(1);
        orderEvent.setTotalPrice(BigDecimal.valueOf(200));

        Account customerAccount=new Account();
        customerAccount.setAccountType(AccountType.CUSTOMER);
        customerAccount.setBalance(BigDecimal.valueOf(0));
        customerAccount.setUserId(1);

        Account merchantAccount=new Account();
        merchantAccount.setBalance(BigDecimal.valueOf(2000));

        ArgumentCaptor<Payment> captor=ArgumentCaptor.forClass(Payment.class);

        Payment payment1=new Payment();
        Payment payment2=new Payment();
        Payment payment3=new Payment();
        payment1.setLocalDate(LocalDateTime.now().plusMinutes(3));
        List<Payment> paymentList=List.of(payment3,payment2,payment1);
        payment3.setLocalDate(LocalDateTime.now());
        payment1.setLocation(Location.Albania);
        Payment payment4=new Payment();
        payment4.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment payment5=new Payment();
        payment5.setPaymentStatus(PaymentStatus.SUCCESS);
        Payment payment6=new Payment();
        payment6.setPaymentStatus(PaymentStatus.SUCCESS);
        List<Payment>paymentsThatFailed=List.of(payment5,payment4,payment6);

        when(processPaymentIdempotencyRepository.findById(orderEvent.getId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountType(AccountType.MERCHANT)).thenReturn(merchantAccount);
        when(accountRepository.findById(orderEvent.getAccountId())).thenReturn(Optional.of(customerAccount));
        when(paymentRepository.findByAccountUserIdAndPaymentStatus(orderEvent.getUserId(),PaymentStatus.SUCCESS)).thenReturn(paymentList);
        when(paymentRepository.findByAccountId(orderEvent.getAccountId())).thenReturn(paymentsThatFailed);

        when(accountRepository.save(customerAccount)).thenReturn(customerAccount);
        when(accountRepository.save(merchantAccount)).thenReturn(merchantAccount);
        when(paymentRepository.save(any(Payment.class))).thenReturn(captor.capture());

        paymentService.processPayment(orderEvent);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert (object instanceof OrderEvent);

    }

    @Test
    void ProcessPaymentTest_FraudScoreEqualsThree()throws Exception{
        Payment payment=new Payment();


        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);
        orderEvent.setLocation(Location.Albania);
        orderEvent.setAmount(2);
        orderEvent.setId("15");
        orderEvent.setUserId(1);
        orderEvent.setTotalPrice(BigDecimal.valueOf(200));

        Account customerAccount=new Account();
        customerAccount.setAccountType(AccountType.CUSTOMER);
        customerAccount.setBalance(BigDecimal.valueOf(2000));
        customerAccount.setUserId(1);

        Account merchantAccount=new Account();
        merchantAccount.setBalance(BigDecimal.valueOf(2000));

        ArgumentCaptor<Payment> captor=ArgumentCaptor.forClass(Payment.class);

        Payment payment1=new Payment();
        Payment payment2=new Payment();
        Payment payment3=new Payment();
        payment1.setLocalDate(LocalDateTime.now().plusMinutes(0));
        List<Payment> paymentList=List.of(payment3,payment2,payment1);
        payment3.setLocalDate(LocalDateTime.now());
        payment1.setLocation(Location.Samoa);
        Payment payment4=new Payment();
        payment4.setPaymentStatus(PaymentStatus.FAILED);
        Payment payment5=new Payment();
        payment5.setPaymentStatus(PaymentStatus.FAILED);
        Payment payment6=new Payment();
        payment6.setPaymentStatus(PaymentStatus.FAILED);
        List<Payment>paymentsThatFailed=List.of(payment5,payment4,payment6);

        when(processPaymentIdempotencyRepository.findById(orderEvent.getId())).thenReturn(Optional.empty());
        when(accountRepository.findByAccountType(AccountType.MERCHANT)).thenReturn(merchantAccount);
        when(accountRepository.findById(orderEvent.getAccountId())).thenReturn(Optional.of(customerAccount));
        when(paymentRepository.findByAccountUserIdAndPaymentStatus(orderEvent.getUserId(),PaymentStatus.SUCCESS)).thenReturn(paymentList);
        when(paymentRepository.findByAccountId(orderEvent.getAccountId())).thenReturn(paymentsThatFailed);

        when(accountRepository.save(customerAccount)).thenReturn(customerAccount);
        when(accountRepository.save(merchantAccount)).thenReturn(merchantAccount);
        when(paymentRepository.save(any(Payment.class))).thenReturn(captor.capture());

        paymentService.processPayment(orderEvent);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert (object instanceof OrderEvent);

    }
}
