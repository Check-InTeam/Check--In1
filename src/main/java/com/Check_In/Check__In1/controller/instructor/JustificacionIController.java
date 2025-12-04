package com.Check_In.Check__In1.controller.instructor;

import com.Check_In.Check__In1.entity.EstadoJustificacion;
import com.Check_In.Check__In1.entity.Justificacion;
import com.Check_In.Check__In1.service.JustificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instructor/justificaciones")
public class JustificacionIController {

    @Autowired
    private JustificacionService justificacionService;

    // 👉 Listar todas las justificaciones
    @GetMapping("")
    public String listarJustificaciones(Model model) {
        model.addAttribute("justificaciones", justificacionService.findAll());
        model.addAttribute("estados", EstadoJustificacion.values());
        return "instructor/justificaciones/lista1";
    }

    // 👉 Ver detalle de una justificación
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable int id, Model model) {
        Justificacion justificacion = justificacionService.findById(id);
        model.addAttribute("justificacion", justificacion);
        return "instructor/justificaciones/detalle";
    }

    // 👉 Actualizar estado + comentario
    @PostMapping("/{id}/actualizar")
    public String actualizarEstadoComentario(
            @PathVariable int id,
            @RequestParam EstadoJustificacion estado,
            @RequestParam(required = false) String comentario) {

        Justificacion justificacion = justificacionService.findById(id);
        if (justificacion != null) {
            justificacion.setEstado(estado);
            justificacion.setComentario(comentario);
            justificacionService.save(justificacion);
        }

        return "redirect:/instructor/justificaciones";
    }
}

