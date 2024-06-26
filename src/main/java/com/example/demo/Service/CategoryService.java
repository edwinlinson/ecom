package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.Category;
import com.example.demo.Repository.CategoryRepo;

@Service
public class CategoryService {
	@Autowired
	CategoryRepo categoryRepository;
	public Optional<Category> findById(long id){
		return categoryRepository.findById((int) id);
	}
	public List<Category> getAllCategory(){
		return categoryRepository.findAll();
	}
	public void addCategory(Category category) {
		categoryRepository.save(category);
	}
	public void deleteCategoryById(int id) {
		categoryRepository.deleteById(id);
	}
	public Optional<Category> getCatById(int id) {
		return categoryRepository.findById(id);
	}
	public Optional<Category> getCatByName(String name){return categoryRepository.findCategoryByName(name);}
	public boolean isCategoryExists(String name){return categoryRepository.existsCategoriesByName(name);}

}
