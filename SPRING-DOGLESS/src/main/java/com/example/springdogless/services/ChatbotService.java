package com.example.springdogless.services;

import com.example.springdogless.Repository.Detallesorden2;
import com.example.springdogless.Repository.OrdenRepository;
import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.controllers.UsuarioController;
import com.example.springdogless.entity.Detalleorden;
import com.example.springdogless.entity.Orden;
import com.example.springdogless.entity.Producto;
import com.example.springdogless.entity.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

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


    private String estadoActual = "MENU"; // Estado inicial
    private Map<String, Map<String, String>> datosUsuario = new HashMap<>();


    public ChatbotService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        cargarMenuDesdeJson();
    }

    //Ojo con esto
    public String getMensajeInicial() {
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


    public String procesarMensaje(String mensaje) {
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

    public String manejarCompraDesdeChatbot(Integer userId, Integer productoId, Integer cantidad) {
        try {
            // Validaciones básicas
            if (userId == null || userId <= 0) {
                return "Error: Usuario no válido.";
            }
            if (productoId == null || productoId <= 0) {
                return "Error: Producto no válido.";
            }
            if (cantidad == null || cantidad <= 0) {
                return "Error: Cantidad debe ser mayor a 0.";
            }

            System.out.println("Agregando producto al carrito: " +
                    "UserId=" + userId + ", ProductoId=" + productoId + ", Cantidad=" + cantidad);

            // Simular el comportamiento de redirección
            usuarioController.agregarProducto2(null, null, productoId, userId, cantidad, null);

            // Retorna un mensaje exitoso ya que `agregarProducto2` no falla
            return "Producto añadido exitosamente al carrito.";
        } catch (IllegalArgumentException e) {
            // Captura errores lanzados por `usuarioController.agregarProducto2`
            e.printStackTrace(); // Depuración adicional
            return e.getMessage();
        } catch (Exception e) {
            // Captura cualquier otra excepción
            e.printStackTrace();
            return "Ocurrió un error al añadir el producto al carrito. Por favor, inténtelo nuevamente.";
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

    public String procesarFlujoCompra(String userId, String mensaje) {
        // Usa un identificador predeterminado si no se proporciona userId
        String usuarioActual = (userId != null) ? userId : "defaultUser";

        // Verificar si el usuario quiere salir y volver al menú principal
        if (mensaje.equalsIgnoreCase("salir")) {
            estadosUsuario.put(usuarioActual, "MENU");
            estadoActual = "MENU";
            return manejarMenuPrincipal("");
        }

        // Obtén el estado actual del usuario o establece un estado inicial
        String estadoActual = estadosUsuario.getOrDefault(usuarioActual, "inicio");
        Map<String, String> datos = datosUsuario.computeIfAbsent(usuarioActual, k -> new HashMap<>());

        switch (estadoActual) {
            case "inicio":
                if (!estadosUsuario.containsKey(usuarioActual) || !"esperandoProducto".equals(estadosUsuario.get(usuarioActual))) {
                    estadosUsuario.put(usuarioActual, "esperandoProducto");
                }

            case "esperandoProducto":
                Producto producto = productRepository.findByNombre(mensaje);
                if (producto == null) {
                    return "No reconozco el nombre, vuelve a intentar.";
                }
                // Guardar detalles del producto en datosUsuario para confirmación
                datos.put("productoId", String.valueOf(producto.getId()));
                datos.put("productoNombre", producto.getNombre());
                datos.put("productoPrecio", String.valueOf(producto.getPrecio()));
                datos.put("productoDescripcion", producto.getDescripcion());

                // Cambiar el estado a validarProducto
                estadosUsuario.put(usuarioActual, "validarProducto");

                return "Producto encontrado:<br>" +
                        "Nombre: " + producto.getNombre() + "<br>" +
                        "Descripción: " + producto.getDescripcion() + "<br>" +
                        "Precio: S/. " + producto.getPrecio() + "<br>" +
                        "¿Es este el producto que desea añadir? " + "<br>" +
                        "<button class=\"button\" onclick=\"sendMessage('sí')\">Sí</button> " +
                        "<button class=\"button no\" onclick=\"sendMessage('no')\">No</button>";


            case "validarProducto":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(usuarioActual, "esperandoCantidad");
                    return "Ingrese la cantidad que desea comprar.";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(usuarioActual, "esperandoProducto");
                    return "Por favor, ingrese el nombre del producto que desea comprar.";
                } else {
                    // Respuesta inválida
                    return "Por favor, seleccione 'sí' o 'no'.<br>" +
                            "¿Es este el producto que desea añadir? " + "<br>" +
                            "<button class=\"button\" onclick=\"sendMessage('sí')\">Sí</button> " +
                            "<button class=\"button no\" onclick=\"sendMessage('no')\">No</button>";
                }

            case "esperandoCantidad":
                try {
                    // Limpia el mensaje antes de procesarlo
                    String mensajeLimpio = mensaje.trim();
                    System.out.println("Cantidad recibida (limpia): " + mensajeLimpio); // Depuración
                    Integer cantidad = Integer.parseInt(mensajeLimpio);

                    // Validar que la cantidad sea positiva
                    if (cantidad <= 0) {
                        return "Por favor, ingrese un número válido mayor a 0.";
                    }

                    // Depuración adicional
                    System.out.println("Cantidad válida: " + cantidad);

                    // Recuperar los datos del usuario actual
                    datos.put("cantidad", String.valueOf(cantidad));

                    // Procesar la compra y actualizar el flujo
                    String resultadoCompra = manejarCompraDesdeChatbot(
                            Integer.parseInt(usuarioActual),
                            Integer.parseInt(datos.get("productoId")),
                            cantidad
                    );

                    System.out.println("Resultado de manejarCompraDesdeChatbot: " + resultadoCompra);

                    estadosUsuario.put(usuarioActual, "otroProducto");
                    return "Producto añadido al carrito. ¿Quiere ingresar otro producto? " +
                            "<button onclick=\"sendMessage('sí')\">Sí</button> " +
                            "<button onclick=\"sendMessage('no')\">No</button>";
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Depuración para identificar el problema
                    return "Por favor, ingrese un número válido para la cantidad.";
                } catch (Exception e) {
                    e.printStackTrace(); // Depuración general
                    return "Ocurrió un error al procesar la cantidad. Por favor, inténtelo nuevamente.";
                }


            case "otroProducto":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(usuarioActual, "esperandoProducto");
                    return "Por favor, ingrese el nombre del próximo producto que desea comprar.";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(usuarioActual, "confirmarCompra");
                    return mostrarDetallesCompra(usuarioActual);
                } else {
                    return "¿Podrías repetir por favor? ¿Quiere ingresar otro producto? <button>Sí</button> <button>No</button>";
                }

            case "confirmarCompra":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(usuarioActual, "modificarDireccion");
                    return "Ingrese la nueva dirección:";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(usuarioActual, "ingresarTarjeta");
                    return "A continuación, ingrese los siguientes datos:<br> Número de tarjeta:";
                } else {
                    return "¿Podrías repetir por favor? ¿Quieres modificar la dirección? <button>Sí</button> <button>No</button>";
                }

            case "modificarDireccion":
                if (mensaje.trim().isEmpty()) {
                    return "¿Podrías repetir por favor? Ingrese la nueva dirección:";
                } else {
                    datos.put("direccion", mensaje.trim());
                    estadosUsuario.put(usuarioActual, "ingresarTarjeta");
                    return "A continuación, ingrese los siguientes datos:<br> Número de tarjeta:";
                }

            case "ingresarTarjeta":
                if (mensaje.trim().isEmpty()) {
                    return "¿Podrías repetir por favor? Ingrese el número de tarjeta:";
                } else {
                    datos.put("numeroTarjeta", mensaje.trim());
                    estadosUsuario.put(usuarioActual, "ingresarTitular");
                    return "Ingrese el nombre del titular:";
                }

            case "ingresarTitular":
                if (mensaje.trim().isEmpty()) {
                    return "¿Podrías repetir por favor? Ingrese el nombre del titular:";
                } else {
                    datos.put("nombreTitular", mensaje.trim());
                    estadosUsuario.put(usuarioActual, "ingresarVencimiento");
                    return "Ingrese la fecha de vencimiento (MM/AA):";
                }

            case "ingresarVencimiento":
                if (mensaje.trim().isEmpty()) {
                    return "¿Podrías repetir por favor? Ingrese la fecha de vencimiento (MM/AA):";
                } else {
                    datos.put("vencimiento", mensaje.trim());
                    estadosUsuario.put(usuarioActual, "ingresarCVV");
                    return "Ingrese el código CVV:";
                }

            case "ingresarCVV":
                if (mensaje.trim().isEmpty()) {
                    return "¿Podrías repetir por favor? Ingrese el código CVV:";
                } else {
                    datos.put("cvv", mensaje.trim());
                    estadosUsuario.put(usuarioActual, "confirmarPago");
                    return "A continuación procederá a pagar. Confirme.";
                }

            case "confirmarPago":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(usuarioActual, "MENU"); // Volver al menú después del pago
                    return procesarPagoYMostrarResumen(usuarioActual);
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(usuarioActual, "MENU");
                    return "Pago cancelado. Volviendo al menú principal.";
                } else {
                    return "¿Podrías repetir por favor? ¿Desea proceder con el pago? <button>Sí</button> <button>No</button>";
                }

            default:
                return "¿Podrías repetir por favor?";

        }
    }

    private String mostrarDetallesCompra(String usuarioActual) {
        // Obtén el mapa de datos del usuario actual desde `datosUsuario`
        Map<String, String> datos = datosUsuario.computeIfAbsent(usuarioActual, k -> new HashMap<>());

        // Recuperar datos almacenados o valores predeterminados si no están definidos
        String nombre = datos.getOrDefault("nombre", "No especificado");
        String apellido = datos.getOrDefault("apellido", "No especificado");
        String zona = datos.getOrDefault("zona", "No especificado");
        String distrito = datos.getOrDefault("distrito", "No especificado");
        String telefono = datos.getOrDefault("telefono", "No especificado");
        String correo = datos.getOrDefault("correo", "No especificado");
        String direccion = datos.getOrDefault("direccion", "No especificado");

        // Construir y devolver los detalles de la compra en formato HTML
        return String.format("""
        Detalles de la compra:<br>
        Nombre: %s<br>
        Apellido: %s<br>
        Zona: %s<br>
        Distrito: %s<br>
        Número de teléfono: %s<br>
        Correo electrónico: %s<br>
        Dirección: %s<br>
        ¿Quieres modificar la dirección? <button onclick="sendMessage('sí')">Sí</button> <button onclick="sendMessage('no')">No</button>
        """, nombre, apellido, zona, distrito, telefono, correo, direccion);
    }

    private String procesarPagoYMostrarResumen(String usuarioActual) {
        // Obtén el mapa de datos del usuario actual desde `datosUsuario`
        Map<String, String> datos = datosUsuario.computeIfAbsent(usuarioActual, k -> new HashMap<>());

        // Simular detalles del pedido
        String resumen = """
        Gracias %s. Tu pedido está confirmado.<br>
        Producto - Costo - Cantidad - Total<br>
        Smartphone X - S/.599.99 - 3 - S/.1799.97<br>
        Auriculares Z - S/.49.99 - 1 - S/.49.99<br>
        Tablet W - S/.350.50 - 1 - S/.350.50<br>
        <br>
        Subtotal: S/.2212.46<br>
        Costo de envío: S/.12.00<br>
        Precio total: S/.2224.46<br>
        Da enter y volverás al menú principal.
        """;

        // Recupera el nombre del usuario desde los datos o usa "Usuario" como predeterminado
        String nombre = datos.getOrDefault("nombre", "Usuario");

        // Formatea el resumen con el nombre del usuario
        return String.format(resumen, nombre);
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
        Orden orden = ordenRepository.findOrdenEstadoCreado(userId);
        if (orden != null) {
            orden.setEstado("Confirmada");
            orden.setFecha(new java.sql.Date(System.currentTimeMillis()));
            ordenRepository.save(orden);
        }
    }


}
