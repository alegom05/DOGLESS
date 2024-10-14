package com.example.springdogless.controllers;

import com.example.springdogless.Repository.*;

import com.example.springdogless.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping({"admin", "admin/"})
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
    private ProductRepository productRepository;


    @GetMapping("/perfil_superadmin")
    public String verperfiladmin(Model model) {
        return "admin/perfil_superadmin"; // Esto renderiza la vista perfil_superadmin.html
    }

    @GetMapping({"/lista", ""})
    public String listaUsuariosTotales(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));
        model.addAttribute("listaAgentes", usuarioRepository.findByRol(3));
        model.addAttribute("listaZonales", usuarioRepository.findByRol(2));

        return "admin/paginaprincipal";
    }
    /*seccion de usuarios */
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
    /*
    @PostMapping("/cambiarContrasenia")
    public String cambiarContrasenia(@RequestParam("id") int id,
                                     @RequestParam("oldPassword") String oldPassword,
                                     @RequestParam("newPassword") String newPassword,
                                     RedirectAttributes redirectAttributes) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // Verifica si la contraseña antigua es correcta
            if (!passwordEncoder.matches(oldPassword, usuario.getPwd())) {
                redirectAttributes.addFlashAttribute("error", "La contraseña antigua es incorrecta.");
                return "redirect:/admin/editadminzonal?id=" + id;
            }

            // Encripta la nueva contraseña antes de guardarla
            String newPasswordEncrypted = passwordEncoder.encode(newPassword);
            usuario.setPwd(newPasswordEncrypted);
            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña cambiada exitosamente.");
            return "redirect:/admin/editadminzonal?id=" + id;
        } else {
            return "redirect:/admin/adminzonal";
        }
    }

     */
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
                                    @RequestParam("email") String email,
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
            usuario.setEmail(email);
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
                                @RequestParam("email") String email,
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
            usuario.setEmail(email);
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
                                 @RequestParam("email") String email,
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
            usuario.setEmail(email);
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
            attr.addFlashAttribute("mensajeExito", "Admin zonal borrado exitosamente");
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
        String contrasenaPorDefecto = "123456";
        // Encriptar la contraseña con BCrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String contrasenaEncriptada = passwordEncoder.encode(contrasenaPorDefecto);
        usuario.setPwd(contrasenaEncriptada);
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
        model.addAttribute("listaSolicitudes", solicitudRepository.findSolicitudes());
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
    @PostMapping("/aprobar")
    public String aprobarSolicitud(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        // Obtener la solicitud por su ID
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(id);

        // Verificar si la solicitud existe
        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();

            // Obtener el usuario asociado a la solicitud
            Usuario usuario = solicitud.getUsuario();

            Rol adminzonalRole = rolRepository.findById(3).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            usuario.setRol(adminzonalRole);

            // Cambiar el veredicto de la solicitud a 1 (aprobado)
            solicitud.setVeredicto((byte) 1);

            // Guardar los cambios en la base de datos
            solicitudRepository.save(solicitud);
            usuarioRepository.save(usuario);

            // Redirigir con un mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", "La solicitud ha sido aprobada.");
        } else {
            // En caso de que la solicitud no exista, agregar un mensaje de error
            redirectAttributes.addFlashAttribute("mensajeError", "La solicitud no se encontró.");
        }

        return "redirect:/admin/solicitudes";
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
    @PostMapping("/denegar")
    public String rechazarSolicitud(@RequestParam("id") Integer id, @RequestParam("comentario") String comentario, RedirectAttributes redirectAttributes) {
        // Obtener la solicitud por su ID
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(id);

        // Verificar si la solicitud existe
        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();

            // Cambiar el veredicto de la solicitud a 1 (aprobado)
            solicitud.setVeredicto((byte) 0);
            solicitud.setComentario(comentario);

            // Guardar los cambios en la base de datos
            solicitudRepository.save(solicitud);

            // Redirigir con un mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", "La solicitud ha sido denegada.");
        } else {
            // En caso de que la solicitud no exista, agregar un mensaje de error
            redirectAttributes.addFlashAttribute("mensajeError", "La solicitud no se encontró.");
        }

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
    @PostMapping("/saveProveedor")
    public String crearProveedor(Proveedor proveedor,
                                  RedirectAttributes attr) {
        proveedor.setBorrado(1);
        // Guardar el nuevo Adminzonal
        proveedorRepository.save(proveedor);
        attr.addFlashAttribute("mensajeExito", "Proveedor creado correctamente");

        return "redirect:/admin/proveedores";
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
                                    @RequestParam("imagen") MultipartFile imagen,
                                    @RequestParam int proveedor,
                                    RedirectAttributes attr) throws IOException {
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


            // Si se sube una nueva imagen, convertirla a byte[] y guardarla
            if (!imagen.isEmpty()) {
                byte[] imagenBytes = imagen.getBytes();
                producto.setImagenprod(imagenBytes);
            }


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
    @GetMapping("/productosPendientes")
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
                            reposicion.getCantidad(),
                            reposicion.getProducto().getId(),
                            reposicion.getZona().getIdzonas());
                } else {
                    // Si el producto no existe, inserta un nuevo registro en la tabla stockproductos
                    stockRepository.insertarNuevoStock(
                            reposicion.getProducto().getId(),
                            reposicion.getZona().getIdzonas(),
                            reposicion.getCantidad());
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
        /*model.addAttribute("listaProveedores", proveedorRepository.findAll());*/
        return "admin/dashboard";
    }



    @PostMapping("/borrarProveedores")
    public String borrarProveedores(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Proveedor> optProv = proveedorRepository.findById(id);

        if (optProv.isPresent()) {
            Proveedor proveedor = optProv.get();
            proveedor.setBorrado(0);
            proveedorRepository.save(proveedor);
            attr.addFlashAttribute("msg", "Proveedor borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Proveedor no encontrado");
        }

        return "redirect:/admin/agentes";
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

    @GetMapping("/producto/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id) {
        System.out.println("Llamando a getImage con ID: " + id); // Agrega este log
        Optional<Producto> productoOptional = productRepository.findById(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            byte[] imagenBytes = producto.getImagenprod();

            // Log para verificar la longitud de la imagen
            System.out.println("Imagen encontrada para ID: " + id + ", longitud de imagen en bytes: " + imagenBytes.length);

            // Configuración de tipo de contenido
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // o MediaType.IMAGE_PNG según sea el caso
                    .body(imagenBytes);
        }

        System.out.println("No se encontró producto con ID: " + id); // Agrega este log
        return ResponseEntity.notFound().build();
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
