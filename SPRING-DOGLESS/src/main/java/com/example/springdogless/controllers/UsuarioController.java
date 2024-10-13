package com.example.springdogless.controllers;

import com.example.springdogless.DTO.OrdenDetalleDTO;
import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.Repository.*;
import com.example.springdogless.entity.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

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
    OrdenRepository ordenRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    ImportacionRepository importacionRepository;
    @Autowired
    Detallesorden2 detallesRepository;
    @Autowired
    private JavaMailSenderImpl mailSender;

    @GetMapping("")
    public String listarOrdenesUsuario(HttpSession session, Model model) {
        // Obtener el usuario de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        // Validar si el usuario está en sesión
        if (usuarioLogueado == null) {
            return "redirect:/login"; // Redirigir al login si no hay sesión
        }

        // Ejecutar la consulta SQL nativa para obtener las órdenes del usuario
        List<Object[]> ordenes = detallesRepository.findOrdersByUserId(usuarioLogueado.getId());

        // Pasar la lista de órdenes a la vista
        model.addAttribute("ordenes", ordenes);
        return "usuario/paginaprincipal"; // Retornar la vista con las órdenes
    }
    @PostMapping("/editorden")
    public String editarOrden(@RequestParam("id") Integer id, Model model) {
        // Aquí va la lógica de edición de la orden
        // Por ejemplo, cargar la orden actual en un formulario
        // Implementar si tienes una vista específica para editar órdenes
        return "usuario/editarOrden";
    }

    /*@PostMapping("/eliminarorden")
    public String eliminarOrden(@RequestParam("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        // Validar si el usuario está en sesión
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Obtener la orden por ID
        Detalleorden orden = detallesRepository.findById(id).orElse(null);

        // Verificar si la orden existe y si el estado permite eliminarla
        if (orden == null || !orden.getOrden().getUsuario().getId().equals(usuarioLogueado.getId())) {
            redirectAttributes.addFlashAttribute("error", "Orden no encontrada o no tiene permiso para eliminarla.");
            return "redirect:/usuario";
        }

        if (orden.getOrden().getEstado().equals("En Proceso")) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar una orden que está en proceso.");
            return "redirect:/usuario";
        }

        // Eliminar la orden
        detallesRepository.deleteById(id);

        // Simulación del reembolso mediante correo
        try {
            enviarCorreoReembolso(usuarioLogueado.getEmail(), orden.getOrden().getTotal());
        } catch (MessagingException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "No se pudo enviar el correo de reembolso.");
            return "redirect:/usuario";
        }

        // Mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "Orden eliminada y reembolso realizado.");
        return "redirect:/usuario";
    }

     */
    @PostMapping("/eliminarorden")
    public String borrarOrden(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Detalleorden> optDetalleorden = detallesRepository.findById(id);
        if (optDetalleorden.isPresent()) {
            detallesRepository.deleteById(id);
        }
        return "redirect:/usuario";
    }


    private void enviarCorreoReembolso(String correo, BigDecimal total) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(correo);
        helper.setSubject("Reembolso realizado");
        helper.setText("Estimado cliente,\n\nSu reembolso de S/. " + total + " ha sido realizado exitosamente.\nGracias por su preferencia.\n\nSaludos cordiales.");

        mailSender.send(message);
    }
    @PostMapping("/descargarboleta")
    public String descargarBoleta(@RequestParam("id") Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Detalleorden orden = detallesRepository.findById(id).orElse(null);

        if (orden == null || !orden.getOrden().getUsuario().getId().equals(usuarioLogueado.getId())) {
            redirectAttributes.addFlashAttribute("error", "Orden no encontrada o no tiene permiso para descargarla.");
            return "redirect:/usuario";
        }

        // Lógica para generar y descargar la boleta (PDF o cualquier formato deseado)
        // Ejemplo: retorno de archivo PDF

        return "redirect:/usuario";  // O donde quieras redirigir después de la descarga
    }



    @GetMapping({"/guia"})
    public String GuiaDeUsuario(Model model) {
        return "usuario/guia";
    }

    @GetMapping("/new")
    public String nuevoAgenteFrm(Model model) {
        model.addAttribute("listaZonas", zonaRepository.findAll());
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        return "usuario/agregar_agente";
    }

    //Libro de reclamaciones
    @GetMapping("/libro")
    public String nuevaReclamacion(Model model, @ModelAttribute("usuario") Usuario usuario, @ModelAttribute("reclamo") Reclamo reclamo) {
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        model.addAttribute("listaDistritos", distritoRepository.findAll());

        return "usuario/libro";
    }

    @PostMapping("/guardarReclamo")
    public String guardarReclamo(@RequestParam("idusuario") Integer idusuario, Usuario usuario, Reclamo reclamo, RedirectAttributes attr) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(idusuario);

        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            reclamo.setDescripcion(reclamo.getDescripcion());
            //usuario.setProveedor(usuario);  // Asignar el proveedor al producto
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/libro";

        }
        usuarioRepository.save(usuario);

        attr.addFlashAttribute("msg", "Reclamo creado exitosamente");
        return "redirect:/usuario/libro";
    }

    /*
    @GetMapping("/tienda")
    public String TiendaProductos(HttpSession session, Model model) {
        // Obtener idzona desde la sesión
        Integer idzona = (Integer) session.getAttribute("idzona");
        if (idzona != null) {
            // Consultar productos por zona
            List<ProductoDTO> productos = productRepository.findProductosByZona(idzona);

            // Imprimir los valores en la consola del servidor
            if (productos != null && !productos.isEmpty()) {
                System.out.println("Productos encontrados: " + productos.size());
                for (ProductoDTO producto : productos) {
                    System.out.println("ID: " + producto.getIdproductos() + ", Nombre: " + producto.getNombre() +
                            ", Precio: " + producto.getPrecio());
                }
            } else {
                System.out.println("No se encontraron productos para la zona: " + idzona);
            }


            model.addAttribute("listaProductos", productos);
        } else {
            model.addAttribute("error", "No se pudo obtener la zona del usuario.");
        }
        return "usuario/tienda";
    }
    */






    @GetMapping("/tienda")
    public String TiendaProductos(HttpSession session, Model model,
                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(required = false) String categoria,
                                  @RequestParam(required = false) String filter,
                                  @RequestParam(required = false) List<String> priceRanges) {
        Integer idzona = (Integer) session.getAttribute("idzona");
        double minPrice = 0;
        double maxPrice = 0;

        if (priceRanges != null) {
            for (String range : priceRanges) {
                String[] prices = range.split("-");
                minPrice = Double.parseDouble(prices[0]);
                maxPrice = Double.parseDouble(prices[1]);
            }
        }

        if (idzona != null) {
            // CAMBIAR
            Page<ProductoDTO> productosPaginados = null;
            List<ProductoDTO> productos;
            int pageSize = 6;
            page=page-1;
            PageRequest pageRequest = PageRequest.of(page, pageSize);

            if (categoria != null) {
                if (filter != null) {
                    switch (filter) {
                        case "ranked":
                            // Obtener productos más rankeados
                            productosPaginados = productRepository.findProductosByZonaAndCategoriaOrderBySatisfaccionDesc(idzona, categoria, pageRequest);
                            break;
                        case "price_desc":
                            // Lógica para ordenar por precio de mayor a menor
                            productosPaginados = productRepository.findProductosByZonaAndCategoriaOrderByPrecioDescByCategoria(idzona, categoria, pageRequest);
                            break;
                        case "price_asc":
                            // Lógica para ordenar por precio de menor a mayor
                            productosPaginados = productRepository.findProductosByZonaAndCategoriaOrderByPrecioAscByCategoria(idzona, categoria, pageRequest);
                            break;
                        default:
                            // Filtrar productos por categoría con paginación si pasa un filtro que no es
                            productosPaginados = productRepository.findProductosByZonaAndCategoriaAndPrecio(idzona, categoria, null, null,pageRequest);
                            break;
                    }
                } else{
                    if (priceRanges != null) {
                        productosPaginados = productRepository.findProductosByZonaAndCategoriaAndPrecio(idzona, categoria, minPrice, maxPrice, pageRequest);
                    }else{
                        // Filtrar productos por categoría con paginación
                        productosPaginados = productRepository.findProductosByZonaAndCategoriaAndPrecio(idzona, categoria, null, null,pageRequest);
                    }
                }
            } else {
                if (filter != null) {
                    switch (filter) {
                        case "ranked":
                            // Obtener productos más rankeados
                            productosPaginados = productRepository.findProductosByZonaOrderBySatisfaccionDesc(idzona, pageRequest);
                            break;
                        case "price_desc":
                            // Lógica para ordenar por precio de mayor a menor
                            productosPaginados = productRepository.findProductosByZonaOrderByPrecioDesc(idzona, pageRequest);
                            break;
                        case "price_asc":
                            // Lógica para ordenar por precio de menor a mayor
                            productosPaginados = productRepository.findProductosByZonaOrderByPrecioAsc(idzona, pageRequest);
                            break;
                        default:
                            // Productos de la zona con paginación si pasa un filtro que no es
                            productosPaginados = productRepository.findProductosByZonaAndPrecio(idzona,null,null, pageRequest);
                            break;
                    }
                } else{
                    if (priceRanges != null) {
                        // Productos de la zona con paginación
                        productosPaginados = productRepository.findProductosByZonaAndPrecio(idzona, minPrice, maxPrice, pageRequest);
                    }else{
                        // Filtrar productos por categoría con paginación
                        productosPaginados = productRepository.findProductosByZonaAndPrecio(idzona,null, null,pageRequest);
                    }
                }

            }

            productos = productosPaginados.getContent();



            // Obtener las categorías únicas de los productos
            //Set<String> categorias = productos.stream()
            //        .map(ProductoDTO::getCategoria)
            //        .collect(Collectors.toSet());
            //model.addAttribute("categorias", categorias);



            // Obtener todas las categorías disponibles
            Set<String> todasLasCategorias = new HashSet<>(productRepository.findAllCategorias(idzona));

            model.addAttribute("categorias", todasLasCategorias); // Mantiene todas las categorías
            model.addAttribute("listaProductos", productos);
            model.addAttribute("totalPages", productosPaginados.getTotalPages());
            model.addAttribute("currentPage", page);

            // Obtener la cantidad de productos por categoría
            Map<String, Integer> conteoPorCategoria = new HashMap<>();

            // Crea por asi decirlo una matriz q se colocara como, por ejemplo, Tecnologia, 4 (indicando la categoria
            // como string y la cantidad como int)
            for (String categoriaz : todasLasCategorias) {
                Integer count = productRepository.countProductosByZonaAndCategoria(idzona, categoriaz);
                conteoPorCategoria.put(categoriaz, count);
            }
            model.addAttribute("conteoPorCategoria", conteoPorCategoria);

            // Obtener el total de productos sin filtrar
            Integer totalProductos = productRepository.countProductosByZona(idzona);
            model.addAttribute("totalProductos", totalProductos); // Agrega el total de productos al modelo



            List<ProductoDTO> tres_productos_rankeados = productRepository.findTop3ProductosByZona(idzona);
            model.addAttribute("TresProductosRankeados", tres_productos_rankeados);








        } else {
            model.addAttribute("error", "No se pudo obtener la zona del usuario.");
        }

        return "usuario/tienda";
    }












    @GetMapping(value = "compras")
    public String listaCompras(Model model, @RequestParam("id") Integer id) {
        List<Object[]> orden = detallesRepository.findOrderCreada(id);
        // Pasar la lista de órdenes a la vista
        model.addAttribute("orden", orden);
        return "usuario/carrito_compras";
    }
    // Guardar la compra con las nuevas cantidades

    @GetMapping(value = "checkout")
    public String checkout(Model model, @RequestParam("id") Integer id) {
        model.addAttribute("listaProductos",productRepository.findProductosByUsuarioId(id));
        return "usuario/checkout";
    }
    @GetMapping(value = "procesopago")
    public String procesoPago(Model model,@RequestParam("id") Integer id) {
        model.addAttribute("listaProductos",productRepository.findProductosByUsuarioId(id));
        return "usuario/procesoDePago";
    }
    @GetMapping(value = "pagoexitoso")
    public String pagoExitoso(Model model,@RequestParam("id") Integer id) {
        model.addAttribute("listaProductos",productRepository.findProductosByUsuarioId(id));
        return "usuario/pagoExitoso";
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "usuario/detallesperfil";
        } else {
            return "redirect:/usuario";
        }
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
