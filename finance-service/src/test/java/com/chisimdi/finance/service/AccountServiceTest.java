package com.chisimdi.finance.service;

import com.chisimdi.finance.service.exceptions.ExistsException;
import com.chisimdi.finance.service.exceptions.ResourceNotFoundException;
import com.chisimdi.finance.service.models.Account;
import com.chisimdi.finance.service.models.AccountType;
import com.chisimdi.finance.service.repositories.AccountRepository;
import com.chisimdi.finance.service.restclients.UserRestClient;
import com.chisimdi.finance.service.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRestClient userRestClient;
    @InjectMocks
    AccountService accountService;

    @Test
    void createMerchantAccountTest() {
        Account account = new Account();
        account.setUserId(1);

        when(userRestClient.doesUserExist(account.getUserId())).thenReturn(true);
        when(accountRepository.findByAccountType(AccountType.MERCHANT)).thenReturn(null);
        when(accountRepository.save(account)).thenReturn(account);

        accountService.createMerchantAccount(account);

        verify(userRestClient).doesUserExist(account.getUserId());
        verify(accountRepository).save(account);

    }

    @Test
    void createMerchantAccountTest_ThrowsResourceNotFoundException() {
        Account account = new Account();
        account.setUserId(1);

        when(userRestClient.doesUserExist(account.getUserId())).thenReturn(false);


        assertThatThrownBy(()-> accountService.createMerchantAccount(account)).isInstanceOf(ResourceNotFoundException.class);

        verify(userRestClient).doesUserExist(account.getUserId());
        verify(accountRepository,never()).save(account);

    }
    @Test
    void createMerchantAccountTest_ThrowsExistsException() {
        Account account = new Account();
        account.setUserId(1);

        when(userRestClient.doesUserExist(account.getUserId())).thenReturn(true);
        when(accountRepository.findByAccountType(AccountType.MERCHANT)).thenReturn(account);


        assertThatThrownBy(()->accountService.createMerchantAccount(account)).isInstanceOf(ExistsException.class);

        verify(userRestClient).doesUserExist(account.getUserId());
        verify(accountRepository,never()).save(account);

    }
    @Test
    void createCustomerAccountTest() {
        Account account = new Account();
        account.setUserId(1);

        when(userRestClient.doesUserExist(account.getUserId())).thenReturn(true);
        when(accountRepository.save(account)).thenReturn(account);

        accountService.createCustomerAccount(account);

        verify(userRestClient).doesUserExist(account.getUserId());
        verify(accountRepository).save(account);

    }

    @Test
    void createCustomerAccountTest_ThrowsResourceNotFoundException() {
        Account account = new Account();
        account.setUserId(1);

        when(userRestClient.doesUserExist(account.getUserId())).thenReturn(false);


   assertThatThrownBy(()->accountService.createCustomerAccount(account)).isInstanceOf(ResourceNotFoundException.class);

        verify(userRestClient).doesUserExist(account.getUserId());
        verify(accountRepository,never()).save(account);

    }


}
