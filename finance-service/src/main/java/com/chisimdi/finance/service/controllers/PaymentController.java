package com.chisimdi.finance.service.controllers;

import com.chisimdi.finance.service.models.PaymentDTO;
import com.chisimdi.finance.service.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Scanner;

@RequestMapping("/payments")
@RestController
public class PaymentController {
    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @Operation(summary = "Retrieves all payments, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @GetMapping("/")
    public List<PaymentDTO>findAllPayments(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return paymentService.findAllPayments(pageNumber, size);
    }

    @Operation(summary = "Retrieves payment by user available only to customers")
    @PreAuthorize("principal.userId == #userId")
    @GetMapping("/{userId}")
    public List<PaymentDTO>findPaymentsByUser(@PathVariable("userId")int userId,@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return paymentService.findPaymentsByUser(userId, pageNumber, size);
    }
}
