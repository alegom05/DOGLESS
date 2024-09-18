package com.example.holamundo.controllers;

import com.example.holamundo.entity.Persona;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/persona")
public class PersonaController {

    @GetMapping("")
    public String unaPersona(Model model) {
        //Solo una persona
        Persona persona = new Persona("Juan", "Perez", "12345678", 20);
        model.addAttribute("persona", persona);

        //Lista de personas
        Persona persona1 = new Persona("Juan", "Perez", "12345678", 20);
        Persona persona2 = new Persona("Maria", "Gomez", "87654321", 25);
        Persona persona3 = new Persona("Carlos", "Lopez", "11223344", 30);

        // Crear una lista de personas
        List<Persona> listaPersonas = new ArrayList<>();
        listaPersonas.add(persona1);
        listaPersonas.add(persona2);
        listaPersonas.add(persona3);

        // Agregar la lista al modelo
        model.addAttribute("listaPersonas", listaPersonas);
        return "home";
    }

    @GetMapping("/listaPersonas")
    public String listaPersonas(Model model) {
        ArrayList<Persona> listaPersonas = new ArrayList<>();
        int numero = 6;
        listaPersonas.add(new Persona("Juan", "Perez", "12345678", 20));
        listaPersonas.add(new Persona("Carlos", "Mencia", "82564875", 25));
        listaPersonas.add(new Persona("Andrea", "Huanca", "78649257", 30));
        model.addAttribute("listaPersonas", listaPersonas);
        model.addAttribute("numero", 6);
        return "home";
    }

    @PostMapping("/guardar")
    public String guardarPersona(Persona persona) {
        System.out.println("Nombre: " + persona.getNombre() + "\n" +
                "Apellido: " + persona.getApellido() + "\n" +
                "DNI: " + persona.getDni());
        return "page1";
    }

    @GetMapping("/pagina1")
    public String pagina1(Model model) {
        Persona persona = new Persona("Jorge", "Perez", "12345678", 20);
        model.addAttribute("persona", persona);

        Persona persona1 = new Persona("Oscar", "Perez", "12345678", 20);
        Persona persona2 = new Persona("María", "Gomez", "87654321", 25);
        Persona persona3 = new Persona("Alejandro", "Ramìrez", "11223344", 30);

        // Crear una lista de personas
        List<Persona> listaPersonas = new ArrayList<>();
        listaPersonas.add(persona1);
        listaPersonas.add(persona2);
        listaPersonas.add(persona3);

        // Agregar la lista al modelo
        model.addAttribute("listaPersonas", listaPersonas);
        return "pagina1";


    }

    public static List<List<Integer>> generarMatrizAleatoria(int n) {
        List<List<Integer>> matriz = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            List<Integer> fila = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                int numeroAleatorio = random.nextInt(100); // Generar un número aleatorio entre 0 y 99
                fila.add(numeroAleatorio);
            }
            matriz.add(fila);
        }

        return matriz;
    }
}
