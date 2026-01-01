package com.chisimdi.finance.service.services;

import com.chisimdi.finance.service.exceptions.ExistsException;
import com.chisimdi.finance.service.exceptions.ResourceNotFoundException;
import com.chisimdi.finance.service.models.Account;
import com.chisimdi.finance.service.models.AccountDTO;
import com.chisimdi.finance.service.models.AccountType;
import com.chisimdi.finance.service.repositories.AccountRepository;
import com.chisimdi.finance.service.restclients.UserRestClient;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    private UserRestClient userRestClient;
    public AccountService(AccountRepository accountRepository, UserRestClient userRestClient){
        this.accountRepository=accountRepository;
        this.userRestClient=userRestClient;
    }

    public AccountDTO toAccountDTO(Account account){
        AccountDTO accountDTO=new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setUserId(account.getUserId());
        if(account.getBalance()!=null){
            accountDTO.setBalance(account.getBalance());
        }
        if(account.getAccountType()!=null){
            accountDTO.setAccountType(account.getAccountType());
        }
        return accountDTO;
    }

    @Transactional
    public AccountDTO createMerchantAccount(Account account){

        if(!userRestClient.doesUserExist(account.getUserId())){
            throw new ResourceNotFoundException("User with id "+account.getUserId()+" does not exist");
        }

       Account account1 = accountRepository.findByAccountType(AccountType.MERCHANT);

        if(account1!=null){
            throw new ExistsException("Merchant account already exists for this platform");
        }

        account.setAccountType(AccountType.MERCHANT);
        accountRepository.save(account);
        return toAccountDTO(account);
    }

    public AccountDTO createCustomerAccount(Account account){

        if(!userRestClient.doesUserExist(account.getUserId())){
            throw new ResourceNotFoundException("User with id "+account.getUserId()+" does not exist");
        }

        account.setAccountType(AccountType.CUSTOMER);
        accountRepository.save(account);
        return toAccountDTO(account);
    }

    public AccountDTO findSpecificAccount(int accountId,int userId){
        Account account= accountRepository.findByIdAndUserId(accountId,userId);
        if(account==null){
            throw new ResourceNotFoundException("Account with id "+accountId+" and user id "+userId+" not found");
        }
        return toAccountDTO(account);
    }

    public List<AccountDTO>findAccountByUser(int userId,int pageNumber, int size){
        Page<Account>accounts=accountRepository.findByUserId(userId, PageRequest.of(pageNumber,size));
        List<AccountDTO>accountDTOS=new ArrayList<>();
        for(Account a:accounts){
            accountDTOS.add(toAccountDTO(a));
        }
        return accountDTOS;
    }

  public   Boolean existsByUserIdAndAccountId(int accountId,int userid){
        return accountRepository.existsByIdAndUserId(accountId, userid);
    }
   public Boolean doesMerchantAccountExist(){
        return accountRepository.existsByAccountType(AccountType.MERCHANT);
    }
    public List<AccountDTO>findAllAccounts(int pageNumber, int size){
        Page<Account>accounts=accountRepository.findAll( PageRequest.of(pageNumber,size));
        List<AccountDTO>accountDTOS=new ArrayList<>();
        for(Account a:accounts){
            accountDTOS.add(toAccountDTO(a));
        }
        return accountDTOS;
    }

    public AccountDTO findMerchantAccount(){
        return toAccountDTO(accountRepository.findByAccountType(AccountType.MERCHANT));
    }



}
