package com.example.springdogless.controllers;

import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.ReposicionRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Reposicion;
import com.example.springdogless.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping({"zonal", "zonal/"})

public class ZonalController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ReposicionRepository reposicionRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping(value = "agentes")
    public String listaAgentes(Model model) {
        model.addAttribute("listaAgentes", usuarioRepository.findByRol_RolAndBorrado("Agente",1));
        return "/zonal/zagentes";
    }

    @GetMapping("/veragente")
    public String verAgente(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "zonal/verAgente";
        } else {
            return "redirect:/zonal/agentes";
        }
    }

    @GetMapping("/nuevoAgente")
    public String nuevoAgente(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        return "zonal/editarAgente";
    }

    @PostMapping("/guardarAgente")
    public String guardarAgente(RedirectAttributes attr, Model model,
                                  @ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { //si no hay errores, se realiza el flujo normal

            if (usuario.getNombre().equals("gaseosa")) {
                model.addAttribute("msg", "Error al crear producto");
                model.addAttribute("listaUsuarios", usuarioRepository.findAll());
                return "zonal/editarZonal";
            } else {
                if (usuario.getId() == 0) {
                    attr.addFlashAttribute("msg", "Usuario creado exitosamente");
                } else {
                    attr.addFlashAttribute("msg", "Usuario actualizado exitosamente");
                }
                usuario.setBorrado(1);
                usuarioRepository.save(usuario);

                return "redirect:/zonal/agentes";
            }

        } else { //hay al menos 1 error
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            return "product/editarZonal";
        }
    }

    //Importaciones
    @GetMapping(value = "importaciones")
    public String listaImportaciones(Model model) {
        model.addAttribute("listaImportaciones", usuarioRepository.findByRol_RolAndBorrado("Agente",1));
        return "/zonal/importaciones";
    }


    //Reposiciones
    @GetMapping(value = "reposiciones")
    public String listaReposiciones(Model model) {
        model.addAttribute("listaReposiciones", reposicionRepository.findAll());
        return "/zonal/reposiciones";
    }


    @GetMapping("/editarReposiciones")
    public String editarReposiciones(@ModelAttribute("reposicion") Reposicion reposicion,
                                      Model model, @RequestParam("id") int id) {

        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            reposicion = optReposicion.get();
            model.addAttribute("reposicion", reposicion);
            model.addAttribute("listaReposiciones", reposicionRepository.findAll());
            model.addAttribute("listaProductos", productRepository.findAll());
            return "reposiciones/editarReposiciones";
        } else {
            return "redirect:/reposiciones";
        }
    }

    @PostMapping("/borrarReposicion")
    public String borrarReposicion(@RequestParam("id") int id,
                                      RedirectAttributes attr) {

        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            Reposicion reposicion = optReposicion.get();
            reposicion.setBorrado(0);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Producto no encontrado");
        }
        return "redirect:/zonal/reposiciones";

    }

    /*
    @PostMapping("/deleteadminzonal")
    public String borrarAdminZonal(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuario.setBorrado(0);
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msg", "Admin borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Admin no encontrado");
        }

        return "redirect:/admin/adminzonal";
    }
    */
}
