package com.example.springdogless.controllers;

import com.example.springdogless.DTO.OrdenEstadoDTO;
import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.Repository.*;

import com.example.springdogless.entity.*;
import com.example.springdogless.services.OrdenService;
import com.example.springdogless.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping({"agente", "agente/"})
public class AgenteController {

    /*@GetMapping("/")
    @ResponseBody
    public String unaPersona() {
        return "olapaola5";
    }*/

    // Mapea la vista del login

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ZonalRepository zonaRepository;
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
    ReposicionRepository reposicionesRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrdenRepository ordenRepository;
    @Autowired
    DetallesordenRepository detallesordenRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    ReporteRepository reporteRepository;
    @Autowired
    LiveMessagesRepository liveMessagesRepository;

    @GetMapping({""})
    public String PaginaPrincipal(Model model) {

        return "agente/paginaprincipal";
    }
    @GetMapping("/cambiarcontraseña")
    public String vercontra(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario); // Pasar el usuario a la vista
        }
        return "agente/cambiarcontra";
    }

    @GetMapping("/perfil_agente")
    public String verperfilzonal(Model model, HttpSession session) {
        // Obtén el objeto Agente de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        // Verifica que el objeto no sea nulo
        if (usuarioLogueado != null) {
            model.addAttribute("agente", usuarioLogueado);
        } else {
            // Redirige o muestra un mensaje de error si el usuario no está en la sesión
            return "redirect:/login"; // Redirige a la página de login si es necesario
        }
        return "agente/perfil_agente"; // Esto renderiza la vista perfil_agente.html
    }
    @GetMapping("/ActualizarPerfilAgente")
    public String ActualizardatosDeAgente(Model model) {
        return "agente/modificarperfil"; // Esto renderiza la vista modificarperfil_agente.html
    }

    @GetMapping({"/dashboard"})
    public String ElDashboard007(Model model) {
        List<OrdenEstadoDTO> ordenCategorias = ordenRepository.contarOrdenesPorProceso();
        List<OrdenEstadoDTO> cantidadPorEstado = ordenRepository.contarOrdenesPorEstado();
        List<OrdenEstadoDTO> ordenesProcesadasYCancelada = ordenRepository.contarOrdenesProcesadasYCanceladasPorMes();

        int enProceso = 0;
        int sinProcesar = 0;

        // Contar las cantidades
        for (OrdenEstadoDTO orden : ordenCategorias) {
            if ("Procesando".equals(orden.getcategoria())) {
                enProceso += orden.getcantidad();
            } else if ("No procesadas".equals(orden.getcategoria())) {
                sinProcesar += orden.getcantidad();
            }
        }

        // Inicializar las variables para cada estado
        int Creado = 0;
        int enValidacion = 0;
        int enRuta = 0;
        int enAduanas = 0;
        int ArriboAlPais = 0;
        int Recibido = 0;
        int Cancelado = 0;
        int enProcesoEstado = 0;

        // Llenar las listas desde cantidadPorEstado
        for (OrdenEstadoDTO estadistica : cantidadPorEstado) {
            String estado_cache = estadistica.getestado();

            // Usar un switch para asignar cantidades
            switch (estado_cache) {
                case "En Proceso":
                    enProcesoEstado += estadistica.getcantidad();
                    break; // Termina el caso actual
                case "Creado":
                    Creado += estadistica.getcantidad();
                    break;
                case "En Validación":
                    enValidacion += estadistica.getcantidad();
                    break;
                case "En Ruta":
                    enRuta += estadistica.getcantidad();
                    break;
                case "En Aduanas":
                    enAduanas += estadistica.getcantidad();
                    break;
                case "Arribo al País":
                    ArriboAlPais += estadistica.getcantidad();
                    break;
                case "Recibido":
                    Recibido += estadistica.getcantidad();
                    break;
                case "Cancelado":
                    Cancelado += estadistica.getcantidad();
                    break;
                default:
                    // Manejar casos no esperados si es necesario
                    break;
            }
        }

        List<ProductoDTO> productosMasVendidos = productRepository.contarTotalVendidosPorProducto();

        model.addAttribute("productosMasVendidos", productosMasVendidos);
        model.addAttribute("ordenesProcesadasYCancelada", ordenesProcesadasYCancelada);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("sinProcesar", sinProcesar);
        model.addAttribute("enProcesoEstado", enProcesoEstado);
        model.addAttribute("Creado", Creado);
        model.addAttribute("enValidacion", enValidacion);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("enRuta", enRuta);
        model.addAttribute("enAduanas", enAduanas);
        model.addAttribute("ArriboAlPais", ArriboAlPais);
        model.addAttribute("Recibido", Recibido);
        model.addAttribute("Cancelado", Cancelado);


        return "agente/dashboard";
    }

    @GetMapping("/chat")
    public String chatAgente(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Obtener información del agente
        List<Usuario> usuariosAsignados = usuarioRepository.findUsuariosAsignadosAlAgente(usuarioLogueado.getZona().getIdzonas());
        ArrayList<Integer> idUsuariosAsignados = new ArrayList<>();
        for (Usuario u : usuariosAsignados) {
            idUsuariosAsignados.add(u.getId());
        }

        // Obtener los mensajes por sala y unir en una sola lista
        ArrayList<List<LiveMessages>> mensajesPorSala = new ArrayList<>();
        for (Integer ids : idUsuariosAsignados) {
            String room = "room_" + ids;
            List<LiveMessages> mensajesSala = liveMessagesRepository.findBySalaOrderByFechaenvioAsc(room);
            mensajesPorSala.add(mensajesSala);  // Agrega los mensajes de cada sala a la lista
        }


        model.addAttribute("listaIdUsuarios", idUsuariosAsignados);
        model.addAttribute("listaUsuarios", usuariosAsignados);
        model.addAttribute("mensajesPorSala", mensajesPorSala); // Pasa los mensajes por sala
        return "agente/chat";
    }


    @PostMapping(value = "/buscaragente")
    public String listaAgentes(Model model,@RequestParam("idzona") Integer idzona, RedirectAttributes redirectAttributes) {
        model.addAttribute("listaAgentes", usuarioRepository.listarAgentesPorZona(idzona));

        return "redirect:agente/chat";
    }





//-------------------secion ordenes--------------------------------------------------------------

    @GetMapping(value = "/ordenes")
    public String listadeOrdenes(Model model,HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesPorZona(usuarioLogueado.getZona().getIdzonas()));
        //por sesion obtener el objeto idzona del usuario agente mediante en una funcion
        //crear un query  en repositorio que filtre las ordenes por id zona
        //algo como esto:

        // SELECT o.*
        //FROM ordenes o
        //JOIN usuarios u ON o.idusuarios = u.idusuarios
        //WHERE u.idzonas = ?;

        //cambiar el model atribute actual
        //la lista de ordenes que se debe visualizar debe ser las ordenes donde el idzonas del usuario debe ser igual al id zonas del agente

        return "agente/ordenes";
    }

    // Con Query
    /*
    @GetMapping(value = "ordenes")
    public String listadeOrdenes(Model model,@RequestParam(defaultValue = "0") int page) {
        int limit = 5;
        int offset = page * limit;
        model.addAttribute("listaOrdenes", ordenRepository.findPaginatedOrders(limit, offset));
        return "agente/ordenesConQuery";
    }
    */
    /*
    @GetMapping(value = "ordenes/sinAsignar")
    public String listadeOrdenesSinAsignar(Model model) {

        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesSinAsignar());
        return "agente/ordenesSinAsignar";
    }

    @GetMapping(value = "ordenes/Pendientes")
    public String listadeOrdenesEnValidacion(Model model) {

        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesEnValidacion());
        return "agente/ordenesPendientes";
    }

    @GetMapping(value = "ordenes/enProgreso")
    public String listadeOrdenesEnProgreso(Model model) {
        // Usar el metodo para obtener las órdenes con los estados 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta'
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesEnProgreso());
        return "agente/ordenesEnProgreso";
    }

    @GetMapping(value = "ordenes/resueltas")
    public String listaDeOrdenesResueltas(Model model) {
        // Usar el metodo para obtener las órdenes con los estados 'Recibido' y 'Cancelado'
        model.addAttribute("listaOrdenes", ordenRepository.ordenesResueltas());
        return "agente/ordenesResueltas";
    }*/

    @GetMapping(value = "/ordenes/sinAsignar")
    public String listaDeOrdenesSinAsignar(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Filtrar por zona y estado "CREADO"
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesSinAsignarPorZona(usuarioLogueado.getZona().getIdzonas()));
        return "agente/ordenesSinAsignar";
    }

    @GetMapping(value = "/ordenes/Pendientes")
    public String listaDeOrdenesPendientes(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Filtrar por zona y estado "En Validación"
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesPendientesPorZona(usuarioLogueado.getZona().getIdzonas()));
        return "agente/ordenesPendientes";
    }

    @GetMapping(value = "/ordenes/enProgreso")
    public String listaDeOrdenesEnProgreso(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Filtrar por zona y estados "En Proceso", "Arribo al País", "En Aduanas", "En Ruta"
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesEnProgresoPorZona(usuarioLogueado.getZona().getIdzonas()));
        return "agente/ordenesEnProgreso";
    }


    @GetMapping(value = "/ordenes/resueltas")
    public String listaDeOrdenesResueltas(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Filtrar por zona y estados "Recibido" o "Cancelado"
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesResueltasPorZona(usuarioLogueado.getZona().getIdzonas()));
        return "agente/ordenesResueltas";
    }

    //------------------------------------------------------------------------------

    @GetMapping( "/updaterorden")
    public String VistaEstadoOrden(Model model, @RequestParam("id") int id) {
        Optional<Orden> optOrden = ordenRepository.findById(id);
        if (optOrden.isPresent()) {
            Orden orden = optOrden.get();
            model.addAttribute("orden", orden);
            return "agente/actualizarestadodeorden";
        } else {
            return "redirect:/agente/ordenes";
        }
    }

    @PostMapping("/actualizarEstado")
    public String avanzarEstado(@RequestParam("id") Integer id) {
        Orden orden = ordenRepository.findByIdOrden(id);
        if (orden!=null) {
            // Obtener la fecha actual y actualizar el campo de fecha en la orden
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql); // Asegúrate de que este campo exista en la entidad Orden

            String estadoActual = orden.getEstado();
            // Lógica para avanzar el estado
            switch (estadoActual) {
                case "Creado":
                    orden.setEstado("En Validación");
                    break;
                case "En Validación":
                    orden.setEstado("En Proceso");
                    break;
                case "En Proceso":
                    orden.setEstado("Arribo al País");
                    break;
                case "Arribo al País":
                    orden.setEstado("En Aduanas");
                    break;
                case "En Aduanas":
                    orden.setEstado("En Ruta");
                    break;
                case "En Ruta":
                    orden.setEstado("Recibido");
                    break;
                default:
                    // Manejo de casos no esperados
                    return "redirect:/agente/updaterorden?id=" + id;
            }

            ordenRepository.save(orden);
        }
        return "redirect:/agente/updaterorden?id=" + id; // Redirige si no se encuentra la orden
    }

    @PostMapping("/cancelarorden")
    public String cancelarOrden(@RequestParam(value = "id", required = false) Integer id) {
        Orden orden = ordenRepository.findById(id).orElse(null);
        if (orden != null) {
            // Lógica para avanzar el estado
            orden.setEstado("Cancelado");
            // Obtener la fecha actual y actualizar el campo de fecha en la orden
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql); // Asegúrate de que este campo exista en la entidad Orden

            ordenRepository.save(orden);
            return "redirect:/agente/updaterorden?id=" + id; // Redirige de vuelta a la vista de la orden
        }
        return "redirect:/agente/ordenes"; // Redirige si no se encuentra la orden
    }
    @GetMapping("/detallesorden")
    public String verDetallesOrden(Model model, @RequestParam("id") int id) {

        Optional<Orden> optOrden = ordenRepository.findById(id);
        Optional<Orden> optOrden2 = ordenRepository.findByIdWithDetails(id);

        if (optOrden.isPresent() && optOrden2.isPresent()) {
            Orden orden = optOrden.get();
            Orden orden2 = optOrden2.get();
            model.addAttribute("orden", orden);
            model.addAttribute("ordenDetalles", orden2); // Diferente nombre
            return "agente/detalledeordenagente";
        } else {
            return "redirect:/agente/ordenes";
        }
    }

    @GetMapping("/{id}")
    public String getOrdenProductos(@PathVariable("id") int id, Model model) {
        Optional<Orden> ordenOptional = ordenRepository.findByIdWithDetails(id);
        if (ordenOptional.isPresent()) {
            model.addAttribute("orden", ordenOptional.get());
            return "detalle_orden"; // la vista que mostrará los productos
        } else {
            return "error"; // en caso de que no exista la orden
        }
    }

    @GetMapping("/usuariosAsignados")
    public String usuariosAsignados(Model model) {
        // Lista de órdenes (sin cambios)
        model.addAttribute("listaOrdenes", ordenRepository.findAll());

        // Obtener usuarios activos (sin filtro de rol)
        model.addAttribute("listaAsignados", usuarioRepository.findActivos());

        // Redirigir a la vista
        return "agente/usuariosAsignados";
    }

    @GetMapping(value = "/usuariosBaneados")
    public String usuariosBaneados(Model model) {
        model.addAttribute("listaOrdenes", ordenRepository.findAll());

        model.addAttribute("listaBaneados", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));

        return "agente/usuariosBaneados";
    }
    // Metodo para redirigir al formulario de baneo
    @GetMapping("/formulariodebaneo")
    public String mostrarFormularioBaneo(Model model, @RequestParam("id") Integer id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "agente/formdebaneo";  // Renderiza el formulario correctamente
        } else {
            return "redirect:/agente/usuariosAsignados";  // Redirige si el usuario no se encuentra
        }
    }

    // Metodo para redirigir a la vista de detalle de baneo
    @GetMapping("/detallebaneo")
    public String mostrarDetalledeBaneo(Model model, @RequestParam("id") Integer id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "agente/detallebaneo";  // Renderiza el formulario correctamente
        } else {
            return "redirect:/agente/usuariosBaneados";  // Redirige si el usuario no se encuentra
        }
    }



    // Metodo para banear a un usuario
    @PostMapping("/banear")
    public String banearUsuario(Model model, @RequestParam("id") Integer id, @RequestParam("motivoBaneo") String motivoBaneo) {
        // Buscar el usuario por su ID
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        // Verificar si el usuario existe
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuarioRepository.banear(id,motivoBaneo);
            return "redirect:/agente/usuariosAsignados";

        } else {
            model.addAttribute("error", "Usuario no encontrado.");
            return "agente/formdebaneo";
        }
    }

    // Metodo para banear a un usuario
    @PostMapping("/desbanear")
    public String desbanearUsuario(Model model, @RequestParam("id") Integer id) {
        // Buscar el usuario por su ID
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        // Verificar si el usuario existe
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuarioRepository.desbanear(id);
            return "redirect:/agente/usuariosBaneados";

        } else {
            model.addAttribute("error", "Usuario no encontrado.");
            return "agente/detallebaneo";
        }
    }


    @GetMapping(value = "/reportesOrdenes")
    public String reportesOrdenes(Model model) {
        model.addAttribute("listaUsuario", usuarioRepository.findByBorradoAndRol_Rol(1,"Usuario"));

        return "agente/reportesOrdenes";
    }

    @GetMapping(value = "/reportesGuardados")
    public String reportesGuardados(Model model) {
        model.addAttribute("listaReportes", reporteRepository.findAll());

        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));

        return "agente/reportesguardados";
    }

    //----------------------seccion generación de reportes-----------------------------------------------------------------------------
    //reporte órdenes por usuario
    @GetMapping(value = "/reportePorUsuario")
    public String reporteOrdenesPorUsuario(Model model) {
        model.addAttribute("listaOrdenes", ordenRepository.findAll());
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));


        return "agente/reportePorUsuario";
    }
    //reporte órdenes totales
    @GetMapping(value = "/reporteOrdenesTotales")
    public String reporteOrdenesTotales(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));
        model.addAttribute("listaOrdenes", ordenRepository.findByBorrado(1));
        return "agente/reporteordenestotales";
    }


    //reporte órdenes de agentes por zona
    /*
    @GetMapping(value = "/reportePorAgente")
    public String reporteOrdenesAgentesZona(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Agente",1));
        return "agente/reporteporagente";
    }
    */
    @GetMapping(value = "/reportePorAgente")
    public String reporteOrdenesAgentesZona(Model model,HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        model.addAttribute("listaOrdenes", ordenRepository.findOrdenesPorZona(usuarioLogueado.getZona().getIdzonas()));
        return "agente/reporteporagente";
    }

    //reporte órdenes totales por fecha
    @GetMapping(value = "/reportePorFecha")
    public String reporteOrdenesTotalesporFiltrofecha(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));
        model.addAttribute("listaOrdenes", ordenRepository.findByBorrado(1));
        return "agente/reporteporfecha";
    }
    @Autowired
    private OrdenService ordenService;

    @GetMapping("/exportarOrdenesPorUsuario")
    public ResponseEntity<Resource> exportUserOrders(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String dni,
            @RequestParam String formato) {

        return this.ordenService.exportUserOrders(nombre, dni, formato);
    }

    // Nuevo metodo para exportar órdenes por rango de fechas
    @GetMapping("/exportarOrdenesPorFecha")
    public ResponseEntity<Resource> exportOrdersByDateRange(
            @RequestParam("fechaInicio") String startDate,
            @RequestParam("fechaFin") String endDate,
            @RequestParam String formato) throws ParseException {

        // Convertir las fechas de String a java.sql.Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Crear fechas de tipo java.util.Date
        java.util.Date utilStartDate = dateFormat.parse(startDate);
        java.util.Date utilEndDate = dateFormat.parse(endDate);

        // Convertir las fechas a java.sql.Date
        java.sql.Date sqlStartDate = new java.sql.Date(utilStartDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(utilEndDate.getTime());

        // Llamar al servicio con las fechas de tipo java.sql.Date
        return this.ordenService.exportTotalOrdersByDate(sqlStartDate, sqlEndDate, formato);

    }

    @GetMapping("/exportarOrdenesTotales")
    public ResponseEntity<Resource> exportAllOrders(@RequestParam String formato) {
        return this.ordenService.exportAllOrders(formato);
    }

    @GetMapping("/exportarOrdenesPorAgente")
    public ResponseEntity<Resource> exportOrdersByAgent(HttpSession session, @RequestParam String formato) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
        Integer zonaId = usuarioLogueado.getZona().getIdzonas();
        String agenteNombre = usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellido();
        String zonaNombre = usuarioLogueado.getZona().getNombre();

        return this.ordenService.exportOrdersByAgent(zonaId, agenteNombre, zonaNombre, formato);
    }


//----------------------fin seccion generación de reportes-----------------------------------------------------------------------------


    @PostMapping("/cambiarContrasenia")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cambiarContrasenia(
            @RequestParam("id") int id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword) {

        Map<String, String> response = new HashMap<>();
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Verificar si la contraseña antigua es correcta
            if (!passwordEncoder.matches(oldPassword, usuario.getPwd())) {
                response.put("status", "error");
                response.put("message", "La contraseña antigua es incorrecta.");
                return ResponseEntity.ok(response);
            }

            // Verificar si la nueva contraseña y la confirmación coinciden
            if (!newPassword.equals(confirmNewPassword)) {
                response.put("status", "error");
                response.put("message", "La nueva contraseña y su confirmación no coinciden.");
                return ResponseEntity.ok(response);
            }
            // Validar requisitos de la nueva contraseña
            if (newPassword.length() < 8 || newPassword.length() > 16) {
                response.put("status", "error");
                response.put("message", "La contraseña debe tener entre 8 y 16 caracteres.");
                return ResponseEntity.ok(response);
            }

            if (!newPassword.matches("^(?=.*\\d)(?=.*[a-zA-Z])(?=(?:.*[!@#$%^&*]){2}).{8,16}$")) {
                response.put("status", "error");
                response.put("message", "La contraseña debe incluir al menos 1 letra, 1 número y 2 caracteres especiales.");
                return ResponseEntity.ok(response);
            }

            // Cambiar la contraseña si pasa todas las verificaciones
            String newPasswordEncrypted = passwordEncoder.encode(newPassword);
            usuario.setPwd(newPasswordEncrypted);
            usuarioRepository.save(usuario);

            response.put("status", "success");
            response.put("message", "Contraseña cambiada exitosamente.");
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Usuario no encontrado.");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> cambiarContrasenia(
            @RequestParam("id") int id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Verificar si la contraseña antigua es correcta
            if (!passwordEncoder.matches(oldPassword, usuario.getPwd())) {
                response.put("status", "error");
                response.put("message", "La contraseña antigua es incorrecta.");
                return ResponseEntity.ok(response); // Devuelve error
            }

            // Encriptar la nueva contraseña
            String newPasswordEncrypted = passwordEncoder.encode(newPassword);
            usuario.setPwd(newPasswordEncrypted);
            usuarioRepository.save(usuario);

            response.put("status", "success");
            response.put("message", "Contraseña cambiada exitosamente.");
            return ResponseEntity.ok(response); // Devuelve éxito
        }

        response.put("status", "error");
        response.put("message", "Usuario no encontrado.");
        return ResponseEntity.ok(response); // Devuelve error si no encuentra usuario
    }
    // Metodo para obtener la lista de usuarios baneados
    @GetMapping("/baneados")
    public List<Usuario> obtenerUsuariosBaneados() {
        return usuarioRepository.findBaneados();  // Devuelve la lista de usuarios baneados
    }

    // Metodo para obtener la lista de usuarios activos
    @GetMapping("/activos")
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findActivos();  // Devuelve la lista de usuarios activos
    }

    @GetMapping("/verDetallesUsuario")
    public String verDetallesUsuario(Model model, @RequestParam("id") int id) {
        // Obtiene el usuario por ID
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        // Verifica si el usuario existe
        if (optionalUsuario.isPresent()) {
            // Agrega el usuario al modelo
            model.addAttribute("usuario", optionalUsuario.get());
        }
        return "agente/detallesperfil"; // Retorna el nombre de la vista
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
