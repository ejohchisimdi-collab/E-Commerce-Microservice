package com.chisimdi.order.service;

import com.chisimdi.events.Location;
import com.chisimdi.events.OrderEvent;
import com.chisimdi.order.service.exceptions.ResourceNotFoundException;
import com.chisimdi.order.service.models.CartStatus;
import com.chisimdi.order.service.models.OrderStatus;
import com.chisimdi.order.service.models.Orders;
import com.chisimdi.order.service.models.ShoppingCart;
import com.chisimdi.order.service.repositories.*;
import com.chisimdi.order.service.services.OrderService;
import com.chisimdi.order.service.services.RestClientService;
import jdk.javadoc.doclet.Taglet;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private KafkaTemplate<String,Object> kafkaTemplate;
    @Mock
    private RestClientService restClientService;
    @Mock
    private PlaceAnOrderIdempotencyRepository placeAnOrderIdempotencyRepository;
    @Mock
    private PaymentFailedIdempotencyRepository paymentFailedIdempotencyRepository;
    @Mock
    private PaymentSucceededIdempotencyRepository paymentSucceededIdempotencyRepository;
    @Mock
    private ReservationFailedIdempotencyRepository reservationFailedIdempotencyRepository;
    @InjectMocks
    OrderService orderService;

    @Test
    void placeAnOrderTest(){
        int cartId=1;
        int userId=2;
        int accountId=3;
        Location location=Location.Albania;

        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setAmount(2);
        shoppingCart.setProductName("Apple");
        shoppingCart.setBasePrice(BigDecimal.ONE);
        shoppingCart.setTotalPrice(BigDecimal.valueOf(2));


        when(shoppingCartRepository.findByIdAndCartStatus(cartId, CartStatus.PENDING)).thenReturn(Optional.of(shoppingCart));
        when(restClientService.doesAccountAndUserExist(accountId,userId)).thenReturn(true);

        Orders orders=orderService.placeAnOrder(cartId,userId,accountId,location);

        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.PENDING);

        verify(kafkaTemplate).send(eq("order-created"),any(OrderEvent.class));

    }
    @Test
    void placeAnOrderTest_ThrowsResourceNotFoundExceptionForShoppingCart(){
        int cartId=1;
        int userId=2;
        int accountId=3;
        Location location=Location.Albania;

        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setAmount(2);
        shoppingCart.setProductName("Apple");
        shoppingCart.setBasePrice(BigDecimal.ONE);
        shoppingCart.setTotalPrice(BigDecimal.valueOf(2));


        when(shoppingCartRepository.findByIdAndCartStatus(cartId, CartStatus.PENDING)).thenReturn(Optional.empty());


        assertThatThrownBy(()->orderService.placeAnOrder(cartId,userId,accountId,location)).isInstanceOf(ResourceNotFoundException.class);



        verify(kafkaTemplate,never()).send(eq("order-created"),any(OrderEvent.class));

    }
    @Test
    void placeAnOrderTest_DoesUserAccountExist(){
        int cartId=1;
        int userId=2;
        int accountId=3;
        Location location=Location.Albania;

        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setAmount(2);
        shoppingCart.setProductName("Apple");
        shoppingCart.setBasePrice(BigDecimal.ONE);
        shoppingCart.setTotalPrice(BigDecimal.valueOf(2));


        when(shoppingCartRepository.findByIdAndCartStatus(cartId, CartStatus.PENDING)).thenReturn(Optional.of(shoppingCart));
        when(restClientService.doesAccountAndUserExist(accountId,userId)).thenReturn(false);

        assertThatThrownBy(()->orderService.placeAnOrder(cartId,userId,accountId,location));



        verify(kafkaTemplate,never()).send(eq("order-created"),any(OrderEvent.class));

    }

    @Test
    void setOrderTOCompleteTest(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        ShoppingCart shoppingCart=new ShoppingCart();

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.of(orders));
        when(shoppingCartRepository.findById(orders.getCartId())).thenReturn(Optional.of(shoppingCart));

        orderService.setOrderToComplete(orderEvent);

        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(shoppingCart.getCartStatus()).isEqualTo(CartStatus.ORDERED);

        verify(shoppingCartRepository).save(shoppingCart);
        verify(orderRepository).save(orders);
    }
    @Test
    void setOrderTOCompleteTest_ReturnAtOrderSearch(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        ShoppingCart shoppingCart=new ShoppingCart();

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.empty());

        orderService.setOrderToComplete(orderEvent);


        verify(shoppingCartRepository,never()).save(shoppingCart);
        verify(orderRepository,never()).save(orders);
    }
    @Test
    void setOrderToCompleteTest_ReturnAtShoppingCart(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        ShoppingCart shoppingCart=new ShoppingCart();

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.of(orders));
        when(shoppingCartRepository.findById(orders.getCartId())).thenReturn(Optional.empty());

        orderService.setOrderToComplete(orderEvent);


        verify(shoppingCartRepository,never()).save(shoppingCart);
        verify(orderRepository,never()).save(orders);
    }

    @Test
    void setOrderToFailedTestReservationFailed(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.of(orders));

        orderService.setOrderToFail(orderEvent);

        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(orders.getFailureReasons().size()).isEqualTo(1);

        verify(orderRepository).save(orders);
    }

    @Test
    void setOrderToFailedTestReservationFailed_ReturnsAtOrder(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.empty());

        orderService.setOrderToFail(orderEvent);


        verify(orderRepository,never()).save(orders);
    }

    @Test
    void setOrderToFailedTestPaymentFailed(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.of(orders));

        orderService.setOrderToFailed(orderEvent);

        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(orders.getFailureReasons().size()).isEqualTo(1);

        verify(orderRepository).save(orders);
    }

    @Test
    void setOrderToFailedTestPaymentFailed_returnAtOrders(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setOrderId(1);

        Orders orders=new Orders();
        orders.setCartId(2);

        when(orderRepository.findById(orderEvent.getOrderId())).thenReturn(Optional.empty());

        orderService.setOrderToFailed(orderEvent);


        verify(orderRepository,never()).save(orders);
    }

    @Test
    void timeoutSetOrdersToFailed(){
        Orders orders=new Orders();
        List<Orders>orders1=new ArrayList<>();
        orders1.add(orders);
        LocalDateTime localDateTime=LocalDateTime.now().minusMinutes(5);
        when(orderRepository.findByCreatedAtBeforeAndOrderStatus(any(LocalDateTime.class),eq(OrderStatus.PENDING)))
                .thenReturn(orders1);


        orderService.ordersTimeout();

        assertThat(orders.getOrderStatus()).isEqualTo(OrderStatus.FAILED);

        verify(orderRepository).save(orders);
    }



}
