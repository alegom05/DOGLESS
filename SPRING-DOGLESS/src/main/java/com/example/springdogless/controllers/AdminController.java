package com.example.springdogless.controllers;

import com.example.springdogless.DTO.OrdenDTO;
import com.example.springdogless.DTO.ProductoDTO;
import com.example.springdogless.DTO.ProveedorDTO;
import com.example.springdogless.Repository.*;

//import com.example.springdogless.dao.UsuarioDao;
import com.example.springdogless.dao.UsuarioDao;
import com.example.springdogless.entity.*;
import com.example.springdogless.services.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    StockProductoRepository stockProductoRepository;
    @Autowired
    ReposicionRepository reposicionesRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    OrdenRepository ordenRepository;
    @Autowired
    UsuarioDao usuarioDao;
    @Autowired
    private EmailService emailService;


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
    /* seccion de usuarios */
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
    @GetMapping({"/usuarios"})
    public String listarProductos(Model model) {


        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));

        return "admin/usuarios";
    }

    /*
    @GetMapping("/usuarios")
    public String listaUsuarios(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaUsuarios", usuarioRepository.findByRol(4));

        return "admin/usuarios";
    }
    */

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
            attr.addFlashAttribute("mensajeExito", "Agente borrado exitosamente");
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
            attr.addFlashAttribute("mensajeExito", "Usuario borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
        }

        return "redirect:/admin/usuarios";
    }
    @PostMapping("/deleteproveedor")
    public String borrarProveedor(@RequestParam("id") Integer id, RedirectAttributes attr) {
        Optional<Proveedor> optProveedor = proveedorRepository.findById(id);

        if (optProveedor.isPresent()) {
            Proveedor proveedor = optProveedor.get();
            proveedor.setBorrado(0);
            proveedorRepository.save(proveedor);
            attr.addFlashAttribute("mensajeExito", "Proveedor borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "Admin no encontrado");
        }

        return "redirect:/admin/proveedores";
    }

    @PostMapping("/impersonate")
    public String impersonateUser(@RequestParam("id") Integer userId, HttpSession session, RedirectAttributes attr) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(userId);

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();

            // Guardar temporalmente el usuario actual (SuperAdmin) completo en la sesión
            session.setAttribute("originalUser", session.getAttribute("usuario"));

            // Cambiar el usuario actual en la sesión por el usuario seleccionado
            session.setAttribute("usuario", usuario);  // Aquí guardamos el usuario completo
            session.setAttribute("userId", usuario.getId());
            session.setAttribute("userRole", usuario.getRol());

            attr.addFlashAttribute("mensajeExito", "Ahora estás logueado como " + usuario.getNombre());

            // Redirigir al usuario a su vista correspondiente según su rol
            switch(usuario.getRol().getId()) {
                case 2: return "redirect:/zonal";
                case 3: return "redirect:/agente";
                case 4: return "redirect:/usuario";
                default: return "redirect:/admin";
            }
        } else {
            attr.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/adminzonal";
        }
    }

    /*@GetMapping("stopImpersonation")
    public String stopImpersonation(HttpSession session, RedirectAttributes attr) {
        Integer originalUserId = (Integer) session.getAttribute("originalUserId");

        if (originalUserId != null) {
            session.setAttribute("userId", originalUserId);
            session.removeAttribute("originalUserId");
            session.setAttribute("userRole", 1); // Asignar el rol de SuperAdmin (según tu lógica de roles)

            attr.addFlashAttribute("mensajeExito", "Has vuelto a tu cuenta de SuperAdmin.");
        } else {
            attr.addFlashAttribute("error", "No estás en modo de suplantación.");
        }

        return "redirect:/admin";
    }*/
    @PostMapping("/revertImpersonation")
    public String revertImpersonation(HttpSession session) {
        // Restaurar al usuario original (SuperAdmin) desde la sesión
        session.setAttribute("usuario", session.getAttribute("originalUser"));
        session.removeAttribute("originalUser");

        return "redirect:/admin";
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
            emailService.enviarCorreo(
                    usuario.getEmail(),
                    "Solicitud Aprobada",
                    "Estimado/a " + usuario.getNombre() +
                            ",\n\nTu solicitud para convertirte en agente ha sido aprobada. Ahora puedes acceder a las funciones asignadas como Agente.\n\nSaludos,\nEquipo de Dogless"
            );
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
            Usuario usuario = solicitud.getUsuario();
            emailService.enviarCorreo(
                    usuario.getEmail(),
                    "Solicitud Denegada",
                    "Estimado/a " + usuario.getNombre() +
                            ",\n\nTu solicitud para convertirte en agente ha sido rechazada por el siguiente motivo:\n\n" +
                            comentario + "\n\nSi tienes alguna duda, por favor contáctanos.\n\nSaludos,\nEquipo de Dogless"
            );
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
        model.addAttribute("listaProveedores", proveedorRepository.findByProveedoresActivos());
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
            attr.addFlashAttribute("mensajeExito", "Proveedor creado exitosamente");
        }
        return "redirect:/admin/proveedores";
    }




    //Vista de productos
    @GetMapping("/productos")
    public String listaProductos(Model model, @RequestParam(required = false) String zona) {
        model.addAttribute("listaProductosCompleto", productRepository.ProductosCompleto());
        //model.addAttribute("listaProductos", productRepository.findByBorrado(1));
        //return "admin/productos";
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
        producto.setCategoria(null);
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
    public String guardarProducto(@ModelAttribute @Valid ProductoForm productoForm, BindingResult result, RedirectAttributes attr) {
        if (productoForm.getCostoenvio() == null) {
            attr.addFlashAttribute("error", "El costo de envío no puede estar vacío");
            return "redirect:/admin/nuevoProducto";
        }
        // Validación adicional para valores no permitidos
        if (productoForm.getPrecio() < 0 || productoForm.getCostoenvio() < 0) {
            attr.addFlashAttribute("error", "El precio o costo de envío no pueden ser negativos");
            return "redirect:/admin/nuevoProducto";
        }
        List<String> categoriasValidas = Arrays.asList("Laptop", "Celular", "Periferico", "Almacenamiento", "Electrodoméstico");
        if (!categoriasValidas.contains(productoForm.getCategoria())) {
            attr.addFlashAttribute("error", "La categoría seleccionada no es válida");
            return "redirect:/admin/nuevoProducto";
        }
        if (result.hasErrors()) {
            attr.addFlashAttribute("error", "Hay errores en el formulario");
            return "redirect:/admin/nuevoProducto";
        }
        Producto producto = new Producto();
        producto.setNombre(productoForm.getNombre());
        System.out.println("Valor de categoría recibido: " + productoForm.getCategoria());
        producto.setCategoria(productoForm.getCategoria());
        Optional<Proveedor> optProducto = proveedorRepository.findById(productoForm.getIdproveedor());
        if (optProducto.isPresent()) {
            Proveedor proveedor = optProducto.get();
            producto.setProveedor(proveedor);  // Asignar el proveedor al producto
        } else {
            attr.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/admin/productos";
        }

        producto.setPrecio(productoForm.getPrecio());
        producto.setModelos(productoForm.getModelos());
        producto.setCostoenvio(productoForm.getCostoenvio());
        producto.setColores(productoForm.getColores());
        if (!productoForm.getImagenprod().isEmpty()) {
            try {
                byte[] imagenEnBytes = productoForm.getImagenprod().getBytes();
                producto.setImagenprod(imagenEnBytes);
            } catch (IOException e) {
                e.printStackTrace(); // Manejar la excepción de manera adecuada
            }
        }
        producto.setDescripcion(productoForm.getDescripcion());
        producto.setAprobado("Pendiente");

        producto.setBorrado(1);
        productRepository.save(producto);

        attr.addFlashAttribute("mensajeExito", "Producto creado exitosamente");
        return "redirect:/admin/productos";
    }
    /*@PostMapping("/deleteproducto")
    public String borrarProducto(@RequestParam("id") Integer id, @RequestParam("idZona") Integer idZona, RedirectAttributes attr) {
        Optional<Stockproducto> optStockProducto = stockProductoRepository.findByIdproductosAndIdzonas(id, idZona);

        if (optStockProducto.isPresent()) {
            Stockproducto stockProducto = optStockProducto.get();
            stockProducto.setBorrado(0);  // Cambia el borrado a 0 para hacer el borrado lógico
            stockProductoRepository.save(stockProducto);
            attr.addFlashAttribute("mensajeExito", "Producto borrado exitosamente para la zona seleccionada.");
        } else {
            attr.addFlashAttribute("error", "Producto no encontrado en la zona seleccionada.");
        }

        return "redirect:/admin/productos";
    }

     */
    @PostMapping("/deleteproducto")
    public String borrarProducto(@RequestParam("id") Integer id, @RequestParam("idZona") Integer idZona, RedirectAttributes attr) {
        Optional<ProductoZona> optProductoZona = stockProductoRepository.findByStockproductoIdproductosAndStockproductoIdzonas(id, idZona);

        if (optProductoZona.isPresent()) {
            ProductoZona productoZona = optProductoZona.get();
            productoZona.setBorrado(0);  // Cambia el campo borrado a 0 para hacer el borrado lógico
            stockProductoRepository.save(productoZona);
            attr.addFlashAttribute("mensajeExito", "Producto borrado exitosamente para la zona seleccionada.");
        } else {
            attr.addFlashAttribute("error", "Producto no encontrado en la zona seleccionada.");
        }

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
    @PostMapping("/aprobarprod")
    public String aprobar(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
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
                redirectAttributes.addFlashAttribute("mensajeExito", "La solicitud ha sido aprobada.");

                return "redirect:/admin/productosPendientes";

            }
        }
        // Redirige si la reposición no se encuentra o el producto no está presente
        return "redirect:/admin";
    }

    @PostMapping("/rechazarprod")
    public String rechazar(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        // Busca la reposición por ID
        Reposicion reposicion = reposicionesRepository.findById(id).orElse(null);

        if (reposicion != null && reposicion.getAprobar()==null) {
            Optional<Producto> producto = productRepository.findById(reposicion.getProducto().getId());
            if (producto.isPresent()) {
                // Actualiza el atributo 'rechazado' de la reposición
                reposicion.setAprobar("rechazado");
                reposicionesRepository.save(reposicion); // Guarda los cambios en la base de datos
                redirectAttributes.addFlashAttribute("mensajeExito", "La solicitud ha sido rechazada.");
                return "redirect:/admin/productosPendientes";
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
        //Parte Superior
        Integer agentesTotales = usuarioRepository.contarAgentes(); // Cambia a tu lógica para obtener este valor
        model.addAttribute("agentesTotales", agentesTotales);
        Integer usuariosBaneados = usuarioRepository.contarBaneados(); // Cambia a tu lógica para obtener este valor
        model.addAttribute("usuariosBaneados", usuariosBaneados);
        Integer proveedoresBaneados = usuarioRepository.contarProveedoresBaneados(); // Cambia a tu lógica para obtener este valor
        model.addAttribute("proveedoresBaneados", proveedoresBaneados);
        //2da gráfica
        Integer usuariosActivos = usuarioRepository.usuariosActivos();
        model.addAttribute("usuariosActivos", usuariosActivos);
        Integer usuariosInactivos = usuarioRepository.usuariosInactivos();
        model.addAttribute("usuariosInactivos", usuariosInactivos);
        //3ra gráfica
        List<ProveedorDTO> proveedoresMasSolicitados = proveedorRepository.findTop10TiendasMasSolicitadas();
        model.addAttribute("proveedoresMasSolicitados", proveedoresMasSolicitados);
        System.out.println("*****");

        for (ProveedorDTO proveedor : proveedoresMasSolicitados) {
            System.out.println("Tienda: " + proveedor.getTienda() + ", Total Pedidos: " + proveedor.getTotalPedidos());
        }



        // Supongamos que Lista_Ordenes_Por_Mes es la lista de todas las órdenes por mes.
        List<OrdenDTO> Lista_Ordenes_Por_Mes = ordenRepository.findCantidadOrdenesPorMes();

        // Listas para almacenar las cantidades por semestre
        List<Integer> ListaCantidadPrimerSemestre = new ArrayList<>();
        List<Integer> ListaCantidadSegundoSemestre = new ArrayList<>();

        // Recorrer las órdenes y asignarlas a los semestres correspondientes
        for (OrdenDTO orden : Lista_Ordenes_Por_Mes) {
            Integer mes = orden.getmes();  // Obtener el mes de la orden
            Integer cantidad = orden.getCantidad();  // Obtener la cantidad de la orden

            // Verificar si el mes corresponde al primer semestre (enero a junio)
            if (mes >= 1 && mes <= 6) {
                ListaCantidadPrimerSemestre.add(cantidad);
            }
            // Si es del segundo semestre (julio a diciembre)
            else if (mes >= 7 && mes <= 12) {
                ListaCantidadSegundoSemestre.add(cantidad);
            }
        }

        // Pasar las listas al modelo
        model.addAttribute("ListaCantidadPrimerSemestre", ListaCantidadPrimerSemestre);
        model.addAttribute("ListaCantidadSegundoSemestre", ListaCantidadSegundoSemestre);



        //6ta gráfica
        //Siguiendo el ejemplo se envían los demas parámetros-->
        Integer norte = ordenRepository.findOrdenesByZona(1);
        model.addAttribute("norte", norte);
        System.out.println("norte: " + norte);
        Integer sur = ordenRepository.findOrdenesByZona(2);
        model.addAttribute("sur", sur);
        Integer este = ordenRepository.findOrdenesByZona(3);
        model.addAttribute("este", este);
        Integer oeste = ordenRepository.findOrdenesByZona(4);
        model.addAttribute("oeste", oeste);

        List<ProductoDTO> lista_proveedorestop5 = productRepository.obtenerTop5ProveedoresConReposicionesYCalificaciones();

        // Crear listas para etiquetas y datos
        List<String> etiquetasProveedores = new ArrayList<>();
        List<Integer> reposicionesAprobadas = new ArrayList<>();
        List<Double> valoracionProveedores = new ArrayList<>();

        // Llenar las listas con los datos de cada proveedor
        for (ProductoDTO proveedor : lista_proveedorestop5) {
            String nombreCompleto = proveedor.getProveedorNombre() + " " + proveedor.getProveedorApellido();
            etiquetasProveedores.add(nombreCompleto);
            reposicionesAprobadas.add(proveedor.getReposicionesAprobadas());
            valoracionProveedores.add(proveedor.getPromedioCalificacionProveedor());
        }

        // Añadir los datos al modelo para pasarlos a la vista
        model.addAttribute("etiquetasProveedores", etiquetasProveedores);
        model.addAttribute("reposicionesAprobadas", reposicionesAprobadas);
        model.addAttribute("valoracionProveedores", valoracionProveedores);




        List<ProductoDTO> lista_proveedorespeorestop3 = productRepository.obtenerTop3ProveedoresConPeoresValoraciones();

        // Crear listas para etiquetas y datos
        List<String> etiquetasProveedoresPeoresValorados = new ArrayList<>();
        List<Double> valoracionProveedoresPeores = new ArrayList<>();
        List<Integer> cantidadMalosComentarios = new ArrayList<>();

        // Llenar las listas con los datos de cada proveedor con peores valoraciones
        for (ProductoDTO proveedor : lista_proveedorespeorestop3) {
            String nombreCompleto = proveedor.getProveedorNombre() + " " + proveedor.getProveedorApellido();
            etiquetasProveedoresPeoresValorados.add(nombreCompleto);
            valoracionProveedoresPeores.add(proveedor.getPromedioCalificacionProveedor());
            cantidadMalosComentarios.add(proveedor.getComentariosNegativosProveedor());
        }

        // Añadir los datos al modelo para pasarlos a la vista
        model.addAttribute("etiquetasProveedoresPeoresValorados", etiquetasProveedoresPeoresValorados);
        model.addAttribute("valoracionProveedoresPeores", valoracionProveedoresPeores);
        model.addAttribute("cantidadMalosComentarios", cantidadMalosComentarios);

        List<ProductoDTO> lista_producto_mas_importados = productRepository.obtenerTop10ProductosMasImportados();
        List<String> etiquetasProductosMasImportado = new ArrayList<>();
        List<Integer> cantidadProductosMasImportado = new ArrayList<>();

        for (ProductoDTO producto : lista_producto_mas_importados) {
            etiquetasProductosMasImportado.add(producto.getNombre());
            cantidadProductosMasImportado.add(producto.getCantidadAprobada());
        }

        model.addAttribute("etiquetasProductosMasImportado", etiquetasProductosMasImportado);
        model.addAttribute("cantidadProductosMasImportado", cantidadProductosMasImportado);

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
