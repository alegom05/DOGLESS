package com.example.holamundo.repository2;


import com.example.holamundo.entity2.Category;
import com.example.holamundo.entity2.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

    public List<Product> findByCategory(Category category);

    List<Product> findByProductname(String productName);

}

