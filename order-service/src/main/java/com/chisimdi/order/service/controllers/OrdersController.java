package com.chisimdi.order.service.controllers;

import com.chisimdi.order.service.models.OrderDTO;
import com.chisimdi.order.service.repositories.OrderRepository;
import com.chisimdi.order.service.services.OrderService;
import com.chisimdi.order.service.utils.MakeAnOrderUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/orders")
@RestController
public class OrdersController {
    public OrderService orderService;

    public OrdersController(OrderService orderService){
        this.orderService=orderService;
    }

    @Operation(summary = "Makes an order, available only to customers")
    @PreAuthorize("hasRole('ROLE_Customer') and principal.userId==#makeAnOrderUtil.userId")
    @PostMapping("/")
    public OrderDTO makeAnOrder(@RequestHeader("Idempotency-Key")String idempotencyKey,@Valid @RequestBody MakeAnOrderUtil makeAnOrderUtil){
        return orderService.placeAnOrderWithIdempotency(idempotencyKey,makeAnOrderUtil.getCartId(),makeAnOrderUtil.getUserId(),makeAnOrderUtil.getAccountId(),makeAnOrderUtil.getLocation());

    }

    @Operation(summary = "Retrieves all orders, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @GetMapping("/")
    public List<OrderDTO>findAllOrders(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return orderService.findALlOrders(pageNumber, size);
    }

    @Operation(summary = "Retrieves order by user, available to both merchants and users with varying limitations")
    @PreAuthorize("hasRole('ROLE_Merchant') or principal.userId == #userId")
    @GetMapping("/{userId}")
    public List<OrderDTO>findOrdersByUser(@PathVariable("userId")int userId,@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return orderService.findOrdersByUser(userId, pageNumber, size);
    }
}
