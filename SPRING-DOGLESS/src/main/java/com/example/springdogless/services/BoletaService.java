package com.example.springdogless.services;

import com.example.springdogless.Repository.ProductRepository;
import com.example.springdogless.entity.Producto;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class BoletaService {

    @Autowired
    private ProductRepository productoRepository;

    public ByteArrayResource generarBoletaPDF(Producto producto) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        try {
            document.open();

            // Título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Detalle de Producto", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n")); // Espacio después del título

            // Tabla de detalles
            PdfPTable tabla = new PdfPTable(2); // 2 columnas
            tabla.setWidthPercentage(100);

            // Añadir detalles del producto
            agregarFilaTabla(tabla, "ID", producto.getId().toString());
            agregarFilaTabla(tabla, "Nombre", producto.getNombre());
            agregarFilaTabla(tabla, "Precio", "S/. " + producto.getPrecio().toString());
            //agregarFilaTabla(tabla, "Cantidad", producto.get().toString());
            //agregarFilaTabla(tabla, "Fecha de Orden", producto.get().toString());
            //agregarFilaTabla(tabla, "Fecha de Entrega", producto.get().toString());

            document.add(tabla);

        } finally {
            document.close();
        }

        return new ByteArrayResource(out.toByteArray());
    }

    private void agregarFilaTabla(PdfPTable tabla, String etiqueta, String valor) {
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph etiquetaParrafo = new Paragraph(etiqueta, boldFont);
        Paragraph valorParrafo = new Paragraph(valor, normalFont);

        tabla.addCell(etiquetaParrafo);
        tabla.addCell(valorParrafo);
    }
}