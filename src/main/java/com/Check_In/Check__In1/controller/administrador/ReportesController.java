package com.Check_In.Check__In1.controller.administrador;

import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.entity.TipoReporte;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.ReportesRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrador/reportes")
public class ReportesController {

    @Autowired
    private ReportesRepository reportesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Listar todos los reportes
    @GetMapping
    public String listarReportes(Model model){
        List<Reportes> reportes = reportesRepository.findAll();
        model.addAttribute("reportes", reportes);
        return "administrador/reportes/index"; // aquí sí debe existir index.html
    }


    // Crear reporte
    @GetMapping("/create")
    public String createReporte(Model model) {
        model.addAttribute("reporte", new Reportes());

        // Traer todos los usuarios que sean aprendices
        List<User> aprendices = userRepository.findByRoleNombre("APRENDIZ");

        model.addAttribute("usuarios", aprendices);
        model.addAttribute("tipos", TipoReporte.values());
        return "administrador/reportes/create";
    }



    // Guardar o actualizar reporte
    @PostMapping("/update")
    public String saveReporte(@ModelAttribute Reportes reporte){
        reporte.setFechaCreacion(LocalDateTime.now());
        reportesRepository.save(reporte);
        return "redirect:/administrador/reportes";
    }

    // Editar reporte
    @GetMapping("/edit/{id}")
    public String editReporte(@PathVariable Long id, Model model){
        Reportes reporte = reportesRepository.findById(id).orElseThrow();
        model.addAttribute("reporte", reporte);
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("tipos", TipoReporte.values());
        return "administrador/reportes/edit";
    }

    // Eliminar reporte
    @PostMapping("/delete/{id}")
    public String deleteReporte(@PathVariable Long id, RedirectAttributes redirectAttributes){
        reportesRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("messageExit", "¡Reporte eliminado correctamente!");
        return "redirect:/administrador/reportes"; // <- SIN barra al final
    }




}
