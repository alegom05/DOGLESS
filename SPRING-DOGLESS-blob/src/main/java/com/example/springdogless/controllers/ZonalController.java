package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;
import com.example.springdogless.entity.*;
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
    ZonalRepository zonaRepository;
    @Autowired
    DistritoRepository distritoRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    ProductRepository productRepository;
    @GetMapping({""})
    public String PaginaPrincipal(Model model) {
        return "zonal/paginaprincipal";
    }

    @GetMapping("/perfil_zonal")
    public String verperfilzonal(Model model) {
        return "zonal/perfil_zonal"; // Esto renderiza la vista perfil_superadmin.html
    }

    @GetMapping("/dashboard")
    public String elDashboardEstaTristeYAzul(Model model, @RequestParam(required = false) String zona) {
        /*model.addAttribute("listaProveedores", proveedorRepository.findAll());*/
        return "zonal/dashboard";
    }

    @GetMapping("/new")
    public String nuevoAgenteFrm(Model model) {
        model.addAttribute("listaZonas", zonaRepository.findAll());
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        return "zonal/agregar_agente";
    }


    @GetMapping(value = "agentes")
    public String listaAgentes(Model model) {
        model.addAttribute("listaAgentes", usuarioRepository.findByRol_RolAndBorrado("Agente",1));
        return "/zonal/agentes";
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
    @PostMapping("/guardar")
    public String crearAdminZonal(Usuario usuario, @RequestParam("idzonas") Integer idZona,
                                  @RequestParam("iddistritos") Integer idDistrito,
                                  RedirectAttributes attr) {

        // Asignar el rol de Agente (id = 3)
        Rol agenteRole = rolRepository.findById(3)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        usuario.setRol(agenteRole);

        // Asignar la zona seleccionada
        Zona zona = zonaRepository.findById(idZona)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada"));
        usuario.setZona(zona);

        // Asignar el distrito seleccionado
        Distrito distrito = distritoRepository.findById(idDistrito)
                .orElseThrow(() -> new IllegalArgumentException("Distrito no encontrado"));
        usuario.setDistrito(distrito);
        usuario.setBorrado(1);
        String contrasenaPorDefecto = "contraseñaPredeterminada";
        usuario.setContrasena(contrasenaPorDefecto);
        // Guardar el nuevo Adminzonal
        usuarioRepository.save(usuario);
        attr.addFlashAttribute("mensajeExito", "Agente creado correctamente");

        return "redirect:/zonal/agentes";
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

    @GetMapping("/verReposicion")
    public String verReposicion(Model model, @RequestParam("id") int id) {

        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            Reposicion reposicion = optReposicion.get();
            model.addAttribute("reposicion", reposicion);
            return "/zonal/verReposicion";
        } else {
            return "redirect:zonal/reposiciones";
        }
    }

    @GetMapping("/editarReposicion")
    public String editarReposicion(@ModelAttribute("product") Producto producto, Model model,
                                   @RequestParam("id") int id) {

        Optional<Producto> optProducto = productRepository.findById(id);

        if (optProducto.isPresent()) {
            producto = optProducto.get();
            model.addAttribute("producto", producto);
            model.addAttribute("listaReposiciones", zonaRepository.findAll());
            model.addAttribute("listaProductos", productRepository.findAll());

            return "zonal/editarReposicion";
        } else {
            return "redirect:/zonal/reposiciones";
        }
    }







    @GetMapping("/nuevaReposicion")
    public String nuevoAdminZonalFrm(Model model) {
        model.addAttribute("listaReposiciones", reposicionRepository.findAll());
        return "zonal/agregarReposicion";
    }



    /*
    @GetMapping("/edit")
    public String editarTransportista(@ModelAttribute("product") Product product,
                                      Model model, @RequestParam("id") int id) {

        Optional<Product> optProduct = productRepository.findById(id);

        if (optProduct.isPresent()) {
            product = optProduct.get();
            model.addAttribute("product", product);
            model.addAttribute("listaCategorias", categoryRepository.findAll());
            model.addAttribute("listaProveedores", supplierRepository.findAll());
            return "product/editFrm";
        } else {
            return "redirect:/product";
        }
    }
    */
    @GetMapping("/borrarReposicion")
    public String borrarTransportista(@RequestParam("id") int id,
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
