package com.example.demo.ServiceImpl;

import com.example.demo.Model.Category;
import com.example.demo.Model.Offers;
import com.example.demo.Model.Product;
import com.example.demo.Repository.OfferRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {
    @Autowired
    ProductService productService;

    @Autowired
    OfferRepo offerRepo;
    private ProductRepo productRepo;
    private CategoryService categoryService;

    public Offers add(Offers offerd) {
        if (offerd.getOfferType().equals("Product")){
            Product product = productService.getProductById(offerd.getOfferProductId()).get();
            Double  oldDiscount = (double) product.getPrice() * ((double) offerd.getOffPercentage()/100);
            String formattedDiscount = String.format("%.2f",oldDiscount);
            Double discount = Double.parseDouble(formattedDiscount);
            String formattedSalePrice = String.format("%2.f",product.getPrice()-discount);
            Double Price = Double.parseDouble(formattedDiscount);
            product.setPrice(Price);
            offerd.setApplicableForProductName(product.getName());
            productRepo.save(product);
        }else {
            Category category = categoryService.findById(offerd.getOfferCategoryId()).get();
            offerd.setApplicableForCategoryName(category.getName());
            int id = offerd.getOfferCategoryId();
            List<Product> productList = productService.getAllProductsByCategoryId(id);
            for (Product product : productList){
                Double  oldDiscount = (double) product.getPrice() * ((double) offerd.getOffPercentage()/100);
                String formattedDiscount = String.format("%.2f",oldDiscount);
                Double discount = Double.parseDouble(formattedDiscount);
                String formattedSalePrice = String.format("%2.f",product.getPrice()-discount);
                Double Price = Double.parseDouble(formattedDiscount);
                product.setPrice(Price);
                offerd.setApplicableForProductName(product.getName());
                productRepo.save(product);
            }
        }
        offerRepo.save(offerd);
        return offerd;
    }

    public List<Offers> getAllOffers() {
       return offerRepo.findAll();
    }
}
