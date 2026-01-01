package com.chisimdi.product.service.services;

import com.chisimdi.product.service.exceptions.ResourceNotFoundException;
import com.chisimdi.product.service.models.BulkImportIdempotency;
import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.models.ProductsDTO;
import com.chisimdi.product.service.models.ReleaseStocksIdempotency;
import com.chisimdi.product.service.repositories.BulkImportIdempotencyRepository;
import com.chisimdi.product.service.repositories.ProductsRepository;
import com.chisimdi.product.service.repositories.ReleaseStockIdempotencyRepository;
import com.chisimdi.product.service.repositories.ReserveStockIdempotencyRepository;
import com.chisimdi.product.service.utils.BulkImportStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private ProductsRepository productsRepository;
    private BulkImportIdempotencyRepository bulkImportIdempotencyRepository;

    public ProductService (ProductsRepository productsRepository, BulkImportIdempotencyRepository bulkImportIdempotencyRepository){
        this.productsRepository=productsRepository;
        this.bulkImportIdempotencyRepository=bulkImportIdempotencyRepository;


    }

    public ProductsDTO toProductsDTO(Products products){
        ProductsDTO productsDTO=new ProductsDTO();
        productsDTO.setId(products.getId());

        if(products.getName()!=null){
       productsDTO.setName(products.getName());
        }
        productsDTO.setStockSize(products.getStockSize());

        if(products.getCategory()!=null){
            productsDTO.setCategory(products.getCategory());
        }
        if(products.getDescription()!=null){
            productsDTO.setDescription(products.getDescription());
        }
        if(products.getPrice()!=null){
            productsDTO.setPrice(products.getPrice());
        }
        return productsDTO;

    }
    public ProductsDTO addProducts(Products products){
        return toProductsDTO(productsRepository.save(products));
    }

    public BulkImportStatus massImportProduct(MultipartFile path)throws IOException {



        Reader reader=new InputStreamReader(path.getInputStream());
            Iterable<CSVRecord>records= CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader);
            List<Products>products=new ArrayList<>();
            for(CSVRecord csvRecord:records){
                Products products1=new Products();
                products1.setName(csvRecord.get(1));
                products1.setStockSize( Integer.parseInt(csvRecord.get(2)));
                products1.setCategory(csvRecord.get(3));
                products1.setDescription(csvRecord.get(4));
                products1.setPrice(new BigDecimal(csvRecord.get(5)));
                products.add(products1);
            }
            productsRepository.saveAll(products);

            return BulkImportStatus.COMPLETED;
        }






    public BulkImportStatus massImportProductWithIdempotency(String idempotencyKey,MultipartFile path)throws IOException{
        BulkImportIdempotency bulkImportIdempotency1=bulkImportIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if(bulkImportIdempotency1!=null){
            return bulkImportIdempotency1.getBulkImportStatus();
        }

        BulkImportStatus bulkImportStatus=massImportProduct(path);
        BulkImportIdempotency bulkImportIdempotency=new BulkImportIdempotency();
        bulkImportIdempotency.setBulkImportStatus(bulkImportStatus);
        bulkImportIdempotency.setIdempotencyKey(idempotencyKey);
        bulkImportIdempotencyRepository.save(bulkImportIdempotency);
        return bulkImportStatus;
    }

    public ProductsDTO findProductsByName(String name){
        Products products=productsRepository.findByName(name);
        if(products==null){
            throw new ResourceNotFoundException("Product with name "+name+" not found");
        }
        return toProductsDTO(products);
    }
    public List<ProductsDTO>findALLProducts(int pageNumber,int size){
        Page<Products>products=productsRepository.findAll(PageRequest.of(pageNumber, size));
        List<ProductsDTO>productsDTOS=new ArrayList<>();

        for(Products p:products){
            productsDTOS.add(toProductsDTO(p));
        }
        return productsDTOS;
    }
    public Boolean existByName(String name){
        return productsRepository.existsByName(name);
    }
    public int getStockSize(String name){
        return productsRepository.findByName(name).getStockSize();
    }
    public BigDecimal getStockPrice(String name){return productsRepository.findByName(name).getPrice();}

}
