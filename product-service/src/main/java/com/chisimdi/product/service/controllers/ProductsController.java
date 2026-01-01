package com.chisimdi.product.service.controllers;

import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.models.ProductsDTO;
import com.chisimdi.product.service.services.ProductService;
import com.chisimdi.product.service.utils.BulkImportStatus;
import com.chisimdi.product.service.utils.PathForImports;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductsController {
    private ProductService productService;

    public ProductsController(ProductService productService){
        this.productService=productService;
    }

@Operation(summary = "retrieves product by name, public endpoint")
    @GetMapping("/{name}")
    public ProductsDTO findProductByName(@PathVariable("name")String name){
        return productService.findProductsByName(name);
    }

    @Operation(summary = "retrieves all products, public endpoint")
    @GetMapping("/")
    public List<ProductsDTO>findAllProducts(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return productService.findALLProducts(pageNumber, size);
    }

    @Operation(summary = "Adds a product to the database, available to merchants and employees")
    @PreAuthorize("hasRole('ROLE_Merchant') or hasRole('ROLE_Employee')")
    @PostMapping("/")
    public ProductsDTO createProduct(@Valid @RequestBody Products products){
        return productService.addProducts(products);
    }

    @Operation(summary = "Mass import of products, available to merchants and employees")
    @PreAuthorize("hasRole('ROLE_Merchant') or hasRole('ROLE_Employee')")
    @PostMapping("/imports")
    public BulkImportStatus importProducts(@RequestParam("file") MultipartFile file, @RequestHeader("Idempotency-key")String idempotencyKey)throws IOException {
        return productService.massImportProductWithIdempotency(idempotencyKey,file);
    }

    @Operation(summary = "checks if a product exists , service endpoint")
    @PreAuthorize("hasRole('ROLE_Service')")
    @GetMapping("/{name}/exists")
    public Boolean doesProductExist(@PathVariable("name")String name){
        return productService.existByName(name);
    }

    @Operation(summary = "Checks the number of stocks a product has, service Endpoint")
    @PreAuthorize("hasRole('ROLE_Service')")
    @GetMapping("/{name}/stocks")
    public int getProductStockSize(@PathVariable("name")String name){
        return productService.getStockSize(name);
    }
    @PreAuthorize("hasRole('ROLE_Service')")

    @Operation(summary = "Checks the base price of a product, service endpoint")
    @GetMapping("/{name}/price")
    public BigDecimal getProductPrice(@PathVariable("name")String name){
        return productService.getStockPrice(name);
    }
}
