package com.example.springdogless.services;

import com.example.springdogless.Repository.OrdenRepository;
import com.example.springdogless.entity.Orden;
import jakarta.validation.constraints.NotNull;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;

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




    private String formatNombreArchivo(String nombre) {
        // Normalizar el nombre, eliminando tildes y caracteres especiales
        String normalized = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        // Reemplazar espacios con guiones bajos
        return normalized.replaceAll("\\s+", "_");
    }

}
