package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;

import com.example.springdogless.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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


    @GetMapping({""})
    public String PaginaPrincipal(Model model) {

        return "agente/paginaprincipal";
    }

    @GetMapping({"dashboard"})
    public String ElDashboard007(Model model) {
        return "/agente/dashboard";
    }
    @GetMapping("chat")
    public String Chat(Model model) {
        return "/agente/chat";
    }

    @GetMapping(value = "ordenes")
    public String listaReposiciones(Model model) {
        model.addAttribute("listaOrdenes", ordenRepository.findByBorrado(1));
        return "agente/ordenes";
    }
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
    public String avanzarEstado(@RequestParam(value = "id", required = false) Integer id) {
        Orden orden = ordenRepository.findById(id).orElse(null);

        if (orden != null) {
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
                    orden.setEstado("En ruta");
                    break;
                case "En Ruta":
                    orden.setEstado("Recibido");
                    break;
                case "Recibido":
                    // En este caso podrías devolver a otra ruta si lo deseas
                    return "redirect:/agente/ordenes"; // O cualquier otra acción
                default:
                    return "redirect:/agente/updaterorden?id=" + id; // Retorna a la misma vista
            }
            // Obtener la fecha actual y actualizar el campo de fecha en la orden
            Date fechaActualUtil = new Date();
            java.sql.Date fechaActualSql = new java.sql.Date(fechaActualUtil.getTime());
            orden.setFecha(fechaActualSql); // Asegúrate de que este campo exista en la entidad Orden

            ordenRepository.save(orden);
            return "redirect:/agente/updaterorden?id=" + id; // Redirige de vuelta a la vista de la orden
        }
        return "redirect:/agente/ordenes"; // Redirige si no se encuentra la orden
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
            return "redirect:/admin/ordenes";
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

    @GetMapping(value = "/reportesOrdenes")
    public String reportesOrdenes(Model model) {
        model.addAttribute("listaOrdenes", ordenRepository.findAll());

        model.addAttribute("listaBaneados", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));

        return "agente/reportesOrdenes";
    }

    //Lista de órdenes por usuario
    @GetMapping(value = "/reportePorUsuario")
    public String ordenesPorUsuario(Model model) {
        model.addAttribute("listaOrdenes", ordenRepository.findAll());

        model.addAttribute("listaUsuarios", usuarioRepository.findByRol_RolAndBorrado("Usuario",1));

        return "agente/reportePorUsuario";
    }

    // Metodo para redirigir al formulario de baneo
    @GetMapping("/formulariodebaneo")
    public String mostrarFormularioBaneo(Model model, @RequestParam("id") int id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "agente/formdebaneo";  // Renderiza el formulario correctamente
        } else {
            return "redirect:/agente/usuariosAsignados";  // Redirige si el usuario no se encuentra
        }
    }

    // Metodo para banear a un usuario (luego de presionar el botón banear en el formulario)


    // Metodo para desbanear a un usuario
    @PostMapping("/banear")
    public String banearUsuario(Model model, @RequestParam("id") int id, @RequestParam("motivoBaneo") String motivoBaneo) {

        // Buscar el usuario por su ID
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        // Verificar si el usuario existe
        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Verificar si el usuario ya está baneado
            if (!usuario.getEstado().equals("baneado")) {
                usuario.setEstado("baneado");  // Cambiar el estado a 'baneado'
                usuario.setFechabaneo(new java.sql.Date(new Date().getTime()));  // Registrar la fecha de baneo
                usuario.setMotivobaneo(motivoBaneo);  // Registrar el motivo de baneo
                usuarioRepository.save(usuario);  // Guardar los cambios en la base de datos

                // Redirigir a la lista de usuarios después de banear
                return "redirect:/agente/usuariosAsignados";
            } else {
                model.addAttribute("error", "El usuario ya está baneado.");
                return "agente/formdebaneo";
            }
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
            return "agente/formdebaneo";
        }
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


    /*
    @GetMapping({"/lista", ""})
    public String listaUsuariosTotales(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/paginaprincipal";
    }
    //seccion de usuarios
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

    @GetMapping("/admin/borrarUsuario")
    public String eliminarUsuario(Model model,
                                  @RequestParam("id") int id,
                                  RedirectAttributes attr) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            usuarioRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        }
        return "redirect:/admin/usuarios";

    }

    @PostMapping("/saveadminzonal")
    public String guardarAdminZonal(@RequestParam("id") int id,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("apellido") String apellido,
                                    @RequestParam("correo") String correo,
                                    @RequestParam("telefono") String telefono,
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
    @PostMapping("/saveagente")
    public String guardarAgente(@RequestParam("id") int id,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("apellido") String apellido,
                                @RequestParam("correo") String correo,
                                @RequestParam("telefono") String telefono,
                                @RequestParam("ruc") String ruc,
                                @RequestParam("codigoAduana") String codidoaduana,
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
            usuario.setCodigoaduana(codidoaduana);
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

        return "redirect:/admin/agentes";
    }

    @PostMapping("/saveusuario")
    public String guardarUsuario(@RequestParam("id") int id,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam("apellido") String apellido,
                                 @RequestParam("correo") String correo,
                                 @RequestParam("telefono") String telefono,
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

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/veradminzonal")
    public String verAdminZonal(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "admin/ver_adminzonal";
        } else {
            return "redirect:/admin/adminzonal";
        }
    }

    @GetMapping("/veragente")
    public String verAgente(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "admin/ver_agente";
        } else {
            return "redirect:/admin/agentes";
        }
    }

    @GetMapping("/verusuario")
    public String verUsuario(Model model, @RequestParam("id") int id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            model.addAttribute("usuario", usuario);
            return "admin/ver_usuarios";
        } else {
            return "redirect:/admin/usuarios";
        }
    }
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

        return "redirect:/admin/agentes";
    }
    @PostMapping("/deleteusuario")
    public String borrarUsuario(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            usuario.setBorrado(0);
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msg", "Usuario borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
        }

        return "redirect:/admin/usuarios";
    }



    //termina usuarios

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
        usuario.setBorrado(1);
        String contrasenaPorDefecto = "contraseñaPredeterminada";
        usuario.setContrasena(contrasenaPorDefecto);
        // Guardar el nuevo Adminzonal
        usuarioRepository.save(usuario);
        attr.addFlashAttribute("mensajeExito", "Administrador zonal creado correctamente");

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
        return "redirect:/admin/solicitudes";
    }

    //Vista de Proveedores

    @GetMapping("/proveedores")
    public String listaProveedores(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProveedores", proveedorRepository.findAll());
        return "admin/proveedores";
    }


    @GetMapping("/verProveedor")
    public String verProveedor(Model model, @RequestParam("id") int id) {

        Optional<Proveedor> optProveedor = proveedorRepository.findById(id);

        if (optProveedor.isPresent()) {
            Proveedor proveedor = optProveedor.get();
            model.addAttribute("proveedor", proveedor);

            return "/admin/verProveedor";
        } else {
            return "redirect:/admin/proveedores";
        }
    }


    @GetMapping(value = "/nuevoProveedor")
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
            model.addAttribute("id", id);
            model.addAttribute("proveedor", proveedor);

            return "admin/editarProveedor";
        } else {
            return "redirect:/admin/proveedores";
        }
    }



    @PostMapping("/guardarProveedor")
    public String guardarProveedor(@RequestParam(value = "id", required = false) Integer id, @ModelAttribute Proveedor proveedor, RedirectAttributes attr) {
        if (id != 0) {
            Optional<Proveedor> optProveedor = proveedorRepository.findById(id);

            if (optProveedor.isPresent()) {
                Proveedor proveedorExistente = optProveedor.get();
                proveedorExistente.setDni(proveedor.getDni());
                proveedorExistente.setRuc(proveedor.getRuc());
                proveedorExistente.setTelefono(proveedor.getTelefono());
                attr.addFlashAttribute("msg", "Proveedor actualizado exitosamente");
            } else {
                attr.addFlashAttribute("error", "Proveedor no encontrado");
                return "redirect:/admin/proveedores";
            }
        } else {
            proveedor.setBorrado(1); // Inicialización en caso de ser un nuevo proveedor
            proveedorRepository.save(proveedor);
            attr.addFlashAttribute("msg", "Proveedor creado exitosamente");
        }
        return "redirect:/admin/proveedores";
    }




    //Vista de productos
    @GetMapping("/productos")
    public String listaProductos(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProductosCompleto", productRepository.ProductosCompleto());
        //model.addAttribute("listaProductos", productRepository.findByBorrado(1));
        //return "/admin/productos";
        return "admin/productogod";
    }



    @GetMapping("/verProducto")
    public String verProducto(Model model, @RequestParam("id") int id) {

        Optional<Producto> optProducto = productRepository.findById(id);

        if (optProducto.isPresent()) {
            Producto producto = optProducto.get();
            model.addAttribute("producto", producto);
            return "admin/verProducto";
        } else {
            return "redirect:/admin/adminzonal";
        }
    }
    @GetMapping(value = "/nuevoProducto")
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
    @PostMapping("/saveproducto")
    public String guardarProducto2(@RequestParam("id") int id,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("descripcion") String descripcion,
                                    @RequestParam("categoria") String categoria,
                                    @RequestParam("precio") Double precio,
                                    @RequestParam("costoenvio") Double costoenvio,
                                    @RequestParam("colores") String colores,
                                    @RequestParam int proveedor,
                                    RedirectAttributes attr) {
        // Obtener el usuario existente
        Optional<Producto> optProducto = productRepository.findById(id);

        if (optProducto.isPresent()) {
            Producto producto = optProducto.get();

            // Actualizar solo los campos editables
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setPrecio(precio);
            producto.setCostoenvio(costoenvio);
            producto.setColores(colores);

            // Buscar las entidades relacionadas por sus IDs
            Proveedor proveedorEntity = proveedorRepository.findById(proveedor).orElse(null); // Se maneja si no existe

            // Asignar las entidades encontradas al usuario
            producto.setProveedor(proveedorEntity);
            // Guardar el usuario actualizado
            productRepository.save(producto);
            attr.addFlashAttribute("mensajeExito", "Cambios guardados correctamente");
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
        }

        return "redirect:/admin/productos";
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

    @PostMapping("/borrarProducto")
    public String borrarProducto(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Producto> optProducto = productRepository.findByproductosinBorrar(id);

        if (optProducto.isPresent()) {
            Producto producto = optProducto.get();
            producto.setBorrado(2);
            productRepository.save(producto);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Producto no encontrado");
        }

        return "redirect:/product";
    }

    //Lista Productos aprobados
    @GetMapping("/productosAprobados")
    public String aprobados(Model model) {
        // Busca todas las reposiciones con el campo "aprobado" igual a "aprobado"
        List<Reposicion> reposicionesAprobadas = reposicionesRepository.findByAprobar("aprobado");
        model.addAttribute("listaReposicionesAprobadas", reposicionesAprobadas);
        return "admin/productosAprobados1";
    }


    @GetMapping("/productosRechazados")
    public String rechazados(Model model) {
        // Busca todas las reposiciones con el campo "aprobado" igual a "rechazado"
        List<Reposicion> reposicionesRechazadas = reposicionesRepository.findByAprobar("rechazado");
        model.addAttribute("listaReposicionesRechazadas", reposicionesRechazadas);
        return "admin/productosRechazados1";
    }

    //Lista Productos pendientes
    @GetMapping("/pendientes")
    public String pendientes(Model model) {
        model.addAttribute("listaReposiciones", reposicionesRepository.findByAprobarIsNull());
        return "admin/productosPendientes";
    }

    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Integer id) {
        // Busca la reposición por ID
        Reposicion reposicion = reposicionesRepository.findById(id).orElse(null);

        if (reposicion != null && reposicion.getAprobar()==null) {
            Optional<Producto> producto = productRepository.findById(reposicion.getProducto().getId());

            if (producto.isPresent()) {

                // Verificar si el producto ya existe en la tabla stockproductos
                Integer cantidadExistente = stockRepository.existeProductoEnZona(reposicion.getProducto().getId(), reposicion.getZona().getIdzonas());

                if (cantidadExistente != 0) {
                    // Si el producto existe, actualiza el stock
                    stockRepository.actualizarStock(
                            Integer.parseInt(reposicion.getCantidad()),
                            reposicion.getProducto().getId(),
                            reposicion.getZona().getIdzonas());
                } else {
                    // Si el producto no existe, inserta un nuevo registro en la tabla stockproductos
                    stockRepository.insertarNuevoStock(
                            reposicion.getProducto().getId(),
                            reposicion.getZona().getIdzonas(),
                            Integer.parseInt(reposicion.getCantidad()));
                }
                // Actualiza el atributo 'aprobado' de la reposición
                reposicion.setAprobar("aprobado");
                reposicionesRepository.save(reposicion); // Guarda los cambios en la base de datos

                return "redirect:/admin/productosAprobados";

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
                return "redirect:/admin/productosRechazados";
            }
        }
        // Redirige si la reposición no se encuentra o el producto no está presente
        return "redirect:/admin";
    }

    @GetMapping("/dashboard")
    public String elDashboardEstaTristeYAzul(Model model, @RequestParam(required = false) String zona) {
        /*model.addAttribute("listaProveedores", proveedorRepository.findAll());
        return "admin/dashboard";
    }
    */



}
