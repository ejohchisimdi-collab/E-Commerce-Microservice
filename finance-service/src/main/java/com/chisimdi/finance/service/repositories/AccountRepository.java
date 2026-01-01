package com.chisimdi.finance.service.repositories;

import com.chisimdi.finance.service.models.Account;
import com.chisimdi.finance.service.models.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Account findByAccountType( AccountType accountType);
    Page<Account> findByUserId(int userId, Pageable pageable);
    Account findByIdAndUserId(int accountId,int userId);
    Boolean existsByIdAndUserId(int accountId,int userId);
    Boolean existsByAccountType(AccountType accountType);
}
