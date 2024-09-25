package com.example.holamundo.controllers;

import com.example.holamundo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/employee")
public class EmpleadoController {

    @Autowired
    employeeRepository employeeRepository;

    @ResponseBody
    @RequestMapping(value = {"/listar", "/",""})
    public String listar() {
        return "listar employee hr";
    }

    @ResponseBody
    @RequestMapping("/crear")
    public String crear() {
        return "crear employee hr";
    }

    @ResponseBody
    @RequestMapping("/editar")
    public String editar() {
        return "editar employee hr";
    }

    @GetMapping(value = {"", "/"})
    public String cantidadEmpleadosPorRegion(Model model) {

        model.addAttribute("listaEmpleadosPorRegion", employeeRepository.obtenerEmpleadosPorRegion());
        return "empleado/estadistica";
    }

    @GetMapping(value = "/buscarEmpleadoPais/{pais}")
    public String cantidadEmpleadosPorPaisBuscar(Model model,
                                                 @PathVariable("pais") String paisBuscar) {

        model.addAttribute("listaEmpleadosPorPais", employeeRepository.obtenerEmpleadosPorPais(paisBuscar));
        return "empleado/empleadoporpaisfiltrado";
    }




}
