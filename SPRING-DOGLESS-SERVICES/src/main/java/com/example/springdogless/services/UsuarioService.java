package com.example.springdogless.services;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void save(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public List<Usuario> getUsuariosByEstado(boolean estadoBaneado) {
        return estadoBaneado ? usuarioRepository.findBaneados() : usuarioRepository.findActivos();
    }
}

