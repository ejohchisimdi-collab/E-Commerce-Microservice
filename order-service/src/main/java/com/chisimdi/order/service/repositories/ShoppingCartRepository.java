package com.chisimdi.order.service.repositories;

import com.chisimdi.order.service.models.CartStatus;
import com.chisimdi.order.service.models.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Integer> {
    Page<ShoppingCart>findByUserId(int userId, Pageable pageable);
    Optional<ShoppingCart> findByIdAndCartStatus(int id, CartStatus cartStatus);
}
