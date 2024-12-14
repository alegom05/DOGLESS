package com.example.springdogless.controllers;

import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.DTO.ResenaDTO;
import com.example.springdogless.Repository.*;
import com.example.springdogless.entity.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
    @Autowired
    ResenaRepository resenaRepository;
    @Autowired
    PreguntasProductoRepository preguntasProductoRepository;
    @Autowired
    ReclamoRepository reclamoRepository;
    @Autowired
    SolicitudRepository solicitudRepository;

    @Autowired
    LiveMessagesRepository liveMessagesRepository;


    @GetMapping("")
    public String listarOrdenesUsuario(HttpSession session, Model model) {
        // Obtener el usuario de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        // Validar si el usuario está en sesión
        if (usuarioLogueado == null) {
            return "redirect:/login"; // Redirigir al login si no hay sesión
        }

        // Ejecutar la consulta SQL nativa para obtener las órdenes del usuario
        List<Orden> ordenes = ordenRepository.findOrdenesPorId(usuarioLogueado.getId());

        // Pasar la lista de órdenes a la vista
        model.addAttribute("ordenes", ordenes);
        return "usuario/paginaprincipal"; // Retornar la vista con las órdenes
    }
    @GetMapping("/gg")
    public String verperdd(Model model) {
        return "usuario/GGGG"; // Esto renderiza la vista perfil_superadmin.html
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
    @GetMapping("/descargarboleta")
    public void descargarBoleta(@RequestParam("id") Integer id, HttpServletResponse response, RedirectAttributes redirectAttributes) throws DocumentException, IOException {
        Optional<Orden> optionalOrden = ordenRepository.findById(id);

        if (optionalOrden.isPresent()) {
            Orden orden = optionalOrden.get();
            List<Detalleorden> detallesOrden = detallesRepository.findListaDetallesOrdenes(orden.getId());

            // Crear el documento PDF
            Document document = new Document();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=boleta_" + orden.getId() + ".pdf");

            // Flujo de salida
            ServletOutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // Encabezado de la empresa
            Paragraph header = new Paragraph("Dogless", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph("Cuidamos de tus mascotas, cuidamos de ti", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC)));
            document.add(Chunk.NEWLINE);

            // Título de la boleta
            Paragraph title = new Paragraph("Boleta de Orden # " + orden.getId(), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Información de la orden
            document.add(new LineSeparator());
            document.add(new Paragraph("Información de la Orden", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            document.add(new Paragraph("Estado: " + orden.getEstado()));
            document.add(new Paragraph("Fecha: " + orden.getFecha()));
            document.add(new Paragraph("Dirección de Envío: " + orden.getDireccionenvio()));
            document.add(new Paragraph("Método de Pago: " + orden.getMetodopago()));
            document.add(new Paragraph("Total: " + orden.getTotal(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            document.add(Chunk.NEWLINE);

            // Separador
            document.add(new LineSeparator());
            document.add(new Paragraph("Detalles de Productos", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            document.add(Chunk.NEWLINE);

            // Tabla de productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100); // Ancho de la tabla
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Encabezados de la tabla
            PdfPCell headerCell;
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] headers = { "Producto", "Cantidad", "Precio Unitario", "Subtotal" };
            for (String headerTitle : headers) {
                headerCell = new PdfPCell(new Phrase(headerTitle, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(headerCell);
            }

            // Detalles de los productos
            for (Detalleorden detalle : detallesOrden) {
                table.addCell(new PdfPCell(new Phrase(detalle.getProducto().getNombre())));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detalle.getPreciounitario()))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detalle.getSubtotal()))));
            }

            document.add(table);

            // Separador final
            document.add(new LineSeparator());
            document.add(new Paragraph("Gracias por confiar en Dogless", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC)));
            document.add(Chunk.NEWLINE);

            document.close();
        } else {
            redirectAttributes.addFlashAttribute("error", "Orden no encontrada.");
        }

    }



    @GetMapping({"/guia"})
    public String GuiaDeUsuario(Model model) {
        return "usuario/guia";
    }

    @GetMapping("/chat")
    public String getChatPage(HttpSession session, Model model) {
        // Obtener el usuario logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogueado == null) {
            // Si el usuario no está logueado, redirigir al login
            return "redirect:/login";
        }

        // Crear el nombre de la sala (room_{idusuario})
        String room = "room_" + usuarioLogueado.getId();

        // Verificar si ya existen mensajes en la sala
        List<LiveMessages> listaMensajesSala = liveMessagesRepository.findBySalaOrderByFechaenvioAsc(room);

        // Si no hay mensajes, puedes decidir si crear un mensaje inicial o simplemente mostrar un mensaje indicativo
        if (listaMensajesSala.isEmpty()) {
            // Puedes agregar un mensaje predeterminado si lo deseas
            LiveMessages mensajeInicial = new LiveMessages();
            mensajeInicial.setIdusuarios(usuarioLogueado);
            mensajeInicial.setContenido("¡Bienvenido al chat!");
            mensajeInicial.setSala(room);
            mensajeInicial.setFechaenvio(LocalDateTime.now());
            liveMessagesRepository.save(mensajeInicial);
            listaMensajesSala.add(mensajeInicial); // Agregar el mensaje a la lista para mostrarlo
        }

        // Pasar los datos al modelo
        model.addAttribute("room", room);
        model.addAttribute("listaMensajesSala", listaMensajesSala);
        model.addAttribute("idAgenteAsignado", 11); // Reemplazar con el ID del agente asignado
        model.addAttribute("noMessages", listaMensajesSala.isEmpty());

        // Retornar la vista del chat
        return "usuario/chat";
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
        return "usuario/libro";
    }

    @PostMapping("/guardarReclamo")
    public String guardarReclamo(@RequestParam("idusuario") Integer idusuario,
                                 @RequestParam("descripcion") String descripcion,
                                 RedirectAttributes attr) {
        // Intentar obtener el usuario por ID
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(idusuario);

        if (optionalUsuario.isPresent()) {
            // Si el usuario existe, crear y guardar el reclamo
            Reclamo reclamo = new Reclamo();
            Usuario usuario = optionalUsuario.get();
            reclamo.setUsuario(usuario);
            reclamo.setDescripcion(descripcion);

            reclamoRepository.save(reclamo);

            // Agregar mensaje de éxito
            attr.addFlashAttribute("msg", "Reclamo creado exitosamente");
        } else {
            // Manejar el caso de usuario no encontrado
            attr.addFlashAttribute("error", "Usuario no encontrado. No se pudo crear el reclamo.");
        }

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




    @GetMapping(value = "/detalles_producto/{idProducto}")
    public String DetallesProducto(HttpSession session, Model model, @PathVariable("idProducto") Integer idProducto) {
        Integer idzona = (Integer) session.getAttribute("idzona");
        ProductoDTO productoDTO = productRepository.findProductoByIdByZonaCompleto(idProducto,idzona);
        List<ResenaDTO> resenas = resenaRepository.findResenasByProductoId(idProducto,1);
        List<ResenaDTO> preguntasFrec = resenaRepository.findResenasByProductoId(idProducto,2);
        model.addAttribute("producto", productoDTO);
        model.addAttribute("resenas", resenas);
        model.addAttribute("preguntasFrec", preguntasFrec);

        // Obtener la cantidad de productos por categoría
        Map<String, Integer> conteoPorCategoria = new HashMap<>();

        // Obtener todas las categorías disponibles
        Set<String> todasLasCategorias = new HashSet<>(productRepository.findAllCategorias(idzona));
        model.addAttribute("categorias", todasLasCategorias); // Mantiene todas las categorías
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

        return "usuario/detalles_producto";
    }

    @PostMapping("/enviarPregunta")
    public String enviarPregunta(@RequestParam("pregunta") String pregunta,
                                 @RequestParam("idProducto") Integer idProducto,
                                 RedirectAttributes redirectAttributes, HttpSession session) {

        // Obtener solo la fecha actual
        Date fecha = new Date(System.currentTimeMillis());
        Optional<Producto> optionalProducto = productRepository.findById(idProducto);
        Usuario usuarioSession = (Usuario) session.getAttribute("usuario");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioSession.getId());

        // Verificar si el producto existe
        if (!optionalProducto.isPresent()) {
            redirectAttributes.addFlashAttribute("mensaje", "El producto no fue encontrado.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "danger"); // Tipo de alerta
            return "redirect:/usuario/detalles_producto/" + idProducto; // Redirigir a la misma página o a una página de error
        }
        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("mensaje", "El usuario no fue encontrado.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "danger"); // Tipo de alerta
            return "redirect:/usuario/detalles_producto/" + idProducto; // Redirigir a la misma página o a una página de error
        }

        Long count = resenaRepository.countPreguntasByUsuarioAndProducto(optionalUsuario.get().getId(), idProducto);
        boolean preguntaExistente = count > 0;

        // Verificar si el usuario ya hizo una pregunta
        if (preguntaExistente) {
            redirectAttributes.addFlashAttribute("mensaje", "Ya has enviado una pregunta para este producto.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "warning");
            return "redirect:/usuario/detalles_producto/" + idProducto;
        }

        // Verificar la longitud de la pregunta
        if (pregunta.length() > 300) {
            redirectAttributes.addFlashAttribute("mensaje", "Tu pregunta no puede exceder los 300 caracteres.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "danger");
            return "redirect:/usuario/detalles_producto/" + idProducto;
        }

        // Obtener el producto del Optional
        Producto producto = optionalProducto.get();
        Usuario usuario = optionalUsuario.get();

        try {
            // Lógica para guardar la pregunta en la base de datos
            Resena preguntaFrec = new Resena();
            preguntaFrec.setUsuario(usuario);
            preguntaFrec.setComentario(pregunta);
            preguntaFrec.setFecha(fecha);
            preguntaFrec.setProducto(producto);
            preguntaFrec.setTipo(2);

            // Llama al repositorio o servicio para guardar la pregunta
            resenaRepository.save(preguntaFrec);

            // Agregar un mensaje al redirigir, si se guarda correctamente
            redirectAttributes.addFlashAttribute("mensaje", "Tu pregunta ha sido enviada correctamente.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "success"); // Tipo de alerta
        } catch (Exception e) {
            // Manejar el error y no guardar nada
            redirectAttributes.addFlashAttribute("mensaje", "Hubo un error al enviar tu pregunta. Inténtalo de nuevo.");
            redirectAttributes.addFlashAttribute("tipoAlerta", "danger"); // Tipo de alerta
        }

        // Redirigir a la misma página del producto
        return "redirect:/usuario/detalles_producto/" + idProducto;
    }


    @PostMapping("/agregarProducto")
    public String agregarProducto(HttpSession session, Model model,@RequestParam("idProducto") Integer idProducto, @RequestParam("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        // Obtener el usuario por su ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si existe una orden con estado 'creado' para el usuario
        Orden orden = ordenRepository.findOrdenEstadoCreado(idUsuario);

        // Si no existe una orden con estado 'creado', crear una nueva
        if (orden == null) {
            orden = new Orden();
            orden.setEstado("Creado");
            orden.setUsuario(usuario);
            orden.setTotal(BigDecimal.ZERO);
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql);
            ordenRepository.save(orden);
        }

        // Verificar si el producto ya está en los detalles de la orden
        Optional<Detalleorden> detallesExistente = detallesRepository.findByIdordenAndIdproducto(orden.getId(), idProducto);

        if (detallesExistente.isPresent()) {
            // Si ya existe el producto en la orden, actualizar la cantidad
            Detalleorden detalles = detallesExistente.get();
            detalles.setCantidad(detalles.getCantidad() + 1);
            detalles.setSubtotal(detalles.getPreciounitario().multiply(new BigDecimal(detalles.getCantidad())));
            detallesRepository.save(detalles);
        } else {
            // Si no existe, agregar un nuevo detalle con el producto
            Producto producto = productRepository.findById(idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            Detalleorden nuevoDetalle = new Detalleorden();
            nuevoDetalle.setOrden(orden);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(1);
            nuevoDetalle.setPreciounitario(BigDecimal.valueOf(producto.getPrecio()));
            nuevoDetalle.setSubtotal(BigDecimal.valueOf(producto.getPrecio()));
            detallesRepository.save(nuevoDetalle);
        }

        redirectAttributes.addFlashAttribute("mensaje", "Producto añadido al carrito");
        return "redirect:/usuario/compras?id=" + idUsuario;
    }
    @PostMapping("/agregarProducto2")
    public String agregarProducto2(HttpSession session, Model model,@RequestParam("idProducto") Integer idProducto, @RequestParam("idUsuario") Integer idUsuario,@RequestParam("cantidad") Integer cantidad, RedirectAttributes redirectAttributes) {
        // Obtener el usuario por su ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si existe una orden con estado 'creado' para el usuario
        Orden orden = ordenRepository.findOrdenEstadoCreado(idUsuario);

        // Si no existe una orden con estado 'creado', crear una nueva
        if (orden == null) {
            orden = new Orden();
            orden.setEstado("Creado");
            orden.setUsuario(usuario);
            orden.setTotal(BigDecimal.ZERO);
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql);
            ordenRepository.save(orden);
        }

        // Verificar si el producto ya está en los detalles de la orden
        Optional<Detalleorden> detallesExistente = detallesRepository.findByIdordenAndIdproducto(orden.getId(), idProducto);

        if (detallesExistente.isPresent()) {
            // Si ya existe el producto en la orden, actualizar la cantidad
            Detalleorden detalles = detallesExistente.get();
            detalles.setCantidad(detalles.getCantidad() + cantidad);
            detalles.setSubtotal(detalles.getPreciounitario().multiply(new BigDecimal(detalles.getCantidad())));
            detallesRepository.save(detalles);
        } else {
            // Si no existe, agregar un nuevo detalle con el producto
            Producto producto = productRepository.findById(idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            Detalleorden nuevoDetalle = new Detalleorden();
            nuevoDetalle.setOrden(orden);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPreciounitario(BigDecimal.valueOf(producto.getPrecio()));
            nuevoDetalle.setSubtotal(BigDecimal.valueOf(producto.getPrecio()));
            detallesRepository.save(nuevoDetalle);
        }

        redirectAttributes.addFlashAttribute("mensaje", "Producto añadido al carrito");
        return "redirect:/usuario/compras?id=" + idUsuario;
    }

    public String agregarProductoChatbot(Integer idProducto, Integer idUsuario, Integer cantidad) {
        // Obtener el usuario por su ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si existe una orden con estado 'Creado' para el usuario
        Orden orden = ordenRepository.findOrdenEstadoCreado(idUsuario);

        // Si no existe una orden con estado 'Creado', crear una nueva
        if (orden == null) {
            orden = new Orden();
            orden.setEstado("Creado");
            orden.setUsuario(usuario);
            orden.setTotal(BigDecimal.ZERO);
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql);
            ordenRepository.save(orden);
        }

        // Verificar si el producto ya está en los detalles de la orden
        Optional<Detalleorden> detallesExistente = detallesRepository.findByIdordenAndIdproducto(orden.getId(), idProducto);

        if (detallesExistente.isPresent()) {
            // Si ya existe el producto en la orden, actualizar la cantidad
            Detalleorden detalles = detallesExistente.get();
            detalles.setCantidad(detalles.getCantidad() + cantidad);
            detalles.setSubtotal(detalles.getPreciounitario().multiply(new BigDecimal(detalles.getCantidad())));
            detallesRepository.save(detalles);
        } else {
            // Si no existe, agregar un nuevo detalle con el producto
            Producto producto = productRepository.findById(idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            Detalleorden nuevoDetalle = new Detalleorden();
            nuevoDetalle.setOrden(orden);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPreciounitario(BigDecimal.valueOf(producto.getPrecio()));
            nuevoDetalle.setSubtotal(BigDecimal.valueOf(producto.getPrecio()).multiply(new BigDecimal(cantidad)));
            detallesRepository.save(nuevoDetalle);
        }

        // Retornar un mensaje de éxito o cualquier otra información relevante
        return "Producto añadido al carrito correctamente";
    }


    //borrar producto
    @GetMapping("/borrarproducto")
    public String eliminarDetallesOrden(HttpSession session, @RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        // Obtener el usuario logueado de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        System.out.println("ID recibido para eliminar: " + id);
        Optional<Detalleorden> detalle = detallesRepository.findByIdDetalle(id);

        if (detalle.isPresent()) {
            // Eliminar el detalle
            detallesRepository.delete(detalle.get()); // Usa .get() para obtener el objeto
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado con éxito."); // Mensaje de éxito
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el producto a eliminar."); // Mensaje de error
        }

        Integer se = usuarioLogueado.getId();
        System.out.println("ID del usuario: " + se);
        return "redirect:/usuario/compras?id="+se; // Redirigir a la página de compras
    }



    /*
    @GetMapping(value = "compras")
    public String listaCompras(Model model, @RequestParam("id") Integer id) {
        List<Object[]> orden = detallesRepository.findOrderCreada(id);
        // Pasar la lista de órdenes a la vista
        model.addAttribute("orden", orden);
        return "usuario/carrito_compras";
    }*/
    @GetMapping( "/compras")
    public String listaProductos(Model model, @RequestParam("id") Integer id) {
        model.addAttribute("listadetalles", detallesRepository.findByOrdenCreado(id));
        return "usuario/carrito_compras";
    }
    // Guardar la compra con las nuevas cantidades
    @PostMapping("/actualizarCantidad")
    public String actualizarCantidad(@RequestParam("id") Integer id,
                                     @RequestParam("idDetallesOrden") List<Integer> idDetallesOrden,
                                     @RequestParam("idOrdenes") List<Integer> idOrdenes,
                                     @RequestParam("cantidades") List<Integer> cantidades,
                                     @RequestParam("subtotales") List<BigDecimal> subtotales,
                                     RedirectAttributes redirectAttributes) {
        // Verifica si el id es 0 o si no hay detalles de orden
        if (id == 0 || idDetallesOrden.isEmpty() || cantidades.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "No ha hecho compras.");
            return "redirect:/usuario/";
        }

        BigDecimal total = BigDecimal.valueOf(0);

        // Itera sobre las listas para actualizar cada cantidad en la tabla detallesorden
        for (int i = 0; i < idDetallesOrden.size(); i++) {
            Integer idDetalle = idDetallesOrden.get(i);
            Integer nuevaCantidad = cantidades.get(i);
            BigDecimal subtotal = subtotales.get(i);

            // Busca el detalle de la orden por idDetalle
            Detalleorden detalle = detallesRepository.findById(idDetalle)
                    .orElse(null); // Cambiamos a null para verificar más adelante

            // Verifica si se encontró el detalle
            if (detalle == null) {
                redirectAttributes.addFlashAttribute("mensaje", "Detalle no encontrado para ID: " + idDetalle);
                return "redirect:/usuario/";
            }

            // Actualiza la cantidad y el subtotal
            detalle.setCantidad(nuevaCantidad);
            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);

            // Guarda los cambios
            detallesRepository.save(detalle);
        }

        // Recalcular el subtotal (cantidad * precio unitario)
        Optional<Orden> optOrden = ordenRepository.findById(idOrdenes.get(0));
        if (optOrden.isPresent()) {
            Orden orden = optOrden.get();
            orden.setTotal(total);
            ordenRepository.save(orden);
        }

        return "redirect:/usuario/checkout?id=" + id;
    }


    @GetMapping(value = "checkout")
    public String checkout(Model model, @RequestParam("id") Integer id) {
        // Obtiene la lista de productos
        List<Detalleorden> listaProductos = detallesRepository.findByOrdenCreado(id);

        // Verifica si la lista está vacía
        if (listaProductos.isEmpty()) {
            // Redirige a la vista de compras si no hay productos
            return "redirect:/usuario/";
        }

        // Agrega la lista de productos al modelo
        model.addAttribute("listaProductos", listaProductos);
        return "usuario/checkout";
    }
    //logica para actualizar la direccion
    @PostMapping("/actualizarDireccion")
    public String actualizarDireccion(Model model, @RequestParam("id") Integer id,
                                      @RequestParam("direccion") String direccion){
        Optional<Orden> optionalOrden= ordenRepository.findOrdenCreado(id);

        if (optionalOrden.isPresent()) {
            Orden orden = optionalOrden.get();
            orden.setDireccionenvio(direccion);
            ordenRepository.save(orden);
        }
        return "redirect:/usuario/procesopago?id=" + id;
    }
    @GetMapping(value = "procesopago")
    public String procesoPago(Model model,@RequestParam("id") Integer id) {
        List<Detalleorden> detallesOrden = detallesRepository.findByOrdenCreado(id);
        // Verifica si la lista está vacía
        if (detallesOrden.isEmpty()) {
            // Redirige a la vista de compras si no hay productos
            return "redirect:/usuario/";
        }
        // Agrega la lista de productos al modelo
        model.addAttribute("detallesOrden", detallesOrden);
        return "usuario/procesoDePago";
    }
    @PostMapping("/confirmarPedido")
    public String actualizarPedido(Model model, @RequestParam("id") Integer id) {
        Optional<Orden> optionalOrden = ordenRepository.findOrdenCreado(id);

        BigDecimal costoenvio = BigDecimal.valueOf(12);

        if (optionalOrden.isPresent()) {
            Orden orden = optionalOrden.get();
            BigDecimal total = orden.getTotal();
            String estadoActual = orden.getEstado();

            // Actualiza el estado solo si está en "Creado"
            if (estadoActual.equals("Creado")) {
                orden.setEstado("En Validación");
            }

            // Calcula el total sumando el costo de envío
            BigDecimal nuevoTotal = total.add(costoenvio);
            orden.setTotal(nuevoTotal);

            // Actualiza la fecha actual
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql);

            // Guarda los cambios
            ordenRepository.save(orden);
        }

        return "redirect:/usuario/pagoexitoso?id=" + id;
    }
    @GetMapping(value = "pagoexitoso")
    public String pagoExitoso(Model model,@RequestParam("id") Integer id) {
        model.addAttribute("listaProductos",detallesRepository.findByOrdenValidada(id));
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

/*
    @PostMapping("/preguntasguardar")
    public String guardarPregunta(
            @RequestParam("comentario") String comentario,
            @RequestParam("idproducto") Integer idproducto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Obtener el usuario de la sesión
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        // Validar si el usuario existe en la sesión
        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para enviar una pregunta.");
            return "redirect:/login"; // Redirige a la página de inicio de sesión si no hay usuario en sesión
        }

        // Buscar el producto en la base de datos
        Optional<Producto> optionalProducto = productRepository.findById(idproducto);

        // Manejar el caso donde no se encuentre el producto
        if (!optionalProducto.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/detallesProducto/" + idproducto; // Redirigir a una página de error si el producto no existe
        }

        Producto producto = optionalProducto.get(); // Obtener el producto

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(idUsuario);
        if (!optionalUsuario.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login"; // Redirigir a una página de error si el producto no existe
        }

        Usuario usuario = optionalUsuario.get();

        // Crear la entidad Preguntasproducto
        Preguntasproducto pregunta = new Preguntasproducto();
        pregunta.setComentario(comentario);
        pregunta.setFecha(LocalDate.now()); // Asignar la fecha actual (sin hora)
        pregunta.setProducto(producto);
        pregunta.setUsuario(usuario);

        // Guardar en la base de datos
        preguntasProductoRepository.save(pregunta);
        // Añadir un mensaje de éxito al modelo
        redirectAttributes.addFlashAttribute("success", "Tu pregunta ha sido enviada con éxito.");

        // Redirigir a la página de detalles del producto
        return "redirect:/detallesProducto/" + idproducto; // Asegúrate de que esta URL coincida con la configuración de tu controlador
    }
*/



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
    @GetMapping("/formularioPostulacion")
    public String mostrarFormularioPostulacion(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogueado == null) {
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }
        if (usuarioLogueado.getCodigoaduana() != null) {
            return "usuario/formularioCompleto";
        }
        // Agregar datos al modelo si necesitas
        model.addAttribute("usuario", usuarioLogueado);

        return "usuario/formularioPostulacion"; // Nombre de la vista HTML
    }

    @PostMapping("/postular")
    public String postularseAgente(HttpSession session, RedirectAttributes redirectAttributes) {
        // Obtener usuario de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogueado == null) {
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }

        // Verificar si el usuario ya tiene un código de aduana asignado
        if (usuarioLogueado.getCodigoaduana() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya ha realizado su postulación anteriormente.");
            return "redirect:/usuario"; // Redirigir a la página principal
        }

        // Asignar el valor de "codigoaduana" en formato ADU{id}
        String codigoAduana = "ADU" + String.format("%03d", usuarioLogueado.getId());
        usuarioLogueado.setCodigoaduana(codigoAduana);

        // Guardar el usuario con el código de aduana actualizado
        usuarioRepository.save(usuarioLogueado);
        session.setAttribute("usuario", usuarioLogueado);

        Solicitud nuevaSolicitud = new Solicitud();
        nuevaSolicitud.setUsuario(usuarioLogueado);
        nuevaSolicitud.setVeredicto(null); // El veredicto será NULL hasta que sea evaluado
        nuevaSolicitud.setComentario(null); // El comentario será opcional
        solicitudRepository.save(nuevaSolicitud); // Guardar la solicitud

        // Agregar mensaje de éxito
        redirectAttributes.addFlashAttribute("msg", "Su postulación ha sido enviada exitosamente");
        return "redirect:/usuario";
    }



    @GetMapping("/informacion-de-contacto")
    public String informaciondecontacto() {
        return "informacion-de-contacto"; // Esto renderiza la vista informacion-de-contacto.html
    }


    @GetMapping("/politica-de-privacidad")
    public String politicadeprivacidad() {
        return "politica-de-privacidad"; // Esto renderiza la vista politica-de-privacidad.html
    }

    @GetMapping("/terminosycondiciones")
    public String terminosycondiciones() {
        return "terminosycondiciones"; // Esto renderiza la vista politica-de-privacidad.html
    }
}
