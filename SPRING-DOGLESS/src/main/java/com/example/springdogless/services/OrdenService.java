package com.example.springdogless.services;

import com.example.springdogless.Repository.Detallesorden2;
import com.example.springdogless.Repository.DetallesordenRepository;
import com.example.springdogless.Repository.OrdenRepository;
import com.example.springdogless.entity.Detalleorden;
import com.example.springdogless.entity.Orden;
import jakarta.validation.constraints.NotNull;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.Normalizer;
import java.util.*;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;

    public OrdenService(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @NotNull
    public ResponseEntity<Resource> exportUserOrders(String nombre, String dni, String formato) {
        List<Orden> ordenes = this.ordenRepository.findByNombreOrDni(nombre, dni);

        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Cargar el archivo de reporte Jasper y el logo como recursos del classpath
            InputStream jasperStream = new ClassPathResource("OrdenesPorUsuario.jasper").getInputStream();
            JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

            InputStream logoStream = new ClassPathResource("static/assets/images_index/logodogless.png").getInputStream();

            // Configurar parámetros para el reporte
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("nombreCompletoUsuario", ordenes.get(0).getNombreCompletoUsuario());
            parameters.put("dni", ordenes.get(0).getDni());
            parameters.put("logoempresa", logoStream);
            parameters.put("ds1", new JRBeanCollectionDataSource(ordenes));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            byte[] reporte;
            String fileName;
            MediaType mediaType;

            if ("pdf".equalsIgnoreCase(formato)) {
                reporte = JasperExportManager.exportReportToPdf(jasperPrint);
                String nombreUsuario = ordenes.get(0).getNombreCompletoUsuario();
                fileName = "Reporte_de_ordenes_Usuario_" + formatNombreArchivo(nombreUsuario) + ".pdf";
                mediaType = MediaType.APPLICATION_PDF;
            } else if ("xlsx".equalsIgnoreCase(formato)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JRXlsxExporter exporter = new JRXlsxExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(false);
                config.setDetectCellType(true);
                config.setCollapseRowSpan(false);
                config.setSheetNames(new String[]{"Ordenes Report"});
                exporter.setConfiguration(config);

                exporter.exportReport();
                reporte = baos.toByteArray();

                String nombreUsuario = ordenes.get(0).getNombreCompletoUsuario();
                fileName = "Reporte_de_ordenes_Usuario_" + formatNombreArchivo(nombreUsuario) + ".xlsx";
                mediaType = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .contentLength(reporte.length)
                    .contentType(mediaType)
                    .headers(headers)
                    .body(new ByteArrayResource(reporte));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    // NUEVO METODO: Generar reporte de órdenes totales por fecha
    @NotNull
    public ResponseEntity<Resource> exportTotalOrdersByDate(Date startDate, Date endDate, String formato) {
        // Filtrar las órdenes por el rango de fechas
        List<Orden> ordenes = this.ordenRepository.findByFechaBetween(startDate, endDate);

        // Verificar si no hay órdenes en el rango
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Cargar el archivo del reporte Jasper y el logo desde el classpath
            InputStream jasperStream = new ClassPathResource("OrdenesPorFecha.jasper").getInputStream();
            JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

            InputStream logoStream = new ClassPathResource("static/assets/images_index/logodogless.png").getInputStream();

            // Configurar los parámetros del reporte
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("fechainicio", startDate);
            parameters.put("fechafin", endDate);
            parameters.put("total", ordenes.size());
            parameters.put("logoempresa", logoStream);
            parameters.put("ds1", new JRBeanCollectionDataSource(ordenes));

            // Llenar el reporte con los datos y parámetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            // Variables para el archivo de salida
            byte[] reporte;
            String fileName;
            MediaType mediaType;

            // Exportar a PDF
            if ("pdf".equalsIgnoreCase(formato)) {
                reporte = JasperExportManager.exportReportToPdf(jasperPrint);
                fileName = "Reporte_de_ordenes_totales_" + startDate.toString() + "_a_" + endDate.toString() + ".pdf";
                mediaType = MediaType.APPLICATION_PDF;

                // Exportar a Excel
            } else if ("xlsx".equalsIgnoreCase(formato)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JRXlsxExporter exporter = new JRXlsxExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(false);
                config.setDetectCellType(true);
                config.setCollapseRowSpan(false);
                config.setSheetNames(new String[]{"Ordenes Totales"});
                exporter.setConfiguration(config);

                exporter.exportReport();
                reporte = baos.toByteArray();

                fileName = "Reporte_de_ordenes_totales_" + startDate.toString() + "_a_" + endDate.toString() + ".xlsx";
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                // Formato no soportado
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            // Configurar encabezados de la respuesta
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .contentLength(reporte.length)
                    .contentType(mediaType)
                    .headers(headers)
                    .body(new ByteArrayResource(reporte));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    //Para generar ordenes totales en el sistema
    @NotNull
    public ResponseEntity<Resource> exportAllOrders(String formato) {
        // Recuperar todas las órdenes
        List<Orden> ordenes = this.ordenRepository.findAllOrders();

        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Cargar el archivo de reporte Jasper y el logo
            InputStream jasperStream = new ClassPathResource("OrdenesTotales.jasper").getInputStream();
            JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

            InputStream logoStream = new ClassPathResource("static/assets/images_index/logodogless.png").getInputStream();

            // Configurar los parámetros para el reporte
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("total", ordenes.size());
            parameters.put("logoempresa", logoStream);
            parameters.put("ds1", new JRBeanCollectionDataSource(ordenes));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            byte[] reporte;
            String fileName;
            MediaType mediaType;

            if ("pdf".equalsIgnoreCase(formato)) {
                reporte = JasperExportManager.exportReportToPdf(jasperPrint);
                fileName = "Reporte_de_ordenes_totales.pdf";
                mediaType = MediaType.APPLICATION_PDF;
            } else if ("xlsx".equalsIgnoreCase(formato)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JRXlsxExporter exporter = new JRXlsxExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(false);
                config.setDetectCellType(true);
                config.setCollapseRowSpan(false);
                config.setSheetNames(new String[]{"Órdenes Totales"});
                exporter.setConfiguration(config);

                exporter.exportReport();
                reporte = baos.toByteArray();
                fileName = "Reporte_de_ordenes_totales.xlsx";
                mediaType = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .contentLength(reporte.length)
                    .contentType(mediaType)
                    .headers(headers)
                    .body(new ByteArrayResource(reporte));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //para generar ordenes por agente
    @NotNull
    public ResponseEntity<Resource> exportOrdersByAgent(Integer zonaId, String agenteNombre, String zonaNombre, String formato) {
        // Obtener las órdenes por la zona
        List<Orden> ordenes = this.ordenRepository.findOrdersByZona(zonaId);

        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Cargar el archivo de reporte Jasper y el logo
            InputStream jasperStream = new ClassPathResource("OrdenesPorAgente.jasper").getInputStream();
            JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

            InputStream logoStream = new ClassPathResource("static/assets/images_index/logodogless.png").getInputStream();

            // Configurar los parámetros del reporte
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("nombreagente", agenteNombre);
            parameters.put("zona", zonaNombre);
            parameters.put("total", ordenes.size());
            parameters.put("logoempresa", logoStream);
            parameters.put("ds1", new JRBeanCollectionDataSource(ordenes));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            byte[] reporte;
            String fileName;
            MediaType mediaType;

            if ("pdf".equalsIgnoreCase(formato)) {
                reporte = JasperExportManager.exportReportToPdf(jasperPrint);
                fileName = "Reporte_de_ordenes_por_agente.pdf";
                mediaType = MediaType.APPLICATION_PDF;
            } else if ("xlsx".equalsIgnoreCase(formato)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JRXlsxExporter exporter = new JRXlsxExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

                SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
                config.setOnePagePerSheet(false);
                config.setDetectCellType(true);
                config.setCollapseRowSpan(false);
                config.setSheetNames(new String[]{"Órdenes Por Agente"});
                exporter.setConfiguration(config);

                exporter.exportReport();
                reporte = baos.toByteArray();
                fileName = "Reporte_de_ordenes_por_agente.xlsx";
                mediaType = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .contentLength(reporte.length)
                    .contentType(mediaType)
                    .headers(headers)
                    .body(new ByteArrayResource(reporte));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //para geberar la boleta de orden
    @Autowired
    Detallesorden2 detallesRepository;

    public ResponseEntity<Resource> exportBoleta(Integer ordenId) {
        Optional<Orden> optionalOrden = ordenRepository.findById(ordenId);

        if (optionalOrden.isPresent()) {
            Orden orden = optionalOrden.get();
            List<Detalleorden> detallesOrden = detallesRepository.findListaDetallesOrdenes(orden.getId());

            try {
                // Cargar la plantilla JasperReports
                InputStream jasperStream = new ClassPathResource("BoletaOrden.jasper").getInputStream();
                JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);

                // Preparar la lista de detalles de orden como JRBeanCollectionDataSource
                List<Map<String, Object>> listaDetalles = new ArrayList<>();
                for (Detalleorden detalle : detallesOrden) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", detalle.getProducto().getNombre());
                    item.put("cantidad", detalle.getCantidad());
                    item.put("precio", detalle.getPreciounitario());
                    item.put("subTotal", detalle.getSubtotal());
                    listaDetalles.add(item);
                }
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaDetalles);

                // Parámetros para el reporte
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("logoempresa", new ClassPathResource("static/assets/images_index/logodogless.png").getInputStream());
                parameters.put("numeroorden", orden.getId());
                parameters.put("nombreCliente", orden.getNombreCompletoUsuario());
                parameters.put("fecha", orden.getFecha());
                parameters.put("direccion", orden.getDireccionenvio());
                parameters.put("metododepago", orden.getMetodopago());
                parameters.put("estado", orden.getEstado());
                parameters.put("total", orden.getTotal());
                parameters.put("dsInvoice", dataSource);
                parameters.put("montototalentexto", convertirNumeroATexto(orden.getTotal()));

                // Generar el reporte
                JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

                // Exportar el reporte a PDF
                byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);

                // Configurar la respuesta
                String fileName = "Boleta_de_orden_" + orden.getId() + ".pdf";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

                return ResponseEntity.ok()
                        .contentLength(reporte.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .headers(headers)
                        .body(new ByteArrayResource(reporte));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Metodo privado para convertir número a texto
    private String convertirNumeroATexto(BigDecimal numero) {
        if (numero == null) {
            return "";
        }

        String[] unidades = {"", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};
        String[] decenas = {"", "diez", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"};
        String[] centenas = {"", "cien", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"};

        // Redondear el número a 2 decimales
        BigDecimal redondeado = numero.setScale(2, BigDecimal.ROUND_HALF_UP);
        int parteEntera = redondeado.intValue();
        int parteDecimal = redondeado.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        // Convertir la parte entera a texto
        StringBuilder texto = new StringBuilder();

        if (parteEntera >= 1000) {
            texto.append(unidades[parteEntera / 1000]).append(" mil ");
            parteEntera %= 1000;
        }

        if (parteEntera >= 100) {
            texto.append(centenas[parteEntera / 100]).append(" ");
            parteEntera %= 100;
        }

        if (parteEntera >= 10) {
            texto.append(decenas[parteEntera / 10]).append(" ");
            parteEntera %= 10;
        }

        if (parteEntera > 0) {
            texto.append(unidades[parteEntera]).append(" ");
        }

        texto.append("y ").append(String.format("%02d/100", parteDecimal)).append(" nuevos soles");

        return texto.toString().trim();
    }






    private String formatNombreArchivo(String nombre) {
        // Normalizar el nombre, eliminando tildes y caracteres especiales
        String normalized = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        // Reemplazar espacios con guiones bajos
        return normalized.replaceAll("\\s+", "_");
    }

}
