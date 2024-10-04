package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;
import com.example.springdogless.entity.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.Optional;

@Controller
@RequestMapping({"usuario", "usuario/"})

public class UsuarioController {

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
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    ImportacionRepository importacionRepository;

    @GetMapping({""})
    public String PaginaPrincipal(Model model) {
        return "zonal/paginaprincipal";
    }

    @GetMapping("/new")
    public String nuevoAgenteFrm(Model model) {
        model.addAttribute("listaZonas", zonaRepository.findAll());
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        return "usuario/agregar_agente";
    }

    //Libro de reclamaciones
    @GetMapping("/libro")
    public String nuevaReclamacion(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        return "usuario/libro";
    }

    //El resto no está hecho

    /*

    @GetMapping(value = "agentes")
    public String listaAgentes(Model model) {
        model.addAttribute("listaAgentes", usuarioRepository.findByRol_RolAndBorrado("Agente",1));
        return "usuario/agentes";
    }



    @GetMapping("/nuevoAgente")
    public String nuevoAgente(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        return "usuario/editarAgente";
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

        return "redirect:/usuario/agentes";
    }
    @GetMapping("/editagente")
    public String editarAgente(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("listaZonas", zonaRepository.findAll());
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            return "usuario/editarAgente";
        } else {
            return "redirect:/usuario/agentes";
        }
    }

    @PostMapping("/saveagente")
    public String guardarAgente(@RequestParam("id") int id,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("apellido") String apellido,
                                @RequestParam("correo") String correo,
                                @RequestParam("telefono") String telefono,
                                @RequestParam("ruc") String ruc,
                                @RequestParam("codigoAduana") String codigoaduana,
                                @RequestParam("razonsocial") String razonsocial,
                                @RequestParam int zona,
                                @RequestParam int distrito,
                                RedirectAttributes attr) {
        // Obtener el usuario existente
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Actualizar solo los campos editables
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setRuc(ruc);
            usuario.setCodigoaduana(codigoaduana);
            usuario.setRazonsocial(razonsocial);

            // Buscar las entidades relacionadas por sus IDs
            Zona zonaEntity = zonaRepository.findById(zona).orElse(null); // Se maneja si no existe
            Distrito distritoEntity = distritoRepository.findById(distrito).orElse(null); // Se maneja si no existe

            // Asignar las entidades encontradas al usuario
            usuario.setZona(zonaEntity);
            usuario.setDistrito(distritoEntity);

            // Guardar el usuario actualizado
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("mensajeExito", "Cambios guardados correctamente");
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
        }

        return "redirect:/usuario/agentes";
    }
    @PostMapping("/deleteagente")
    public String borrarAgente(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuario.setBorrado(0);
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msg", "Agente borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Agente no encontrado");
        }

        return "redirect:/usuario/agentes";
    }

    @PostMapping("/guardarAgente")
    public String guardarAgente(RedirectAttributes attr, Model model,
                                  @ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { //si no hay errores, se realiza el flujo normal

            if (usuario.getNombre().equals("gaseosa")) {
                model.addAttribute("msg", "Error al crear producto");
                model.addAttribute("listaUsuarios", usuarioRepository.findAll());
                return "usuario/editarZonal";
            } else {
                if (usuario.getId() == 0) {
                    attr.addFlashAttribute("msg", "Usuario creado exitosamente");
                } else {
                    attr.addFlashAttribute("msg", "Usuario actualizado exitosamente");
                }
                usuario.setBorrado(1);
                usuarioRepository.save(usuario);

                return "redirect:/usuario/agentes";
            }

        } else { //hay al menos 1 error
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            return "product/editarZonal";
        }
    }


    @GetMapping("/editimportaciones")
    public String editarImportaciones(Model model, @RequestParam("id") int id) {

        Optional<Importacion> optImportacion = importacionRepository.findById(id);

        if (optImportacion.isPresent()) {
            Importacion importacion = optImportacion.get();
            model.addAttribute("importacion", importacion);
            model.addAttribute("listaZonas", zonaRepository.findAll());
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            return "usuario/editarImportacion";
        } else {
            return "redirect:/usuario/importaciones";
        }
    }
    @PostMapping("/deleteimportacion")
    public String borrarImportacion(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Importacion> optImportacion = importacionRepository.findById(id);

        if (optImportacion.isPresent()) {
            Importacion importacion = optImportacion.get();
            importacion.setBorrado(0);
            importacionRepository.save(importacion);
            attr.addFlashAttribute("mensajeExito", "Importacion borrada exitosamente");
        } else {
            attr.addFlashAttribute("error", "Agente no encontrado");
        }

        return "redirect:/usuario/importaciones";
    }
    @PostMapping("/saveimportacion")
    public String guardarImportacion(@RequestParam("id") int id,
                                     @RequestParam("fechaPedido") Date fechaPedido,

                                     RedirectAttributes attr) {
        // Obtener el usuario existente
        Optional<Importacion> optImportacion = importacionRepository.findById(id);

        if (optImportacion.isPresent()) {
            Importacion importacion = optImportacion.get();

            // Actualizar solo los campos editables
            importacion.setFechaPedido(fechaPedido);


            // Guardar el usuario actualizado
            importacionRepository.save(importacion);
            attr.addFlashAttribute("mensajeExito", "Cambios guardados correctamente");
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
        }

        return "redirect:/usuario/importaciones";
    }
    //Puede ser innecesario
    @GetMapping("/verImportacion")
    public String verImportacion(Model model, @RequestParam("id") int id) {

        Optional<Importacion> optionalImportacion = importacionRepository.findById(id);

        if (optionalImportacion.isPresent()) {
            Importacion importacion = optionalImportacion.get();
            model.addAttribute("importacion", importacion);
            return "usuario/verImportacion";
        } else {
            return "redirect:usuario/importaciones";
        }
    }


    //Reposiciones
    @GetMapping(value = "reposiciones")
    public String listaReposiciones(Model model) {
        model.addAttribute("listaReposiciones", reposicionRepository.findByBorrado(1));
        return "usuario/reposiciones";
    }

    @GetMapping("/verReposicion")
    public String verReposicion(Model model, @RequestParam("id") int id) {

        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            Reposicion reposicion = optReposicion.get();
            model.addAttribute("reposicion", reposicion);
            return "usuario/verReposicion";
        } else {
            return "redirect:usuario/reposiciones";
        }
    }

    @GetMapping("/nuevaReposicion")
    public String nuevaReposicion(Model model, @ModelAttribute("reposicion") Reposicion reposicion) {
        model.addAttribute("listaReposiciones", reposicionRepository.findAll());
        model.addAttribute("listaProveedores", proveedorRepository.findAll());
        model.addAttribute("listaProductos", productRepository.findAll());
        return "usuario/editarReposicion";
    }

    @GetMapping("/editarReposicion")
    public String editarReposicion(@ModelAttribute("reposicion") Reposicion reposicion,
                                   Model model,
                                   @RequestParam(value="id", required = false) int id) {

        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            reposicion = optReposicion.get();
            model.addAttribute("reposicion", reposicion);
            model.addAttribute("listaReposiciones", reposicionRepository.findAll());
            model.addAttribute("listaProveedores", proveedorRepository.findAll());
            model.addAttribute("listaProductos", productRepository.findAll());

            return "usuario/editarReposicion";
        } else {
            return "redirect:/usuario/reposiciones";
        }
    }

    @PostMapping("/guardarReposicion")
    public String guardarReposicion(RedirectAttributes attr, Model model,
                                  @ModelAttribute("reposicion") @Valid Reposicion reposicion, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { //si no hay errores, se realiza el flujo normal

            if (reposicion.getCantidad()==8) {
                model.addAttribute("msg", "Error al crear producto");
                model.addAttribute("listaProductos", productRepository.findAll());
                model.addAttribute("listaReposiciones", reposicionRepository.findAll());

                return "usuario/editarReposicion";
            } else {
                if (reposicion.getId() == null) {
                    attr.addFlashAttribute("msg", "Reposición creada exitosamente");
                } else {
                    attr.addFlashAttribute("msg", "Reposición actualizada exitosamente");
                }
                if (reposicion.getProducto() != null && reposicion.getProducto().getId() == null) {
                    productRepository.save(reposicion.getProducto());
                }
                if (reposicion.getProducto() != null && reposicion.getProducto().getProveedor() != null
                        && reposicion.getProducto().getProveedor().getId() == null) {
                    proveedorRepository.save(reposicion.getProducto().getProveedor());
                }
                reposicion.setBorrado(1);
                reposicionRepository.save(reposicion);
                return "redirect:/usuario/reposiciones";
            }

        } else { //hay al menos 1 error
            model.addAttribute("listaProductos", productRepository.findAll());
            model.addAttribute("listaReposiciones", reposicionRepository.findAll());
            return "usuario/editarReposicion";
        }
    }
    

    @PostMapping("/borrarReposicion")
    public String borrarAdminusuario(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Reposicion> optReposicion = reposicionRepository.findById(id);

        if (optReposicion.isPresent()) {
            Reposicion reposicion = optReposicion.get();
            reposicion.setBorrado(2);
            reposicionRepository.save(reposicion);
            attr.addFlashAttribute("msg", "Reposición borrada exitosamente");
        } else {
            attr.addFlashAttribute("error", "Reposición no encontrada");
        }

        return "redirect:/usuario/reposiciones";
    }

    */








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
        return "redirect:/usuario/reposiciones";

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
