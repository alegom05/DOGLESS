package com.example.holamundo.repository;

import com.example.holamundo.entity.Region;
import com.example.holamundo.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    @Controller
    @RequestMapping("/region")
    public class RegionController {

        final RegionRepository regionRepository;

        public RegionController(RegionRepository regionRepository) {
            this.regionRepository = regionRepository;
        }




        @GetMapping("/listar")
        public String listar(Model model){
            List<Region> regionList = regionRepository.findAll();
            model.addAttribute("listaRegiones",regionList);
            return "region/lista";
        }

    }

}