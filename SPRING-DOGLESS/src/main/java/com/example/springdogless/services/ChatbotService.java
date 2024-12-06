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
        return "Recuerda que también puedes acceder a nuestra sección \"Libro de Reclamaciones\" de la barra lateral.";
    }

    private String generarOrdenDeCompra() {
        String numeroOrden = "ORD-" + System.currentTimeMillis();
        return String.format("Tu orden de compra ha sido generada exitosamente. Número de orden: %s.", numeroOrden);
    }

    public String manejarCompraDesdeChatbot(Integer userId, Integer productoId, Integer cantidad) {
        try {
            // Llama al método `agregarProducto2` del UsuarioController
            usuarioController.agregarProducto2(null, null, productoId, userId, cantidad, null);
            return "Producto añadido exitosamente al carrito.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Hubo un error al añadir el producto al carrito. Por favor, inténtelo nuevamente.";
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
        String estadoActual = estadosUsuario.getOrDefault(userId, "inicio");
        Map<String, String> datos = datosUsuario.computeIfAbsent(userId, k -> new HashMap<>());

        switch (estadoActual) {
            case "inicio":
                estadosUsuario.put(userId, "esperandoProducto");
                return "Por favor, ingrese el nombre del producto que desea comprar.";

            case "esperandoProducto":
                Producto producto = productRepository.findByNombre(mensaje);
                if (producto == null) {
                    return "No encontramos el producto. Por favor, inténtelo de nuevo.";
                }
                datos.put("productoId", String.valueOf(producto.getId()));
                datos.put("productoNombre", producto.getNombre());
                datos.put("productoPrecio", String.valueOf(producto.getPrecio()));
                estadosUsuario.put(userId, "esperandoCantidad");
                return "Producto encontrado: " + producto.getNombre() +
                        "\nPrecio: S/. " + producto.getPrecio() +
                        "\nIngrese la cantidad.";

            case "esperandoCantidad":
                try {
                    int cantidad = Integer.parseInt(mensaje);
                    datos.put("cantidad", String.valueOf(cantidad));
                    manejarCompraDesdeChatbot(Integer.parseInt(userId), Integer.parseInt(datos.get("productoId")), cantidad);
                    estadosUsuario.put(userId, "otroProducto");
                    return "Producto añadido al carrito. ¿Quiere ingresar otro producto? (Escriba 'sí' o 'no')";
                } catch (NumberFormatException e) {
                    return "Por favor, ingrese un número válido para la cantidad.";
                }

            case "otroProducto":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "esperandoProducto");
                    return "Por favor, ingrese el nombre del próximo producto que desea comprar.";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "continuarCompra");
                    return "¿Desea continuar con la compra? (Escriba 'sí' o 'no')";
                } else {
                    return "Respuesta no válida. ¿Quiere ingresar otro producto? (Escriba 'sí' o 'no')";
                }

            case "continuarCompra":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "validarDatos");
                    return "Por favor valide los siguientes datos:\n" +
                            "Nombre:\nApellido:\nZona:\nDistrito:\nNúmero de teléfono:\nCorreo:";
                } else {
                    estadosUsuario.put(userId, "inicio");
                    return "Regresando al menú principal. ¿En qué puedo ayudarte?";
                }

            case "validarDatos":
                if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "inicio");
                    return "Regresando al menú principal.";
                } else if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "direccion");
                    return "Ingrese su dirección de envío.";
                } else {
                    return "Respuesta no válida. ¿Desea continuar con los datos validados? (Escriba 'sí' o 'no')";
                }

            case "direccion":
                datos.put("direccion", mensaje);
                estadosUsuario.put(userId, "cambiarDireccion");
                return "¿Quiere cambiar su dirección? (Escriba 'sí' o 'no')";

            case "cambiarDireccion":
                if (mensaje.equalsIgnoreCase("sí")) {
                    estadosUsuario.put(userId, "direccion");
                    return "Ingrese la nueva dirección:";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "tarjeta");
                    return "Ingrese su número de tarjeta.";
                } else {
                    return "Respuesta no válida. ¿Quiere cambiar su dirección? (Escriba 'sí' o 'no')";
                }

            case "tarjeta":
                datos.put("tarjeta", mensaje);
                estadosUsuario.put(userId, "nombreTitular");
                return "Ingrese el nombre del titular de la tarjeta.";

            case "nombreTitular":
                datos.put("nombreTitular", mensaje);
                estadosUsuario.put(userId, "vencimiento");
                return "Ingrese la fecha de vencimiento de la tarjeta (MM/AA).";

            case "vencimiento":
                datos.put("vencimiento", mensaje);
                estadosUsuario.put(userId, "cvv");
                return "Ingrese el CVV de la tarjeta.";

            case "cvv":
                datos.put("cvv", mensaje);
                estadosUsuario.put(userId, "confirmarPago");
                return "A continuación se procede a pagar. ¿Está de acuerdo? (Escriba 'sí' o 'no')";

            case "confirmarPago":
                if (mensaje.equalsIgnoreCase("sí")) {
                    confirmarCompra(Integer.parseInt(userId));
                    estadosUsuario.put(userId, "descargarPDF");
                    return "Gracias, " + datos.get("nombre") + ". Tu pedido ya está confirmado. Recibirás un correo electrónico con tu número de pedido. ¿Quieres descargar el PDF de la boleta? (Escriba 'sí' o 'no')";
                } else if (mensaje.equalsIgnoreCase("no")) {
                    estadosUsuario.put(userId, "inicio");
                    return "Regresando al menú principal.";
                } else {
                    return "Respuesta no válida. ¿Está de acuerdo con proceder al pago? (Escriba 'sí' o 'no')";
                }

            case "descargarPDF":
                if (mensaje.equalsIgnoreCase("sí")) {
                    // Lógica para generar y enviar el PDF
                    estadosUsuario.put(userId, "inicio");
                    return "PDF descargado. Regresando al menú principal.";
                } else {
                    estadosUsuario.put(userId, "inicio");
                    return "Regresando al menú principal.";
                }

            default:
                estadosUsuario.put(userId, "inicio");
                return "Ocurrió un error. Regresando al menú principal.";
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
