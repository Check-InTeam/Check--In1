package com.Check_In.Check__In1.controller.aprendiz;

import com.Check_In.Check__In1.entity.EstadoJustificacion;
import com.Check_In.Check__In1.entity.Justificacion;
import com.Check_In.Check__In1.entity.TipoJustificacion;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.service.JustificacionService;
import com.Check_In.Check__In1.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/aprendiz/justificaciones")
public class JustificacionAController {

    private static final String UPLOAD_DIR = "uploads/justificaciones/";

    @Autowired
    private JustificacionService justificacionService;

    @Autowired
    private UserService userService;

    // 👉 Redirigir al listado si entran directo a /aprendiz/justificaciones
    @GetMapping("")
    public String redirigirALista() {
        return "redirect:/aprendiz/justificaciones/mis";
    }

    // 👉 Formulario para crear nueva justificación
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login";
        }

        Justificacion justificacion = new Justificacion();
        justificacion.setUser(user);
        model.addAttribute("justificacion", justificacion);
        model.addAttribute("user", user); // <-- Este es el importante

        return "aprendiz/justificaciones/create";
    }



    // 👉 Guardar la justificación
    @PostMapping("/guardar")
    public String guardarJustificacion(
            @RequestParam(value = "motivo", required = false) String motivo,
            @RequestParam("tipo") TipoJustificacion tipo, // 👈 ahora es enum
            @RequestParam("archivo") MultipartFile archivo,
            HttpSession session) throws IOException {

        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login";
        }

        Justificacion justificacion = new Justificacion();
        justificacion.setTipo(tipo);

        if (tipo == TipoJustificacion.OTRO) {
            justificacion.setMotivo(motivo); // lo que escribió el usuario
        } else {
            justificacion.setMotivo(tipo.name()); // usamos el nombre del enum
        }

        justificacion.setFecha(LocalDate.now());
        justificacion.setEstado(EstadoJustificacion.EnProceso);
        justificacion.setUser(user);

        if (!archivo.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, archivo.getBytes());
            justificacion.setArchivo(fileName);
        }

        justificacionService.save(justificacion);
        return "redirect:/aprendiz/justificaciones/mis";
    }



    // 👉 Ver todas las justificaciones del aprendiz
    @GetMapping("/mis")
    public String verMisJustificaciones(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("justificaciones", justificacionService.buscarPorUsuario(user));
        return "aprendiz/justificaciones/lista";
    }

    @GetMapping("/archivo/{filename}")
    public ResponseEntity<Resource> verArchivo(@PathVariable String filename) {
        try {
            Path ruta = Paths.get("uploads/justificaciones").resolve(filename).normalize();
            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Si es PDF o imagen, el navegador lo abre directamente
            String contentType = Files.probeContentType(ruta);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
