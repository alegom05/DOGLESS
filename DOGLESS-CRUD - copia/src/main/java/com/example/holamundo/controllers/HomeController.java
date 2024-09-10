package com.example.holamundo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping(method = RequestMethod.GET, value = "/holaMundo")
    @ResponseBody
    public String holaMundo() {
        return "hola mundo!!!";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/holaMundo2")
    @ResponseBody
    public String holaMundo2() {
        return "hola mundo TELECO!!!";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String home() {
        return "vista1";
    }

    @GetMapping(value = "/holaHtml")
    public String holaHtml() {
        return "hola";
    }

    @RequestMapping(value = {"/hola2", "/holaRojo", "/hola22", "/cliente/crear"})
    public String holaHtml2() {
        return "hola2";
    }

    @GetMapping("/vistaEnvioParam1")
    public String vista1() {
        return "envioParam1";
    }

    @GetMapping("/recibirParam1")
    @ResponseBody
    public String recibirParam1(@RequestParam(name = "nombre", required = false) String nombre,
                                @RequestParam("id") String idStr,
                                @RequestParam(name = "nota", required = false) Integer nota) {
        System.out.println("id: " + idStr);
        if (nombre != null && nota != null)
            return "id recibido = " + idStr + " | nombre recibido = " + nombre + " | nota = " + nota;
        else
            return "id recibido = " + idStr;
    }

                    // presentation/hola -> id
                    // presentation/hola/5
    @GetMapping("/presentation/{type}/{id}")
    @ResponseBody
    public String recibirParamPath(@PathVariable(value = "type",required = false) String type,
                                   @PathVariable("id") String id) {
        return "type: " + type + " | id= " + id;
    }

    @GetMapping("/irAvista2")
    public String irAvista2(){
        return "vista2";
    }

    @GetMapping("/vista1")
    public String olapaola(){
        return "vista1";
    }

    @GetMapping("/irAvista1")
    public String irAvista1(){
        return "vista1";
    }
}
