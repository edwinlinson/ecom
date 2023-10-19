package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.demo.Model.Category;

import java.util.Optional;


public interface CategoryRepo extends JpaRepository<Category, Integer> {
  Optional<Category>  findCategoryByName(String name);
  boolean existsCategoriesByName(String name);
}
