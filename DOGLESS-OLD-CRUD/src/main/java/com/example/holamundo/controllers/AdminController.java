package com.example.holamundo.controllers;

import com.example.holamundo.Repository.DistritoRepository;
import com.example.holamundo.Repository.RolRepository;
import com.example.holamundo.Repository.UsuarioRepository;

import com.example.holamundo.Repository.ZonaRepository;
import com.example.holamundo.entity.Distrito;
import com.example.holamundo.entity.Rol;
import com.example.holamundo.entity.Usuario;
import com.example.holamundo.entity.Zona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping({"admin",""})
public class AdminController {

    /*@GetMapping("/")
    @ResponseBody
    public String unaPersona() {
        return "olapaola5";
    }*/
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ZonaRepository zonaRepository;
    @Autowired
    DistritoRepository distritoRepository;
    @Autowired
    RolRepository rolRepository;

    @GetMapping({"lista",""})
    public String listaUsuariosTotales(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/paginaprincipal";
//        return "usuario/list";
    }
    @GetMapping("adminzonal")
    public String listaAdminZonal(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));

        return "admin/adminzonales";
    }
    @GetMapping("/new")
    public String nuevoAdminZonalFrm(Model model) {
        model.addAttribute("listaZonas",zonaRepository.findAll());
        model.addAttribute("listaDistritos",distritoRepository.findAll());
        return "admin/tables/agregar_adminzonal";
    }
    @PostMapping("/guardar")
    public String crearAdminZonal(Usuario usuario, @RequestParam("idzonas") Integer idZona,
                                  @RequestParam("iddistritos") Integer idDistrito,
                                  RedirectAttributes attr) {

        // Asignar el rol de Adminzonal (id = 2)
        Rol adminzonalRole = rolRepository.findById(2)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        usuario.setRol(adminzonalRole);

        // Asignar la zona seleccionada
        Zona zona = zonaRepository.findById(idZona)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada"));
        usuario.setZona(zona);

        // Asignar el distrito seleccionado
        Distrito distrito = distritoRepository.findById(idDistrito)
                .orElseThrow(() -> new IllegalArgumentException("Distrito no encontrado"));
        usuario.setDistrito(distrito);

        // Guardar el nuevo Adminzonal
        usuarioRepository.save(usuario);
        attr.addFlashAttribute("mensajeExito", "Administrador zonal creado correctamente");

        return "redirect:/admin/adminzonal";
    }


    @GetMapping("/edit")
    public String editarAdminZonal(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("listaZonas",zonaRepository.findAll());
            model.addAttribute("listaDistritos",distritoRepository.findAll());
            return "admin/tables/editar_adminzonal";
        } else {
            return "redirect:/admin/adminzonal";
        }
    }
    @PostMapping("/save")
    public String guardarAdminZonal(@RequestParam("id") int id,
                                    @RequestParam String correo,
                                    @RequestParam String telefono,
                                    @RequestParam int zona,
                                    @RequestParam int distrito,
                                    RedirectAttributes attr) {
        // Obtener el usuario existente
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Actualizar solo los campos editables
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);

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

        return "redirect:/admin/adminzonal";
    }


    @GetMapping({"listaZonales",""})
    public String listaZonales(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));

        return "admin2/list";
//        return "usuario/list";
    }

    @GetMapping({"listaAgentes",""})
    public String listaAgentes(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(3));

        return "admin2/list";
//        return "usuario/list";
    }

    @GetMapping({"listaUsuarios",""})
    public String listaUsuarios(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));

        return "admin2/list";
//        return "usuario/list";
    }

    @GetMapping({"lista2",""})
    public String listaUsuarios2(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin2/list";
//        return "usuario/list";
    }



    /*
    @GetMapping(value = "new")
    public String nuevoProductoFrm(Model model, @ModelAttribute("product") Product product) {
        List<Category> listaCategorias = categoryRepository.findAll();
        List<Supplier> listaProveedores = supplierRepository.findAll();
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();

        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("listaProveedores", listaProveedores);
        //model.addAttribute("listaOrderDetails", listaOrderDetailsm);
        return "product/newFrm";
    }


    @GetMapping("/edit")
    public String editarProducto(@ModelAttribute("product") Product product, Model model, @RequestParam("id") int id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            product = optProduct.get();
            model.addAttribute("product", product);
            model.addAttribute("listaCategorias", categoryRepository.findAll());
            model.addAttribute("listaProveedores", supplierRepository.findAll());
            model.addAttribute("listaOrderDetails", orderDetailsRepository.findAll());

            return "product/newFrm";
        } else {
            return "redirect:/product";
        }
    }



    @PostMapping("/save")
    public String guardarProducto(RedirectAttributes attr,
                                  Model model,
                                  @ModelAttribute("product") @Valid Product product,
                                  BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            model.addAttribute("listaCategorias", categoryRepository.findAll());
            model.addAttribute("listaProveedores", supplierRepository.findAll());
            return "product/newFrm";
        }else{
            if (product.getId() == 0) {
                List<Product> productList = productRepository.findByProductname(product.getProductname());
                boolean existe = false;
                for (Product p : productList) {
                    if (p.getProductname().equals(product.getProductname())) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    System.out.println("El producto existe");
                    model.addAttribute("listaCategorias", categoryRepository.findAll());
                    model.addAttribute("listaProveedores", supplierRepository.findAll());
                    return "product/newFrm";
                } else {
                    attr.addFlashAttribute("msg", "Producto creado exitosamente");
                    productRepository.save(product);
                    return "redirect:/product";
                }
            } else {
                attr.addFlashAttribute("msg", "Producto actualizado exitosamente");
                productRepository.save(product);
                return "redirect:/product";
            }
        }
    }


    @GetMapping("/delete")
    public String borrarTransportista(Model model,
                                        @RequestParam("id") int id,
                                        RedirectAttributes attr) {

        Optional<Product> optProduct = productRepository.findById(id);

        if (optProduct.isPresent()) {
            productRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        }
        return "redirect:/product";

    }

    */



}
