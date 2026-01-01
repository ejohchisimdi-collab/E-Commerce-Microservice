package com.chisimdi.order.service.controllers;

import com.chisimdi.order.service.models.ShoppingCartDTO;
import com.chisimdi.order.service.services.ShoppingCartService;
import com.chisimdi.order.service.utils.AddToCartUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/shopping-cart")
@RestController
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService){
        this.shoppingCartService=shoppingCartService;
    }

    @Operation(summary = "Adds a product to cart")
    @PreAuthorize("hasRole('ROLE_Customer') and principal.userId == #addToCartUtil.userId")
    @PostMapping("/")
    public ShoppingCartDTO addToCart(@RequestHeader("Idempotency-Key")String idempotencyKey,@RequestBody @Valid AddToCartUtil addToCartUtil){
        return shoppingCartService.addToCartWithIdempotency(idempotencyKey,addToCartUtil.getUserId(), addToCartUtil.getProductName(), addToCartUtil.getAmount());
    }

    @Operation(summary = "Retrieves all carts, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @GetMapping("/")
    public List<ShoppingCartDTO> getAllCarts(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return shoppingCartService.findAllShoppingCarts(pageNumber, size);

    }

    @Operation(summary = "Retrieves all carts by user, available to merchants and customers with varying limitations")
    @PreAuthorize("hasRole('ROLE_Merchant') or principal.userId == #userId")
    @GetMapping("/{userId}")
    public List<ShoppingCartDTO>getCartsByUser(@PathVariable("userId")int userId,@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return shoppingCartService.findShoppingCartByUserId(userId, pageNumber, size);
    }
}
