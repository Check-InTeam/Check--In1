package com.Check_In.Check__In1.controller.administrador;

import com.Check_In.Check__In1.entity.Asistencia;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.AsistenciaEstadisticasService;
import com.Check_In.Check__In1.service.AsistenciaService;
import com.Check_In.Check__In1.service.EstadisticasPdfService;
import com.Check_In.Check__In1.service.ReporteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administrador/asistencia")
public class AsistenciaController {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private EstadisticasPdfService estadisticasPdfService;

    @Autowired
    private AsistenciaEstadisticasService estadisticasService;

    // ================= LISTADO =================
    @GetMapping
    public String listarAsistencias(Model model) {
        LocalDate hoy = LocalDate.now();
        List<User> usuarios = userRepository.findAll();
        Map<Long, Asistencia> asistenciaMap = new HashMap<>();

        for (User user : usuarios) {
            asistenciaMap.put(user.getId(),
                    asistenciaRepository.findByUserAndFecha(user, hoy).orElse(null));
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("asistenciaMap", asistenciaMap);

        return "administrador/asistencia/index";
    }

    // ================= CREAR / EDITAR UNIFICADO =================
    @GetMapping("/crear")
    public String mostrarFormularioCrear(@RequestParam(value = "id", required = false) Long id, Model model) {
        Asistencia asistencia;

        if (id != null) {
            asistencia = asistenciaRepository.findById(id).orElse(new Asistencia());
        } else {
            asistencia = new Asistencia();
        }

        model.addAttribute("asistencia", asistencia);
        model.addAttribute("usuarios", userRepository.findAll());
        return "administrador/asistencia/crear"; // formulario unificado
    }

    @PostMapping("/crear")
    public String guardarAsistencia(@Valid @ModelAttribute("asistencia") Asistencia asistencia,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarios", userRepository.findAll());
            return "administrador/asistencia/crear";
        }

        if (asistencia.getFecha() == null) {
            asistencia.setFecha(LocalDate.now());
        }

        asistenciaRepository.save(asistencia);
        return "redirect:/administrador/asistencia";
    }

    // ================= ELIMINAR =================
    @GetMapping("/eliminar/{id}")
    public String eliminarAsistencia(@PathVariable("id") Long id) {
        asistenciaRepository.deleteById(id);
        return "redirect:/administrador/asistencia";
    }

    // ================= VER DETALLE =================
    @GetMapping("/ver/{id}")
    public String verAsistencia(@PathVariable("id") Long id, Model model) {
        Asistencia asistencia = asistenciaRepository.findById(id).orElse(null);
        if (asistencia != null) {
            model.addAttribute("asistencia", asistencia);
            return "administrador/asistencia/ver";
        }
        return "redirect:/administrador/asistencia";
    }

    // ================= GENERAR ASISTENCIAS DIARIAS =================
    @GetMapping("/generar-diario")
    public String generarAsistenciasDiarias() {
        asistenciaService.generarAsistenciasDiarias();
        return "redirect:/administrador/asistencia";
    }


    @GetMapping("/exportar-estadisticas")
    public ResponseEntity<InputStreamResource> exportarEstadisticas() {
        Map<EstadoAsistencia, Long> estadisticas = estadisticasService.obtenerEstadisticas();

        ByteArrayInputStream bis = estadisticasPdfService.generarReporteEstadisticas(estadisticas);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=estadisticas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}