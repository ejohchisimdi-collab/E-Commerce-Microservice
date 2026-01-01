package com.chisimdi.order.service.services;

import com.chisimdi.order.service.exceptions.ResourceNotFoundException;
import com.chisimdi.order.service.models.*;
import com.chisimdi.order.service.repositories.*;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.chisimdi.events.OrderEvent;
import com.chisimdi.events.Location;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private OrderRepository orderRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private KafkaTemplate<String,Object> kafkaTemplate;
    private RestClientService restClientService;
    private PlaceAnOrderIdempotencyRepository placeAnOrderIdempotencyRepository;
    private PaymentFailedIdempotencyRepository paymentFailedIdempotencyRepository;
    private PaymentSucceededIdempotencyRepository paymentSucceededIdempotencyRepository;
    private ReservationFailedIdempotencyRepository reservationFailedIdempotencyRepository;

    public OrderService(OrderRepository orderRepository,KafkaTemplate kafkaTemplate,ShoppingCartRepository shoppingCartRepository,
                        RestClientService restClientService,PlaceAnOrderIdempotencyRepository placeAnOrderIdempotencyRepository,
                        PaymentSucceededIdempotencyRepository paymentSucceededIdempotencyRepository,
                        PaymentFailedIdempotencyRepository paymentFailedIdempotencyRepository,
                        ReservationFailedIdempotencyRepository reservationFailedIdempotencyRepository){
        this.orderRepository=orderRepository;
        this.kafkaTemplate=kafkaTemplate;
        this.shoppingCartRepository=shoppingCartRepository;
        this.restClientService=restClientService;
        this.placeAnOrderIdempotencyRepository=placeAnOrderIdempotencyRepository;
        this.paymentFailedIdempotencyRepository=paymentFailedIdempotencyRepository;
        this.paymentSucceededIdempotencyRepository=paymentSucceededIdempotencyRepository;
        this.reservationFailedIdempotencyRepository=reservationFailedIdempotencyRepository;
    }
    public OrderDTO toOrdersDTO(Orders orders){
        OrderDTO orderDTO=new OrderDTO();
        orderDTO.setId(orders.getId());
        orderDTO.setUserId(orders.getUserId());
        if(orders.getProductName()!=null){
            orderDTO.setProductName(orders.getProductName());
        }
        if(orders.getBasePrice()!=null){
            orderDTO.setBasePrice(orders.getBasePrice());
        }
        if(orders.getTotalPrice()!=null){
            orderDTO.setTotalPrice(orders.getTotalPrice());
        }
        orderDTO.setCartId(orders.getCartId());
        orderDTO.setAmount(orders.getAmount());
        if(orders.getOrderStatus()!=null) {
            orderDTO.setOrderStatus(orders.getOrderStatus());
        }
        if(orders.getCreatedAt()!=null){
            orderDTO.setCreatedAt(orders.getCreatedAt());
        }
        if(orders.getFailureReasons()!=null){
            orderDTO.setFailureReasons(orders.getFailureReasons());
        }
        return orderDTO;

    }


    public Orders placeAnOrder(int cartId, int userId, int accountId, Location location){
        ShoppingCart shoppingCart=shoppingCartRepository.findByIdAndCartStatus(cartId,CartStatus.PENDING).orElseThrow(()->new ResourceNotFoundException("Cart with id "+cartId+"and status pending not found"));
        if(!restClientService.doesAccountAndUserExist(accountId, userId)){
            throw new ResourceNotFoundException("Account with userId "+userId+" and account Id "+accountId+" not found");
        }
        Orders orders=new Orders();
        orders.setAmount(shoppingCart.getAmount());
        orders.setOrderStatus(OrderStatus.PENDING);
        orders.setBasePrice(shoppingCart.getBasePrice());
        orders.setAmount(shoppingCart.getAmount());
        orders.setUserId(userId);
        orders.setTotalPrice(shoppingCart.getTotalPrice());
        orders.setProductName(shoppingCart.getProductName());
        orders.setCreatedAt(LocalDateTime.now());
        orders.setCartId(cartId);
        orderRepository.save(orders);

        OrderEvent orderEvent=new OrderEvent(UUID.randomUUID().toString(),orders.getId(),userId,accountId,location,orders.getAmount(),orders.getTotalPrice(),orders.getProductName());

        log.info("Sending Event");
        kafkaTemplate.send("order-created",orderEvent);


        return orders;

    }
    public OrderDTO placeAnOrderWithIdempotency(String idempotencyKey,int cartId, int userId, int accountId, Location location){
        PlaceAnOrderIdempotency placeAnOrderIdempotency1=placeAnOrderIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if(placeAnOrderIdempotency1!=null){
            return toOrdersDTO(placeAnOrderIdempotency1.getOrders());
        }

        Orders orders=placeAnOrder(cartId, userId, accountId, location);
        PlaceAnOrderIdempotency placeAnOrderIdempotency=new PlaceAnOrderIdempotency();
        placeAnOrderIdempotency.setIdempotencyKey(idempotencyKey);
        placeAnOrderIdempotency.setOrders(orders);
        placeAnOrderIdempotencyRepository.save(placeAnOrderIdempotency);
        return toOrdersDTO(orders);
    }

    public List<OrderDTO> findALlOrders(int pageNumber,int size){
        Page<Orders>orders=orderRepository.findAll(PageRequest.of(pageNumber, size));
        List<OrderDTO>orderDTOS=new ArrayList<>();
        for(Orders o:orders){
            orderDTOS.add(toOrdersDTO(o));
        }
        return orderDTOS;

    }
    public List<OrderDTO>findOrdersByUser(int userId,int pageNumber,int size){
        Page<Orders>orders=orderRepository.findByUserId(userId,PageRequest.of(pageNumber,size));
        List<OrderDTO>orderDTOS=new ArrayList<>();
        for(Orders o:orders){
            orderDTOS.add(toOrdersDTO(o));
        }
        return orderDTOS;
    }

    @KafkaListener(topics = "payment-succeeded")
    public void setOrderToComplete(OrderEvent  orderEvent){
        PaymentSucceededIdempotency paymentSucceededIdempotency1=paymentSucceededIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(paymentSucceededIdempotency1!=null){
            return;
        }

        log.info("Processing completed payment");
        Orders orders=orderRepository.findById(orderEvent.getOrderId()).orElse(null);
        if(orders==null){
            return;
        }

        ShoppingCart shoppingCart=shoppingCartRepository.findById(orders.getCartId()).orElse(null);
        if(shoppingCart==null){
            return;
        }
        orders.setOrderStatus(OrderStatus.COMPLETED);
       shoppingCart.setCartStatus(CartStatus.ORDERED);
       shoppingCartRepository.save(shoppingCart);
        orderRepository.save(orders);
        PaymentSucceededIdempotency paymentSucceededIdempotency=new PaymentSucceededIdempotency();
        paymentSucceededIdempotency.setId(orderEvent.getId());
        paymentSucceededIdempotency.setLocalDateTime(LocalDateTime.now());
        paymentSucceededIdempotencyRepository.save(paymentSucceededIdempotency);
    }
    @KafkaListener(topics = "reservation-failed")
    public void setOrderToFail(OrderEvent orderEvent){
        ReservationFailedIdempotency reservationFailedIdempotency1=reservationFailedIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(reservationFailedIdempotency1!=null){
            return;
        }


        Orders orders=orderRepository.findById(orderEvent.getOrderId()).orElse(null);
            if(orders==null){
                return;
            }
            orders.setOrderStatus(OrderStatus.FAILED);
            orders.getFailureReasons().add("Reservation Failed");
            orderRepository.save(orders);
            ReservationFailedIdempotency reservationFailedIdempotency=new ReservationFailedIdempotency();
            reservationFailedIdempotency.setId(orderEvent.getId());
            reservationFailedIdempotency.setLocalDateTime(LocalDateTime.now());
            reservationFailedIdempotencyRepository.save(reservationFailedIdempotency);


    }
    @KafkaListener(topics = "payment-failed")
    public void setOrderToFailed(OrderEvent  orderEvent){
        PaymentFailedIdempotency paymentFailedIdempotency1=paymentFailedIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(paymentFailedIdempotency1!=null){
            return;
        }

        Orders orders=orderRepository.findById(orderEvent.getOrderId()).orElse(null);
        if(orders==null){
            return;
        }
        orders.setOrderStatus(OrderStatus.FAILED);
        orders.getFailureReasons().add("Payment failed");
        orderRepository.save(orders);
        PaymentFailedIdempotency paymentFailedIdempotency=new PaymentFailedIdempotency();
        paymentFailedIdempotency.setId(orderEvent.getId());
        paymentFailedIdempotency.setLocalDateTime(LocalDateTime.now());
        paymentFailedIdempotencyRepository.save(paymentFailedIdempotency);
    }

    @Retryable(retryFor = OptimisticLockException.class,maxAttempts = 5,backoff = @Backoff(delay = 200,multiplier = 2))
    @Scheduled(cron = "0 */5 * * * *")
    public void ordersTimeout(){
        LocalDateTime localDateTime=LocalDateTime.now().minusMinutes(5);
        List<Orders>orders=orderRepository.findByCreatedAtBeforeAndOrderStatus(localDateTime,OrderStatus.PENDING);
        for(int x=0;x<orders.size();x++){
            Orders orders1=orders.get(x);
            orders1.setFailureReasons(new ArrayList<>());
            orders1.getFailureReasons().add("Orders took to long to process");
            orders1.setOrderStatus(OrderStatus.FAILED);
            orderRepository.save(orders1);
        }
    }



}
