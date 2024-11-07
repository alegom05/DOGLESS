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

        // Verifica si hay órdenes para el usuario especificado
        if (ordenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Cargar el archivo de reporte Jasper
            File file = ResourceUtils.getFile("classpath:OrdenesPorUsuario.jasper");
            File logoempresa = ResourceUtils.getFile("classpath:static/assets/images_index/logodogless.png");
            JasperReport report = (JasperReport) JRLoader.loadObject(file);

            // Configurar parámetros para el reporte
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("nombreCompletoUsuario", ordenes.get(0).getNombreCompletoUsuario());
            parameters.put("dni", ordenes.get(0).getDni());
            parameters.put("logoempresa", new FileInputStream(logoempresa));
            parameters.put("ds1", new JRBeanCollectionDataSource(ordenes));



            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            byte[] reporte;
            String fileName;
            MediaType mediaType;

            if ("pdf".equalsIgnoreCase(formato)) {
                // Generar PDF
                reporte = JasperExportManager.exportReportToPdf(jasperPrint);

                // Generar el nombre del archivo con el nombre del usuario
                String nombreUsuario = ordenes.get(0).getNombreCompletoUsuario();
                fileName = "Reporte_de_ordenes_Usuario_" + formatNombreArchivo(nombreUsuario) + ".pdf";
                mediaType = MediaType.APPLICATION_PDF;

            } else if ("xlsx".equalsIgnoreCase(formato)) {
                // Generar Excel
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

                // Generar el nombre del archivo con el nombre del usuario
                String nombreUsuario = ordenes.get(0).getNombreCompletoUsuario();
                fileName = "Reporte_de_ordenes_Usuario_" + formatNombreArchivo(nombreUsuario) + ".xlsx";
                mediaType = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            } else {
                return ResponseEntity.badRequest().body(null);
            }


            // Configurar headers de respuesta
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
