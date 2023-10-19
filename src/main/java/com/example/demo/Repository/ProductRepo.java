package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.demo.Model.Product;

public interface ProductRepo extends JpaRepository<Product, Long>{
	List<Product> findAllByCategoryId(int id);
	boolean existsProductByName(String name);

//    Optional<Product> getProductByProduct_Id(Long id);
}
