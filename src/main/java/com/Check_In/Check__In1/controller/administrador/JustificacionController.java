package com.Check_In.Check__In1.controller.administrador;

import com.Check_In.Check__In1.entity.EstadoJustificacion;
import com.Check_In.Check__In1.entity.Justificacion;
import com.Check_In.Check__In1.service.JustificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/administrador/justificaciones")
public class JustificacionController {

    @Autowired
    private JustificacionService justificacionService;

    private final String UPLOAD_DIR = "uploads/justificaciones/";

    // 📌 INDEX con filtros
    @GetMapping
    public String index(@RequestParam(required = false) EstadoJustificacion estado,
                        @RequestParam(required = false) String nombre,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                        Model model) {

        List<Justificacion> justificaciones;

        if (estado != null) {
            justificaciones = justificacionService.findByEstado(estado);
        } else if (nombre != null && !nombre.isEmpty()) {
            justificaciones = justificacionService.getJustificacionByUserNombre(nombre);
        } else if (fecha != null) {
            justificaciones = justificacionService.getJustificacionByFecha(fecha);
        } else {
            justificaciones = justificacionService.getAllJustificacion();
        }

        model.addAttribute("justificaciones", justificaciones);
        model.addAttribute("estados", EstadoJustificacion.values()); // para el filtro en el select
        return "administrador/justificaciones/index";
    }


    // 📌 FORMULARIO EDITAR (cambiar estado + comentario)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
            Justificacion justificacion = justificacionService.findById(id);
            if (justificacion == null) {
                return "redirect:/administrador/justificaciones";
            }
            model.addAttribute("justificacion", justificacion);
            model.addAttribute("estados", EstadoJustificacion.values());
            return "administrador/justificaciones/edit";
    }

    // 📌 ACTUALIZAR ESTADO
    @PostMapping("/{id}/update")
    public String update(@PathVariable int id,
                         @RequestParam EstadoJustificacion estado,
                         @RequestParam(value = "comentario", required = false) String comentario) {

        Justificacion justificacion = justificacionService.findById(id);
        if (justificacion != null) {
            justificacion.setEstado(estado);
                justificacion.setComentario(comentario);
                justificacionService.save(justificacion);
            }
        return "redirect:/administrador/justificaciones";
    }



    // 📌 Eliminar
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            justificacionService.deleteJustificacion(id);
            redirectAttributes.addFlashAttribute("success", "Justificación eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la justificación.");
        }
        return "redirect:/administrador/justificaciones";
    }

    @GetMapping("/archivo/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> verArchivo(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
