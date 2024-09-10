package com.example.holamundo.controllers;

import com.example.holamundo.entity.Shipper;
import com.example.holamundo.repository.ShipperRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/shipper")
public class ShipperController {

    final ShipperRepository shipperRepository;

    public ShipperController(ShipperRepository shipperRepository) {
        this.shipperRepository = shipperRepository;
    }

    /*@RequestMapping({"/",""})
    @ResponseBody
    public String msg(){
        return "olapaola";
    }*/

    @GetMapping({"/listar",""})
    public String listar(Model model){
        List<Shipper> shipperList = shipperRepository.findAll();
        model.addAttribute("listaTransportistas",shipperList);
        return "shipper/lista";
    }

    @GetMapping("/new")
    public String nuevoTransportistaFrm() {
        return "shipper/newFrm";
    }

    /*@PostMapping("/guardar")
    public String guardar(Shipper shipper){
        shipperRepository.save(shipper);
        return "redirect:/shipper/listar";
    }*/

    @GetMapping("/editar")
    public String editarTransportista(Model model, @RequestParam("id") int id) {

        Optional<Shipper> optShipper = shipperRepository.findById(id);

        if (optShipper.isPresent()) {
            Shipper shipper = optShipper.get();
            model.addAttribute("transportista", shipper);
            return "shipper/editform";
        } else {
            return "redirect:/shipper";
        }
    }

    @GetMapping("/borrar")
    public String borrarTransportista(Model model,
                                      @RequestParam("id") int id) {

        Optional<Shipper> optShipper = shipperRepository.findById(id);

        if (optShipper.isPresent()) {
            shipperRepository.deleteById(id);
        }
        return "redirect:/listar";

    }

    @PostMapping("/BuscarTransportistas")
    public String buscarTransportista(@RequestParam("searchField") String searchField, Model model) {
        //List<Shipper> listaTransportistas = shipperRepository.findByCompanyname(searchField);
        List<Shipper> listaTransportistas = shipperRepository.buscarPorNombreParcial(searchField);
        model.addAttribute("listaTransportistas", listaTransportistas);
        return "shipper/listar";
    }
    //Agregado
    @PostMapping("/guardar")
    public String guardarNuevoTransportista(Shipper shipper, RedirectAttributes attr) {
        shipperRepository.save(shipper);
        attr.addFlashAttribute("msg", "Usuario creado exitosamente");
        return "redirect:/shipper/listar";
    }

}
