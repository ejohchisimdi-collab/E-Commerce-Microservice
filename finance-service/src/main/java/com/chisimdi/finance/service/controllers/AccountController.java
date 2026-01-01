package com.chisimdi.finance.service.controllers;

import com.chisimdi.finance.service.models.Account;
import com.chisimdi.finance.service.models.AccountDTO;
import com.chisimdi.finance.service.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/accounts")
@RestController
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    @Operation(summary = "Creates a customer account, available only to customers")
    @PreAuthorize("hasRole('ROLE_Customer') and principal.userId == #account.userId")
    @PostMapping("/customers")
    public AccountDTO createCustomerAccount(@Valid @RequestBody Account account){
        return accountService.createCustomerAccount(account);
    }

    @Operation(summary = "Creates a merchant account, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant') and principal.userId == #account.userId")
    @PostMapping("/merchants")
    public AccountDTO createMerchantAccount(@Valid @RequestBody Account account){
        return accountService.createMerchantAccount(account);
    }

    @Operation(summary = "Retrieves a specific account, available only to the owners of that account ")
    @PreAuthorize("principal.userId == #userId")
    @GetMapping("/{accountId}/users/{userId}")
    public AccountDTO findSpecificAccount(@PathVariable("accountId")int accountId,@PathVariable("userId")int userId){
        return accountService.findSpecificAccount(accountId, userId);
    }

    @Operation(summary = "Retrieves account by user Id, available only to the user of that account")
    @PreAuthorize("principal.userId == #userId")
    @GetMapping("/{userId}")
    public List<AccountDTO>findAccountByUser(@PathVariable("userId")int userId,@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return accountService.findAccountByUser(userId,pageNumber,size);
    }

    @Operation(summary = "Checks if a user and their account exists, service endpoint")
    @PreAuthorize("hasRole('ROLE_Service')")
    @GetMapping("/{accountId}/users/{userId}/exists")
    public Boolean doesUserAndAccountExist(@PathVariable("accountId")int accountId,@PathVariable("userId")int userId){
        return accountService.existsByUserIdAndAccountId(accountId, userId);
    }

    @Operation(summary = "Checks if a merchant account exists, service endpoint")
    @PreAuthorize("hasRole('ROLE_Service')")
    @GetMapping("/merchants/exists")
    public Boolean doesMerchantAccountExist(){
        return accountService.doesMerchantAccountExist();
    }
    @GetMapping("/")
    public List<AccountDTO>findAllAccounts(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return accountService.findAllAccounts(pageNumber, size);
    }

    @Operation(summary = "Retrieves merchant account, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @GetMapping("/merchants")
    public AccountDTO findMerchantAccount(){
        return accountService.findMerchantAccount();
    }

}
