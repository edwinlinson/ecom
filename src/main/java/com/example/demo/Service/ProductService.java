package com.example.demo.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Product;
import com.example.demo.Repository.ProductRepo;

@Service
public class ProductService {
	@Autowired
	ProductRepo productRepository;

	public List<Product> getAllProducts(){
		return productRepository.findAll();
	}
//	public List<Product> getAllProducts( int pageNo, int pageSize){
//		Pageable pageable = Ps
//		return productRepository.findAll();
//	}
	public void addProduct(Product product) {
		productRepository.save(product);
	}
	public void removeProductbyId(Long id) {
		productRepository.deleteById(id);
	}
	public Optional<Product> getProductById(Long id){
		return productRepository.findById(id);
	}
//	public Product getProductsByeId(Long id){
//		return productRepository.findById(id);
//	}
	public List<Product> getAllProductsByCategoryId(int id){
		return productRepository.findAllByCategoryId(id);
	}
	
//	public int remainingQtyById(Long id){productRepository.}
	public boolean isProductExists(String name){return productRepository.existsProductByName(name);}
//	public Optional<Product> getProductByProductId(Long id){ return productRepository.getProductByProduct_Id(id); }

	public Page<Product> findPaginated(int pageNo, int pageSize){
//		Pageable pageable = PageRequest.of(pageNo-1,pageSize)
		Pageable pageable = PageRequest.of(pageNo-1,pageSize);
		return productRepository.findAll(pageable);
	}

	public List<Product> searchProducts(String searchTerm) {
		return getAllProducts().stream()
				.filter(product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase()))
				.collect(Collectors.toList());
	}
}
