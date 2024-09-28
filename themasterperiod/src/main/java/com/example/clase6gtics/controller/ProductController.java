package com.example.clase6gtics.controller;

import com.example.clase6gtics.entity.Product;
import com.example.clase6gtics.repository.CategoryRepository;
import com.example.clase6gtics.repository.ProductRepository;
import com.example.clase6gtics.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;
    final SupplierRepository supplierRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping(value = {"", "/"})
    public String listaProductos(Model model) {
        model.addAttribute("listaProductos", productRepository.findAll());
        return "product/list";
    }

    @GetMapping("/new")
    public String nuevoProductoFrm(Model model, @ModelAttribute("product") Product product) {
        model.addAttribute("listaCategorias", categoryRepository.findAll());
        model.addAttribute("listaProveedores", supplierRepository.findAll());
        return "product/editFrm";
    }

    @PostMapping("/save")
    public String guardarProducto(RedirectAttributes attr, Model model,
                                  @ModelAttribute("product") @Valid Product product, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { //si no hay errores, se realiza el flujo normal

            if (product.getProductname().equals("gaseosa")) {
                model.addAttribute("msg", "Error al crear producto");
                model.addAttribute("listaCategorias", categoryRepository.findAll());
                model.addAttribute("listaProveedores", supplierRepository.findAll());
                return "product/editFrm";
            } else {
                if (product.getId() == 0) {
                    attr.addFlashAttribute("msg", "Producto creado exitosamente");
                } else {
                    attr.addFlashAttribute("msg", "Producto actualizado exitosamente");
                }
                productRepository.save(product);
                return "redirect:/product";
            }

        } else { //hay al menos 1 error
            model.addAttribute("listaCategorias", categoryRepository.findAll());
            model.addAttribute("listaProveedores", supplierRepository.findAll());
            return "product/editFrm";
        }
    }

    @GetMapping("/edit")
    public String editarTransportista(@ModelAttribute("product") Product product,
                                      Model model, @RequestParam("id") int id) {

        Optional<Product> optProduct = productRepository.findById(id);

        if (optProduct.isPresent()) {
            product = optProduct.get();
            model.addAttribute("product", product);
            model.addAttribute("listaCategorias", categoryRepository.findAll());
            model.addAttribute("listaProveedores", supplierRepository.findAll());
            return "product/editFrm";
        } else {
            return "redirect:/product";
        }
    }

    @GetMapping("/delete")
    public String borrarTransportista(@RequestParam("id") int id,
                                      RedirectAttributes attr) {

        Optional<Product> optProduct = productRepository.findById(id);

        if (optProduct.isPresent()) {
            productRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Producto borrado exitosamente");
        }
        return "redirect:/product";

    }

   /* @InitBinder("product")
    public void validador(WebDataBinder binder) {

        binder.registerCustomEditor(BigDecimal.class, "unitprice", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try{
                    BigDecimal bigDecimal = new BigDecimal(text);
                    this.setValue(bigDecimal);
                }catch (NumberFormatException e){
                    this.setValue(0);
                }
            }
        });
    }*/


}
