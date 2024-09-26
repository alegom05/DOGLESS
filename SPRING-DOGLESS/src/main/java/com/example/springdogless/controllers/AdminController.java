package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;

import com.example.springdogless.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequestMapping({"admin", "admin/",""})
public class AdminController {

    /*@GetMapping("/")
    @ResponseBody
    public String unaPersona() {
        return "olapaola5";
    }*/

    // Mapea la vista del login

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ZonaRepository zonaRepository;
    @Autowired
    DistritoRepository distritoRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    SolicitudRepository solicitudRepository;
    @Autowired
    ProveedorRepository proveedorRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    ReposicionesRepository reposicionesRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping({"/lista", ""})
    public String listaUsuariosTotales(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/paginaPrincipal";
//        return "usuario/list";
    }

    @GetMapping("/adminzonal")
    public String listaAdminZonal(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/adminzonales";
    }


    @GetMapping("/agentes")
    public String listaAgentes(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));

        return "admin/agentes";

    }

    @GetMapping("/usuarios")
    public String listaUsuarios(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));

        return "admin/usuarios";
    }



    @GetMapping("/editadminzonal")
    public String editarAdminZonal(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("listaZonas", zonaRepository.findAll());
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            return "admin/editar_adminzonal";
        } else {
            return "redirect:/admin/adminzonal";
        }
    }

    @GetMapping("/editagente")
    public String editarAgente(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("listaZonas", zonaRepository.findAll());
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            return "admin/editar_agente";
        } else {
            return "redirect:/admin/agentes";
        }
    }

    @GetMapping("/editusuario")
    public String editarUsuario(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("listaZonas", zonaRepository.findAll());
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            return "admin/editar_usuarios";
        } else {
            return "redirect:/admin/usuarios";
        }
    }




    @GetMapping("/new")
    public String nuevoAdminZonalFrm(Model model) {
        model.addAttribute("listaZonas", zonaRepository.findAll());
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        return "admin/agregar_adminzonal";
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


    @GetMapping("lista2")
    public String listaUsuarios2(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(2));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/list";
//        return "usuario/list";
    }

    @GetMapping("solicitudes")
    public String listaSolicitudes(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaSolicitudes", solicitudRepository.findAll());
        return "admin/solicitudes";
    }
    //Aceptar Solicitudes
    @GetMapping(value="{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Integer id , RedirectAttributes redirectAttributes){
        Solicitud solicitud = solicitudRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Solicitud no encontrada"));
        solicitud.setVeredicto((byte)1); //aceptada
        solicitudRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("mensajeExito","La solicitud ha sido aceptada"); // para enviar mensaje temporal
        return "redirect:admin/solicitudes";
    }
    //Denegar Solicitudes
    @GetMapping(value="{id}/denegar")
    public String denegarSolicitud(@PathVariable Integer id , RedirectAttributes redirectAttributes){
        Solicitud solicitud = solicitudRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Solicitud no encontrada"));
        solicitud.setVeredicto((byte)0);  //aceptada
        solicitudRepository.save(solicitud);
        redirectAttributes.addFlashAttribute("mensajeExito","La solicitud ha sido denegada"); // para enviar mensaje temporal
        return "redirect:admin/solicitudes";
    }

    //Vista de Proveedores

    @GetMapping("proveedores")
    public String listaProveedores(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProveedores", proveedorRepository.findAll());
        return "admin/proveedores";
    }


    @GetMapping(value = "nuevoProveedor")
    public String nuevoProveedor(Model model, @ModelAttribute("proveedor") Proveedor proveedor) {
        List<Proveedor> listaProveedores = proveedorRepository.findAll();
        model.addAttribute("listaProveedores", listaProveedores);
        //model.addAttribute("listaOrderDetails", listaOrderDetailsm);
        return "admin/nuevoProveedor";
    }


    @GetMapping("/editarProveedor")
    public String editarProveedor(@ModelAttribute("proveedor") Proveedor proveedor, Model model, @RequestParam("id") int id) {
        Optional<Proveedor> optProveedor = proveedorRepository.findById(id);
        if (optProveedor.isPresent()) {
            proveedor = optProveedor.get();
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("listaProveedores", proveedorRepository.findAll());

            return "admin2/newFrmP";
        } else {
            return "redirect:/admin/proveedores";
        }
    }


    @PostMapping("/guardarProveedor")
    public String guardarProveedor(Proveedor proveedor, RedirectAttributes attr) {
        proveedorRepository.save(proveedor);
        attr.addFlashAttribute("msg", "Proveedor creado exitosamente");
        return "redirect:/proveedores";
    }


    //Vista de productos
    @GetMapping("productos")
    public String listaProductos(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProductos", productRepository.findByBorrado(1));
        return "admin/productos";

    }

    @GetMapping(value = "nuevoProducto")
    public String nuevoProductoFrm(Model model, @ModelAttribute("product") Producto producto) {
        model.addAttribute("listaProductos", productRepository.findAll());
        model.addAttribute("listaProveedores", proveedorRepository.findAll());
        model.addAttribute("producto", producto);

        //model.addAttribute("listaOrderDetails", listaOrderDetailsm);
        return "admin/nuevoProducto";
    }

    @GetMapping("/editarProducto")
    public String editarProducto(@ModelAttribute("producto") Producto producto, Model model, @RequestParam("id") int id) {
        Optional<Producto> optProducto = productRepository.findById(id);
        if (optProducto.isPresent()) {
            producto = optProducto.get();
            model.addAttribute("producto", producto);
            model.addAttribute("listaProductos", proveedorRepository.findAll());
            model.addAttribute("listaProveedores", proveedorRepository.findAll());

            return "admin/editarProducto";
        } else {
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/guardarProducto")
    public String guardarProducto(@RequestParam("idproveedor") Integer idproveedor, Producto producto, RedirectAttributes attr) {
        Optional<Proveedor> optProducto = proveedorRepository.findById(idproveedor);

        if (optProducto.isPresent()) {
            Proveedor proveedor = optProducto.get();
            producto.setProveedor(proveedor);  // Asignar el proveedor al producto
        } else {
            attr.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/admin/productos";
        }
        producto.setBorrado(1);
        productRepository.save(producto);

        attr.addFlashAttribute("msg", "Producto creado exitosamente");
        return "redirect:/admin/productos";
    }

    @GetMapping("/delete")
    public String borrarProveedor(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Producto> optProducto = productRepository.findById(id);

        if (optProducto.isPresent()) {
            Producto producto = optProducto.get();
            producto.setBorrado(0);
            productRepository.save(producto);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Producto no encontrado");
        }

        return "redirect:/product";
    }


    //Lista Productos pendientes
    @GetMapping("/pendientes")
    public String pendientes(Model model) {
        model.addAttribute("listaReposiciones", reposicionesRepository.findByAprobarIsNull());
        return "/admin/productosPendientes";
    }

    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Integer id) {
        // Busca la reposición por ID
        Reposicion reposicion = reposicionesRepository.findById(id).orElse(null);

        if (reposicion != null && reposicion.getAprobar()==null) {
            Optional<Producto> producto = productRepository.findById(reposicion.getProducto().getId());

            if (producto.isPresent()) {
                // Actualiza el stock usando la cantidad de reposición
                stockRepository.actualizarStock(Integer.parseInt(reposicion.getCantidad()), reposicion.getProducto().getId(), reposicion.getZona().getIdzonas());
                // Actualiza el atributo 'aprobado' de la reposición
                reposicion.setAprobar("aprobado");
                reposicionesRepository.save(reposicion); // Guarda los cambios en la base de datos
                return "redirect:/admin/pendientes";
            }
        }
        // Redirige si la reposición no se encuentra o el producto no está presente
        return "redirect:/admin";
    }

    @GetMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Integer id) {
        // Busca la reposición por ID
        Reposicion reposicion = reposicionesRepository.findById(id).orElse(null);

        if (reposicion != null && reposicion.getAprobar()==null) {
            Optional<Producto> producto = productRepository.findById(reposicion.getProducto().getId());
            if (producto.isPresent()) {
                // Actualiza el atributo 'rechazado' de la reposición
                reposicion.setAprobar("rechazado");
                reposicionesRepository.save(reposicion); // Guarda los cambios en la base de datos
                return "redirect:/admin/pendientes";
            }
        }
        // Redirige si la reposición no se encuentra o el producto no está presente
        return "redirect:/admin";
    }

    @GetMapping("/aprobados")
    public String aprobados(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProductos", productRepository.findByEstado("Aprobado"));

        return "/admin/productosAprobados";
    }

    //Lista Productos pendientes
    //Waiting for it...
    /*
    @PostMapping("/guardarProducto")
    public String guardarProducto(RedirectAttributes attr,
                                  Model model,
                                  @ModelAttribute("producto") @Valid Producto producto,
                                  BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            model.addAttribute("listaProductos", productRepository.findAll());
            return "admin2/newFrmP";
        }else{
            if (producto.getId() == 0) {
                List<Producto> productList = productRepository.findByProductname(producto.getProductname());
                boolean existe = false;
                for (Producto p : productList) {
                    if (p.getNombre().equals(producto.getProductname())) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    System.out.println("El producto existe");
                    model.addAttribute("listaProductos", productRepository.findAll());
                    return "product/newFrmP";
                } else {
                    attr.addFlashAttribute("msg", "Producto creado exitosamente");
                    productRepository.save(producto);
                    return "redirect:/admin/productos";
                }
            } else {
                attr.addFlashAttribute("msg", "Producto actualizado exitosamente");
                productRepository.save(producto);
                return "redirect:/admin/productos";
            }
        }
    }
    */

    /*
    @GetMapping("/delete")
    public String borrarProveedor(Model model,
                                      @RequestParam("id") int id,
                                      RedirectAttributes attr) {

        Optional<Proveedor> optProveedor = proveedorRepository.findById(id);

        if (optProduct.isPresent()) {
            productRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        }
        return "redirect:/product";

    }
    */


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
