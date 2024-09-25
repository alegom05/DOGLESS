package com.example.holamundo.repository;


import com.example.holamundo.entity.Category;
import com.example.holamundo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

    public List<Product> findByCategory(Category category);

    List<Product> findByProductname(String productName);

}

