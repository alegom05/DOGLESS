package com.example.holamundo.Repository;


import com.example.holamundo.entity.Usuario;
import com.example.holamundo.entity2.Category;
import com.example.holamundo.entity2.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{


}

