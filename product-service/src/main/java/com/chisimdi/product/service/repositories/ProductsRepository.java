package com.chisimdi.product.service.repositories;

import com.chisimdi.product.service.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Integer> {
    Boolean existsByName(String name);
    Products findByName(String name);
}
