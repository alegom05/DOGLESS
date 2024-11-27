package com.example.springdogless.services;

import com.example.springdogless.Repository.Detallesorden2;
import com.example.springdogless.Repository.OrdenRepository;
import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.Repository.UsuarioRepository;
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

    private String estadoActual = "MENU"; // Estado inicial

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
                return generarOrdenDeCompra();
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
                estadoActual = "ORDEN";
                return generarOrdenDeCompra();
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


    private String getIntroduccion() {
        return "Hola, soy Dogbot. ¿En qué puedo ayudarte? Elige una de las siguientes opciones:";
    }

    private String getMenuOpciones() {
        StringBuilder menu = new StringBuilder();
        for (JsonNode opcion : menuOpciones) {
            menu.append("\n").append(opcion.get("opcion").asText()).append(". ").append(opcion.get("descripcion").asText());
        }
        return menu.toString();
    }

    private String manejarReclamaciones() {
        return "Puedes acceder a nuestro libro de reclamaciones virtual en este enlace: [Libro de Reclamaciones Virtual].";
    }

    private String generarOrdenDeCompra() {
        String numeroOrden = "ORD-" + System.currentTimeMillis();
        return String.format("Tu orden de compra ha sido generada exitosamente. Número de orden: %s.", numeroOrden);
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
        String estadoActual = estadosUsuario.getOrDefault(userId, "inicio");

        switch (estadoActual) {
            case "inicio":
                estadosUsuario.put(userId, "esperandoProducto");
                return "Por favor, ingrese el nombre del producto que desea comprar.";

            case "esperandoProducto":
                Producto producto = productRepository.findByNombre(mensaje);
                if (producto == null) {
                    return "No encontramos el producto. Por favor, inténtelo de nuevo.";
                }
                estadosUsuario.put(userId, "esperandoCantidad");
                estadosUsuario.put(userId + "_productoId", String.valueOf(producto.getId()));
                return "Producto encontrado: " + producto.getNombre() +
                        "\nPrecio: S/. " + producto.getPrecio() +
                        "\n¿Desea continuar? Responda 'sí' o 'no'.";

            case "esperandoCantidad":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "esperandoCantidadNumero");
                    return "¿Cuántas unidades desea comprar?";
                } else {
                    estadosUsuario.put(userId, "inicio");
                    return "Flujo cancelado. Volviendo al inicio.";
                }

            case "esperandoCantidadNumero":
                try {
                    int cantidad = Integer.parseInt(mensaje);
                    Integer productoId = Integer.parseInt(estadosUsuario.get(userId + "_productoId"));
                    agregarProductoACarrito(Integer.parseInt(userId), productoId, cantidad);
                    estadosUsuario.put(userId, "continuarCompra");
                    return "Producto añadido al carrito. ¿Desea proceder a la compra? Responda 'sí' o 'no'.";
                } catch (NumberFormatException e) {
                    return "Ingrese un número válido.";
                }

            case "continuarCompra":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "checkout");
                    return "Por favor, confirme sus datos: nombre, apellido, número de teléfono, y correo.";
                } else {
                    estadosUsuario.put(userId, "inicio");
                    return "Flujo cancelado. Volviendo al inicio.";
                }

            case "checkout":
                // Validar los datos ingresados
                if (mensaje.split(",").length < 4) {
                    return "Ingrese todos los datos en el formato: nombre, apellido, teléfono, correo.";
                }
                estadosUsuario.put(userId, "esperandoPago");
                return "Ingrese los datos de su tarjeta: número, nombre, vencimiento y CVV.";

            case "esperandoPago":
                if (mensaje.split(",").length < 4) {
                    return "Ingrese todos los datos de su tarjeta en el formato: número, nombre, vencimiento, CVV.";
                }
                confirmarCompra(Integer.parseInt(userId));
                estadosUsuario.put(userId, "inicio");
                return "Gracias por su compra. Su pedido está confirmado. Recibirá un correo electrónico con su número de pedido.";

            default:
                estadosUsuario.put(userId, "inicio");
                return "No entendí su mensaje. Por favor, inténtelo de nuevo.";
        }
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
