package com.example.springdogless.services;

import com.example.springdogless.Repository.Detallesorden2;
import com.example.springdogless.Repository.OrdenRepository;
import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.controllers.UsuarioController;
import com.example.springdogless.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

@Service
public class ChatbotService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private List<JsonNode> menuOpciones;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private Detallesorden2 detallesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Map<String, String> estadosUsuario = new HashMap<>();

    @Value("${huggingface.apiKey}")
    private String apiKey;

    @Value("${huggingface.model}")
    private String model;

    @Value("${chatbot.horario.inicio}")
    private String horarioInicio;

    @Value("${chatbot.horario.fin}")
    private String horarioFin;

    @Autowired
    private UsuarioController usuarioController;

    private Map<String, List<Detalleorden>> productosSesion = new HashMap<>();

    private String estadoActual = "MENU"; // Estado inicial
    private Map<String, Map<String, String>> datosUsuario = new HashMap<>();

    private Map<String, Timestamp> ultimaActualizacionPorUsuario = new HashMap<>();

    private final Map<String, List<Detalleorden>> productosPorSesion = new HashMap<>();
    private final Map<String, String> estadoSesionUsuario = new HashMap<>();


    public ChatbotService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        cargarMenuDesdeJson();
    }

    //Ojo con esto
    public String getMensajeInicial() {
        String usuarioActual = getUsuarioActual();
        estadoActual = "MENU"; // Reinicia el estado actual a "MENU"
        estadosUsuario.clear(); // Limpia los estados de los usuarios
        datosUsuario.clear(); // Limpia los datos guardados de los usuarios

        // Registrar el inicio de la conversación para el usuario actual
        ultimaActualizacionPorUsuario.put(usuarioActual, new Timestamp(System.currentTimeMillis()));

        return manejarMenuPrincipal(""); // Devuelve el menú inicial
    }

    private void cargarMenuDesdeJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/preguntas_respuestas.json")) {
            JsonNode root = objectMapper.readTree(inputStream);
            menuOpciones = new ArrayList<>();
            root.get("menu_opciones").forEach(menuOpciones::add);
        } catch (IOException e) {
            e.printStackTrace();
            menuOpciones = new ArrayList<>();
        }
    }

    // Método para actualizar el timestamp de la última actualización
    public void actualizarUltimaActualizacion(String userId) {
        ultimaActualizacionPorUsuario.put(userId, new Timestamp(System.currentTimeMillis()));
    }

    public String procesarMensaje(String mensaje) {

        String sessionId = getUsuarioActual();
        if (!estadoSesionUsuario.containsKey(sessionId)) {
            estadoSesionUsuario.put(sessionId, "MENU");
            productosPorSesion.put(sessionId, new ArrayList<>());
        }


        if (mensaje.equalsIgnoreCase("reiniciar")) {
            estadoActual = "MENU";
            estadosUsuario.clear(); // Limpia los estados de los usuarios
            datosUsuario.clear(); // Limpia los datos guardados de los usuarios
            return manejarMenuPrincipal("");
        }

        // Siempre inicia con el menú principal
        if (estadoActual.equals("MENU") || mensaje.equalsIgnoreCase("regresar")) {
            return manejarMenuPrincipal(mensaje);
        }

        // Según el estado actual, maneja la interacción
        switch (estadoActual) {
            case "CONSULTA":
                return manejarConsulta(mensaje);
            case "ORDEN":
                return procesarFlujoCompra("defaultUser", mensaje);
            case "RECLAMO":
                return manejarReclamaciones();
            default:
                return "Ocurrió un error. Por favor, reinicia la conversación.";
        }
    }


    private String manejarMenuPrincipal(String mensaje) {
        // Mensaje introductorio en formato HTML
        String introduccion = """
        Hola, soy Dogbot. ¿En qué puedo ayudarte? Elige una de las siguientes opciones:<br>
        1. Quiero hacer una consulta<br>
        2. Quiero generar una orden de compra<br>
        3. Quiero generar un reclamo<br>
        """;

        // Procesar la selección del usuario
        switch (mensaje) {
            case "1":
                estadoActual = "CONSULTA";
                return "Haz tu consulta a continuación:";
            case "2":
                estadoActual = "ORDEN"; // Cambiar el estado global al flujo de compra
                estadosUsuario.put("defaultUser", "inicio"); // Inicia el flujo de compra
                return "Por favor, ingrese el nombre del producto que desea comprar.";
            case "3":
                estadoActual = "RECLAMO";
                return "A continuación escribe tu reclamo:<br>" + manejarReclamaciones();
            default:
                // Si el mensaje no corresponde a una opción válida, mantén el menú inicial
                estadoActual = "MENU";
                return introduccion; // Devuelve el menú en HTML
        }
    }



    private String manejarConsulta(String mensaje) {
        if (mensaje.equalsIgnoreCase("regresar")) {
            estadoActual = "MENU";
            return manejarMenuPrincipal(mensaje);
        }

        // Consultar a la IA para interpretar el mensaje
        String consultaInterpretada = callHuggingFaceAPI(mensaje);

        // Buscar coincidencia en el archivo JSON usando la interpretación de la IA
        String respuestaPredefinida = buscarRespuestaEnJson(consultaInterpretada);
        if (respuestaPredefinida != null) {
            return respuestaPredefinida + "\n\nEscribe 'regresar' para volver al menú principal.";
        }

        // Si no encuentra coincidencia, devuelve un mensaje indicando ambigüedad
        return "Lo siento, no entiendo tu pregunta. Por favor, intenta reformularla o escribe 'regresar' para volver al menú principal.";
    }



    private String buscarRespuestaEnJson(String mensaje) {
        if (menuOpciones == null || menuOpciones.isEmpty()) {
            return null;
        }

        // Normalizar el mensaje del usuario
        String mensajeNormalizado = mensaje.toLowerCase()
                .replaceAll("[áäà]", "a")
                .replaceAll("[éëè]", "e")
                .replaceAll("[íïì]", "i")
                .replaceAll("[óöò]", "o")
                .replaceAll("[úüù]", "u")
                .replaceAll("[^a-z0-9\\s]", "")
                .trim();

        // Dividir el mensaje en palabras
        String[] palabrasUsuario = mensajeNormalizado.split("\\s+");

        for (JsonNode opcion : menuOpciones) {
            if (opcion.has("respuestas")) {
                for (JsonNode respuesta : opcion.get("respuestas")) {
                    String clave = respuesta.get("clave").asText().toLowerCase()
                            .replaceAll("[áäà]", "a")
                            .replaceAll("[éëè]", "e")
                            .replaceAll("[íïì]", "i")
                            .replaceAll("[óöò]", "o")
                            .replaceAll("[úüù]", "u")
                            .replaceAll("[^a-z0-9\\s]", "")
                            .trim();

                    // Dividir la clave en palabras
                    String[] palabrasClave = clave.split("\\s+");

                    // Verificar similitud entre palabras
                    boolean coincidenciaEncontrada = false;
                    for (String palabraUsuario : palabrasUsuario) {
                        for (String palabraClave : palabrasClave) {
                            // Verificar coincidencia exacta
                            if (palabraUsuario.equals(palabraClave)) {
                                coincidenciaEncontrada = true;
                                break;
                            }

                            // Verificar palabras en singular/plural
                            if (palabraUsuario.endsWith("s") && palabraClave.equals(palabraUsuario.substring(0, palabraUsuario.length() - 1)) ||
                                    palabraClave.endsWith("s") && palabraUsuario.equals(palabraClave.substring(0, palabraClave.length() - 1))) {
                                coincidenciaEncontrada = true;
                                break;
                            }

                            // Calcular distancia de Levenshtein para palabras similares
                            if (calcularDistanciaLevenshtein(palabraUsuario, palabraClave) <= 2) {
                                coincidenciaEncontrada = true;
                                break;
                            }
                        }
                        if (coincidenciaEncontrada) break;
                    }

                    if (coincidenciaEncontrada) {
                        return respuesta.get("respuesta").asText();
                    }
                }
            }
        }

        return null;
    }

    // Método auxiliar para calcular la distancia de Levenshtein
    private int calcularDistanciaLevenshtein(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }




    private String getMenuOpciones() {
        StringBuilder menu = new StringBuilder();
        for (JsonNode opcion : menuOpciones) {
            menu.append("\n").append(opcion.get("opcion").asText()).append(". ").append(opcion.get("descripcion").asText());
        }
        return menu.toString();
    }

    private String manejarReclamaciones() {
        return "Recuerda que también puedes acceder a nuestra sección \"Libro de Reclamaciones\" de la barra lateral.";
    }

    private String generarOrdenDeCompra() {
        String numeroOrden = "ORD-" + System.currentTimeMillis();
        return String.format("Tu orden de compra ha sido generada exitosamente. Número de orden: %s.", numeroOrden);
    }

    public String manejarCompraDesdeChatbot(Integer idProducto, Integer idUsuario, Integer cantidad) {
        try {
            // Validaciones
            if (idUsuario == null || idUsuario <= 0) {
                return "Error: Usuario no válido.";
            }
            if (idProducto == null || idProducto <= 0) {
                return "Error: Producto no válido.";
            }
            if (cantidad == null || cantidad <= 0) {
                return "Error: La cantidad debe ser mayor a 0.";
            }

            // Obtener el producto
            Optional<Producto> productoOptional = productRepository.findById(idProducto);
            if (!productoOptional.isPresent()) {
                return "Error: Producto no encontrado.";
            }
            Producto producto = productoOptional.get();

            // Agregar el producto a la sesión
            String userId = String.valueOf(idUsuario);
            List<Detalleorden> detalles = productosSesion.computeIfAbsent(userId, k -> new ArrayList<>());

            // Verificar si el producto ya está en el carrito
            boolean productoEncontrado = false;
            for (Detalleorden detalle : detalles) {
                if (detalle.getProducto().getId().equals(idProducto)) {
                    detalle.setCantidad(detalle.getCantidad() + cantidad);
                    detalle.setSubtotal(detalle.getPreciounitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                    productoEncontrado = true;
                    break;
                }
            }

            // Si no está en el carrito, agregarlo
            if (!productoEncontrado) {
                Detalleorden nuevoDetalle = new Detalleorden();
                nuevoDetalle.setProducto(producto);
                nuevoDetalle.setCantidad(cantidad);
                nuevoDetalle.setPreciounitario(BigDecimal.valueOf(producto.getPrecio()));
                nuevoDetalle.setSubtotal(BigDecimal.valueOf(producto.getPrecio()).multiply(BigDecimal.valueOf(cantidad)));
                detalles.add(nuevoDetalle);
            }

            // Asociar el producto con la orden activa en la base de datos
            Orden orden = ordenRepository.findOrdenEstadoCreado(idUsuario);
            if (orden == null) {
                throw new RuntimeException("No se encontró una orden activa para el usuario.");
            }

            Detalleorden detalleEnDB = detallesRepository.findByIdordenAndIdproducto(orden.getId(), idProducto)
                    .orElse(null);
            if (detalleEnDB != null) {
                detalleEnDB.setCantidad(detalleEnDB.getCantidad() + cantidad);
                detalleEnDB.setSubtotal(detalleEnDB.getPreciounitario().multiply(BigDecimal.valueOf(detalleEnDB.getCantidad())));
                detallesRepository.save(detalleEnDB);
            } else {
                Detalleorden nuevoDetalleDB = new Detalleorden();
                nuevoDetalleDB.setOrden(orden);
                nuevoDetalleDB.setProducto(producto);
                nuevoDetalleDB.setCantidad(cantidad);
                nuevoDetalleDB.setPreciounitario(BigDecimal.valueOf(producto.getPrecio()));
                nuevoDetalleDB.setSubtotal(BigDecimal.valueOf(producto.getPrecio()).multiply(BigDecimal.valueOf(cantidad)));
                detallesRepository.save(nuevoDetalleDB);
            }

            return String.format("Producto %s añadido exitosamente al carrito.", producto.getNombre());
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al añadir el producto. Inténtalo nuevamente.";
        }
    }






    private String callHuggingFaceAPI(String mensaje) {
        String url = String.format("https://api-inference.huggingface.co/models/%s", model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = String.format("Interpreta la pregunta del usuario: %s", mensaje);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("inputs", prompt);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            if (responseBody.isArray() && responseBody.size() > 0) {
                return responseBody.get(0).path("generated_text").asText().trim();
            } else {
                return mensaje; // Si la IA no genera una respuesta, usar el mensaje original
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mensaje; // En caso de error, usar el mensaje original
        }
    }

    public List<Orden> obtenerHistorialDeOrdenes(int userId) {
        return ordenRepository.findByUsuarioIdAndEstado(userId, "Confirmada");
    }


    public String procesarFlujoCompra(String userId, String mensaje) {

        // Obtener el usuario autenticado desde el contexto de seguridad
        String username = getUsuarioActual(); // Este obtiene el email si es tu identificador de autenticación
        if ("anonymousUser".equals(username)) {
            return "Error: Debes estar autenticado para realizar una compra.";
        }

        // Buscar al usuario en la base de datos usando el método findByEmail
        Usuario usuario = usuarioRepository.findByEmail(username); // Devuelve directamente Usuario, no Optional
        if (usuario == null) {
            return "Error: No se encontró el usuario en el sistema.";
        }

        // Cargar datos del usuario en el mapa
        userId = String.valueOf(usuario.getId());
        Map<String, String> datos = datosUsuario.computeIfAbsent(userId, k -> new HashMap<>());
        datos.put("nombre", Optional.ofNullable(usuario.getNombre()).orElse("No especificado"));
        datos.put("apellido", Optional.ofNullable(usuario.getApellido()).orElse("No especificado"));
        datos.put("zona", Optional.ofNullable(usuario.getZona()).map(Zona::getNombre).orElse("No especificado"));
        datos.put("distrito", Optional.ofNullable(usuario.getDistrito()).map(Distrito::getDistrito).orElse("No especificado"));
        datos.put("telefono", Optional.ofNullable(usuario.getTelefono()).orElse("No especificado"));
        datos.put("correo", Optional.ofNullable(usuario.getEmail()).orElse("No especificado"));
        datos.put("direccion", Optional.ofNullable(usuario.getDireccion()).orElse("No especificado"));

        // Obtener el estado actual del usuario
        String estadoActual = estadosUsuario.getOrDefault(userId, "inicio");

        System.out.println("Usuario autenticado: " + usuario.getId());

        switch (estadoActual) {
            case "inicio":
                crearOrdenEnCurso(Integer.parseInt(userId));

                estadosUsuario.put(userId, "esperandoProducto");

            case "esperandoProducto":
                Producto producto = productRepository.findByNombre(mensaje);
                if (producto == null) {
                    return "No reconozco el nombre del producto. Por favor, intenta nuevamente.";
                }
                // Guardar detalles del producto en los datos del usuario
                datos.put("productoId", String.valueOf(producto.getId()));
                datos.put("productoNombre", producto.getNombre());
                datos.put("productoPrecio", String.valueOf(producto.getPrecio()));
                datos.put("productoDescripcion", producto.getDescripcion());

                estadosUsuario.put(userId, "validarProducto");
                return String.format("""
                <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
                    <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Producto encontrado</h4>
                    <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
                    <p style="margin: 5px 0; line-height: 1.2;"><strong>Nombre:</strong></p>
                    <p style="margin: 5px 0; line-height: 1.2;">%s</p>
                    <p style="margin: 5px 0; line-height: 1.2;"><strong>Descripción:</strong></p>
                    <p style="margin: 5px 0; line-height: 1.2;">%s</p>
                    <p style="margin: 5px 0; line-height: 1.2;"><strong>Precio:</strong></p>
                    <p style="margin: 5px 0; line-height: 1.2;">S/. %s</p>
                    <hr style="border: 0; border-top: 1px solid #ccc; margin-top: 10px; margin-bottom: 10px;">
                    <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Es este el producto que desea añadir?</p>
                    <div style="text-align: center;">
                        <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                        <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
                    </div>
                </div>
                """, producto.getNombre(), producto.getDescripcion(), producto.getPrecio());


            case "validarProducto":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "esperandoCantidad");
                    return "Ingrese la cantidad que desea comprar.";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "esperandoProducto");
                    return "Por favor, ingrese el nombre del producto que desea comprar.";
                } else {
                    return "Por favor, seleccione 'sí' o 'no'.";
                }

            case "esperandoCantidad":
                try {
                    int cantidad = Integer.parseInt(mensaje.trim());
                    if (cantidad <= 0) {
                        return "Por favor, ingrese una cantidad válida mayor a 0.";
                    }
                    // Actualiza solo la cantidad actual sin duplicar datos
                    datos.put("cantidad", String.valueOf(cantidad));

                    // Procesar la compra con la cantidad actual
                    Integer idProducto = Integer.parseInt(datos.get("productoId"));
                    String resultadoCompra = manejarCompraDesdeChatbot(
                            idProducto,
                            Integer.parseInt(userId),
                            cantidad
                    );

                    estadosUsuario.put(userId, "otroProducto");
                    return String.format("""
        <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
            <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Cantidad añadida exitosamente</h4>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
            <p style="margin: 5px 0; line-height: 1.2;"><strong>%s</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">Cantidad: %d</p>
            <p style="margin: 5px 0; line-height: 1.2;">Subtotal: S/. %.2f</p>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-top: 10px; margin-bottom: 10px;">
            <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Quiere ingresar otro producto?</p>
            <div style="text-align: center;">
                <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
            </div>
        </div>
        """, datos.get("productoNombre"), cantidad, cantidad * Double.parseDouble(datos.get("productoPrecio")));
                } catch (NumberFormatException e) {
                    return "Por favor, ingrese un número válido para la cantidad.";
                }




            case "otroProducto":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "esperandoProducto");
                    return "Por favor, ingrese el nombre del próximo producto que desea comprar.";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "confirmarCompra");
                    return mostrarDetallesCompra(userId);
                } else {
                    return """
                <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
                    <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">¿Quiere ingresar otro producto?</h4>
                    <div style="text-align: center;">
                        <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                        <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
                    </div>
                </div>
                """;
                }


            case "confirmarCompra":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "modificarDireccion");
                    return "Ingrese la nueva dirección:";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "ingresarTarjeta");
                    return "A continuación, ingrese los siguientes datos:<br> Número de tarjeta:";
                } else {
                    return "¿Quiere modificar la dirección? <button>Sí</button> <button>No</button>";
                }

            case "modificarDireccion":
                if (mensaje.trim().isEmpty()) {
                    return "Por favor, ingrese una dirección válida.";
                } else {
                    datos.put("direccion", mensaje.trim());
                    estadosUsuario.put(userId, "ingresarTarjeta");
                    return "A continuación, ingrese los siguientes datos:<br> Número de tarjeta:";
                }

            case "ingresarTarjeta":
                if (mensaje.trim().isEmpty()) {
                    return "Por favor, ingrese el número de tarjeta.";
                } else {
                    datos.put("numeroTarjeta", mensaje.trim());
                    estadosUsuario.put(userId, "ingresarTitular");
                    return "Ingrese el nombre del titular:";
                }

            case "ingresarTitular":
                if (mensaje.trim().isEmpty()) {
                    return "Por favor, ingrese el nombre del titular.";
                } else {
                    datos.put("nombreTitular", mensaje.trim());
                    estadosUsuario.put(userId, "ingresarVencimiento");
                    return "Ingrese la fecha de vencimiento (MM/AA):";
                }

            case "ingresarVencimiento":
                if (mensaje.trim().isEmpty()) {
                    return "Por favor, ingrese la fecha de vencimiento.";
                } else {
                    datos.put("vencimiento", mensaje.trim());
                    estadosUsuario.put(userId, "ingresarCVV");
                    return "Ingrese el código CVV:";
                }

            case "ingresarCVV":
                if (mensaje.trim().isEmpty()) {
                    return "Por favor, ingrese el código CVV.";
                } else {
                    datos.put("cvv", mensaje.trim());
                    estadosUsuario.put(userId, "confirmarPago");
                    return """
            <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
                <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Confirmación de Pago</h4>
                <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
                <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Desea proceder con el pago?</p>
                <div style="text-align: center;">
                    <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                    <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
                </div>
            </div>
            """;
                }


            case "confirmarPago":
                if (mensaje.equalsIgnoreCase("sí")) {
                    // Crear la orden en la base de datos
                    try {
                        crearOrdenConProductos(userId);
                    } catch (Exception e) {
                        e.printStackTrace(); // Imprime el stack trace completo para análisis
                        System.out.println("Error horror: " + e.getMessage()); // Detalle adicional del error
                        return "Ocurrió un error al procesar el pago. Por favor, intente nuevamente.";
                    }

                    estadosUsuario.put(userId, "confirmarPDF");
                    return procesarPagoYMostrarResumen(userId) + """
        <br>
        <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
            <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Generar PDF</h4>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
            <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Desea generar un PDF con el resumen de su compra?</p>
            <div style="text-align: center;">
                <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
            </div>
        </div>
        """;
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "MENU");
                    return """
        <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
            <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Pago Cancelado</h4>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
            <p style="text-align: center; margin: 5px 0; line-height: 1.2;">El pago ha sido cancelado. Volviendo al menú principal.</p>
        </div>
        """ + manejarMenuPrincipal("");
                } else {
                    return """
        <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
            <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Confirmación de Pago</h4>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
            <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Desea proceder con el pago?</p>
            <div style="text-align: center;">
                <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
            </div>
        </div>
        """;
                }




            case "confirmarPDF":
                if (mensaje.equalsIgnoreCase("sí")) {
                    // Generar un enlace de descarga único
                    String downloadLink = "/descargarPDF?userId=" + userId;
                    estadosUsuario.put(userId, "MENU");
                    return "PDF generado. <a href='" + downloadLink + "'>Descargar Resumen de Compra</a><br><br>" + manejarMenuPrincipal("");
                }

            default:
                return "No entiendo tu solicitud. Por favor, intenta nuevamente.";
        }
    }

    @GetMapping("/descargarPDF")
    public ResponseEntity<byte[]> descargarPDF(String userId) {
        byte[] pdfContent = generarPDFResumenCompra(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Resumen_Compra.pdf")
                .body(pdfContent);
    }

    @Transactional
    public void crearOrdenConProductos(String userId) {
        Usuario usuario = usuarioRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear la nueva orden y guardarla primero
        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuario(usuario);
        nuevaOrden.setFecha(new java.sql.Date(System.currentTimeMillis()));
        nuevaOrden.setDireccionenvio(datosUsuario.get(userId).get("direccion"));
        nuevaOrden.setMetodopago("Tarjeta"); // Esto puede ajustarse dinámicamente
        nuevaOrden.setTotal(BigDecimal.ZERO);

        // Persistir la orden para obtener su ID
        nuevaOrden = ordenRepository.save(nuevaOrden);

        // Calcular el total y añadir los detalles de la orden
        BigDecimal total = BigDecimal.ZERO;
        List<Detalleorden> detalles = productosSesion.get(userId);
        if (detalles != null) {
            for (Detalleorden detalle : detalles) {
                detalle.setOrden(nuevaOrden); // Asociar la orden persistida
                total = total.add(detalle.getSubtotal());
                detallesRepository.save(detalle); // Guardar cada detalle en la BD
            }
        }

        // Actualizar el total de la orden y guardarla nuevamente
        nuevaOrden.setTotal(total);
        ordenRepository.save(nuevaOrden);
    }


    @Transactional
    protected void crearOrdenEnCurso(int userId) {
        System.out.println("Iniciando creación de orden para usuario ID: " + userId);

        // Verificar si ya existe una orden activa
        Orden ordenExistente = ordenRepository.findOrdenEstadoCreado(userId);
        if (ordenExistente != null) {
            System.out.println("Ya existe una orden activa para el usuario ID: " + userId);
            return;
        }

        // Crear una nueva orden si no existe ninguna activa
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuario(usuario);
        List<String> estadosValidos = Arrays.asList("Creado", "En Validación", "En Proceso", "Arribo al País", "En Aduanas", "En Ruta", "Recibido", "Cancelado");

        if (!estadosValidos.contains("Creado")) { // Cambia "Creado" al estado que quieras asignar
            throw new IllegalArgumentException("Estado no válido: Creado");
        }
        nuevaOrden.setEstado("Creado");

        System.out.println("Estado de la orden u.U: " + nuevaOrden.getEstado());


        nuevaOrden.setFecha(new java.sql.Date(System.currentTimeMillis()));

        ordenRepository.save(nuevaOrden);

        System.out.println("Orden creada y guardada con ID: " + nuevaOrden.getId());
    }

    @Transactional
    public void cancelarOrdenesActivas(int userId) {
        List<Orden> ordenesActivas = ordenRepository.findByUsuarioIdAndEstado(userId, "Creado");
        for (Orden orden : ordenesActivas) {
            orden.setEstado("Cancelada"); // Cambia el estado a 'Cancelada'
            ordenRepository.save(orden);
        }
    }




    public byte[] generarPDFResumenCompra(String userId) {
        Map<String, String> datos = datosUsuario.get(userId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            // Agregar los mismos elementos de contenido que en el ejemplo anterior
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Resumen de Compra", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Espacio

            // Datos del usuario
            document.add(new Paragraph("Información del Cliente:"));
            document.add(new Paragraph("Nombre: " + datos.get("nombre") + " " + datos.get("apellido")));
            document.add(new Paragraph("Correo: " + datos.get("correo")));
            document.add(new Paragraph("Teléfono: " + datos.get("telefono")));
            document.add(new Paragraph("Dirección: " + datos.get("direccion")));

            document.add(new Paragraph(" ")); // Espacio

            // Detalles del producto
            document.add(new Paragraph("Detalles del Producto:"));
            document.add(new Paragraph("Producto: " + datos.get("productoNombre")));
            document.add(new Paragraph("Descripción: " + datos.get("productoDescripcion")));
            document.add(new Paragraph("Cantidad: " + datos.get("cantidad")));
            document.add(new Paragraph("Precio Unitario: S/. " + datos.get("productoPrecio")));

            // Calcular total
            int cantidad = Integer.parseInt(datos.get("cantidad"));
            double precioUnitario = Double.parseDouble(datos.get("productoPrecio"));
            double total = cantidad * precioUnitario;

            document.add(new Paragraph(" ")); // Espacio

            document.add(new Paragraph("Total de la Compra: S/. " + String.format("%.2f", total)));

            document.close();

            return baos.toByteArray();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private String manejarFlujoProducto(Map<String, String> datos, String mensaje) {
        Producto producto = productRepository.findByNombre(mensaje);
        if (producto == null) {
            return "No se encontró el producto. Por favor, intenta con otro nombre.";
        }

        datos.put("productoId", String.valueOf(producto.getId()));
        datos.put("productoNombre", producto.getNombre());
        datos.put("productoPrecio", producto.getPrecio().toString());

        return String.format("""
        Producto encontrado:<br>
        Nombre: %s<br>
        Precio: S/. %s<br>
        ¿Deseas continuar con la compra? <button onclick="sendMessage('sí')">Sí</button> <button onclick="sendMessage('no')">No</button>
        """, producto.getNombre(), producto.getPrecio());
    }

    // Método para obtener el usuario autenticado
    public String getUsuarioActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Devuelve el email del usuario autenticado
        } else {
            return principal.toString(); // Devuelve "anonymousUser" si no está autenticado
        }
    }




    private String mostrarDetallesCompra(String username) {
        Map<String, String> datos = datosUsuario.get(username);
        if (datos == null || datos.isEmpty()) {
            return "Error: No se pudieron cargar los datos del usuario.";
        }

        String nombre = datos.getOrDefault("nombre", "No especificado");
        String apellido = datos.getOrDefault("apellido", "No especificado");
        String zona = datos.getOrDefault("zona", "No especificado");
        String distrito = datos.getOrDefault("distrito", "No especificado");
        String telefono = datos.getOrDefault("telefono", "No especificado");
        String correo = datos.getOrDefault("correo", "No especificado");
        String direccion = datos.getOrDefault("direccion", "No especificado");

        return String.format("""
        <div style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 400px; font-family: Arial, sans-serif;">
            <h4 style="text-align: center; color: #333; margin-bottom: 10px; font-size: 16px; line-height: 1.2;">Detalles del usuario</h4>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;">
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Nombre:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Apellido:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Zona:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Distrito:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Número de teléfono:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Correo electrónico:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <p style="margin: 5px 0; line-height: 1.2;"><strong>Dirección:</strong></p>
            <p style="margin: 5px 0; line-height: 1.2;">%s</p>
            <hr style="border: 0; border-top: 1px solid #ccc; margin-top: 10px; margin-bottom: 10px;">
            <p style="text-align: center; margin: 5px 0; line-height: 1.2;">¿Quieres modificar la dirección?</p>
            <div style="text-align: center;">
                <button class="button" style="margin: 5px; padding: 8px 12px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('sí')">Sí</button>
                <button class="button no" style="margin: 5px; padding: 8px 12px; background-color: #f44336; color: white; border: none; border-radius: 4px; cursor: pointer;" onclick="sendMessage('no')">No</button>
            </div>
        </div>
        """, nombre, apellido, zona, distrito, telefono, correo, direccion);



    }


    private String procesarPagoYMostrarResumen(String usuarioActual) {
        // Obtener los productos comprados en la sesión
        List<Detalleorden> detallesSesion = productosSesion.getOrDefault(usuarioActual, new ArrayList<>());

        if (detallesSesion.isEmpty()) {
            return "No hay productos comprados en esta sesión.";
        }

        // Construir el resumen
        StringBuilder resumen = new StringBuilder();
        BigDecimal subtotal = BigDecimal.ZERO;

        resumen.append("<div style=\"border: 1px solid #ddd; padding: 15px; border-radius: 8px; max-width: 600px; font-family: Arial, sans-serif;\">");
        resumen.append("<h4 style=\"text-align: center; color: #333; margin-bottom: 10px; font-size: 18px; line-height: 1.4;\">Resumen de tu compra</h4>");
        resumen.append("<hr style=\"border: 0; border-top: 1px solid #ccc; margin-bottom: 10px;\">");
        resumen.append("<table style=\"width: 100%; border-collapse: collapse;\">");
        resumen.append("<thead style=\"background-color: #f4f4f4; text-align: left;\">");
        resumen.append("<tr>");
        resumen.append("<th style=\"padding: 8px; border: 1px solid #ddd;\">Producto</th>");
        resumen.append("<th style=\"padding: 8px; border: 1px solid #ddd;\">Cantidad</th>");
        resumen.append("<th style=\"padding: 8px; border: 1px solid #ddd;\">Precio Unitario</th>");
        resumen.append("<th style=\"padding: 8px; border: 1px solid #ddd;\">Total</th>");
        resumen.append("</tr>");
        resumen.append("</thead>");
        resumen.append("<tbody>");

        for (Detalleorden detalle : detallesSesion) {
            BigDecimal totalProducto = detalle.getPreciounitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            subtotal = subtotal.add(totalProducto);

            resumen.append("<tr>");
            resumen.append(String.format("<td style=\"padding: 8px; border: 1px solid #ddd;\">%s</td>", detalle.getProducto().getNombre()));
            resumen.append(String.format("<td style=\"padding: 8px; border: 1px solid #ddd; text-align: center;\">%d</td>", detalle.getCantidad()));
            resumen.append(String.format("<td style=\"padding: 8px; border: 1px solid #ddd; text-align: right;\">S/. %.2f</td>", detalle.getPreciounitario()));
            resumen.append(String.format("<td style=\"padding: 8px; border: 1px solid #ddd; text-align: right;\">S/. %.2f</td>", totalProducto));
            resumen.append("</tr>");
        }

        resumen.append("</tbody>");
        resumen.append("</table>");
        resumen.append("<hr style=\"border: 0; border-top: 1px solid #ccc; margin-top: 10px; margin-bottom: 10px;\">");

        // Calcular el costo total
        BigDecimal costoEnvio = new BigDecimal("12.00");
        BigDecimal total = subtotal.add(costoEnvio);

        resumen.append(String.format("<p style=\"text-align: right; font-weight: bold;\">Subtotal: S/. %.2f</p>", subtotal));
        resumen.append(String.format("<p style=\"text-align: right; font-weight: bold;\">Costo de envío: S/. %.2f</p>", costoEnvio));
        resumen.append(String.format("<p style=\"text-align: right; font-weight: bold; font-size: 16px;\">Total: S/. %.2f</p>", total));
        resumen.append("<hr style=\"border: 0; border-top: 1px solid #ccc; margin-top: 10px; margin-bottom: 10px;\">");
        resumen.append("<p style=\"text-align: center; margin: 10px 0; line-height: 1.6; font-size: 14px;\">Gracias por tu compra.</p>");
        resumen.append("</div>");

        return resumen.toString();
    }



    public boolean esFlujoDeCompra(String mensaje) {
        // Lógica para decidir si el mensaje es parte del flujo de compra
        return estadosUsuario.values().contains("esperandoProducto") ||
                estadosUsuario.values().contains("esperandoCantidad") ||
                estadosUsuario.values().contains("continuarCompra");
    }

    private void agregarProductoACarrito(int userId, int productoId, int cantidad) {
        // Lógica para añadir producto al carrito
        Orden orden = ordenRepository.findOrdenEstadoCreado(userId);
        if (orden == null) {
            orden = new Orden();
            orden.setUsuario(new Usuario());
            orden.setEstado("Creado");
            ordenRepository.save(orden);
        }

        Optional<Detalleorden> detalleExistente = detallesRepository.findByIdordenAndIdproducto(orden.getId(), productoId);
        if (detalleExistente.isPresent()) {
            Detalleorden detalle = detalleExistente.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
            detalle.setSubtotal(detalle.getPreciounitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            detallesRepository.save(detalle);
        } else {
            Detalleorden nuevoDetalle = new Detalleorden();
            nuevoDetalle.setProducto(productRepository.findById(productoId).get());
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPreciounitario(BigDecimal.valueOf(productoId));
            nuevoDetalle.setSubtotal(BigDecimal.valueOf(productoId * cantidad));
            detallesRepository.save(nuevoDetalle);
        }
    }

    private void confirmarCompra(int userId) {
        // Obtener la orden en estado "Creado"
        Orden orden = ordenRepository.findOrdenEstadoCreado(userId);

        if (orden == null) {
            throw new RuntimeException("No se encontró una orden en curso para confirmar.");
        }

        // Actualizar el estado de la orden
        orden.setEstado("Confirmada");
        orden.setFecha(new java.sql.Date(System.currentTimeMillis()));
        ordenRepository.save(orden);

        // Limpiar los productos de la sesión
        productosSesion.remove(String.valueOf(userId));
    }



}
