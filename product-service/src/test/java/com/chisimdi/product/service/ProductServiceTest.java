package com.chisimdi.product.service;

import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.repositories.BulkImportIdempotencyRepository;
import com.chisimdi.product.service.repositories.ProductsRepository;
import com.chisimdi.product.service.services.ProductService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.io.Resource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductsRepository productsRepository;
    @Mock
    private BulkImportIdempotencyRepository bulkImportIdempotencyRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void massImportTest()throws Exception{
        MockMultipartFile multipartFile=new MockMultipartFile("file","file.csv","text/csv",new FileInputStream("C:\\Users\\ejohc\\Downloads\\Ecommerce Microservice\\product-service\\EconmerceToUse.csv"));

        ArgumentCaptor<List<Products>>captor=ArgumentCaptor.forClass(List.class);

        when(productsRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));


        productService.massImportProduct(multipartFile);
        verify(productsRepository).saveAll(captor.capture());

        List<Products>products=captor.getValue();

        assertThat(products.get(0).getName()).isEqualTo("Chocolate Chip Cookie Mix");
        assertThat(products.get(0).getStockSize()).isEqualTo(59);







    }
}
