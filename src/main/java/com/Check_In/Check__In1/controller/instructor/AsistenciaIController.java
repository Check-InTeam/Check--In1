package com.Check_In.Check__In1.controller.instructor;

import com.Check_In.Check__In1.entity.Asistencia;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.EmailService;
import com.Check_In.Check__In1.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/instructor/asistencia")
public class AsistenciaIController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private EmailService emailService;

    // ================= VER APRENDICES =================
    @GetMapping
    public String listarAprendices(Model model) {
        LocalDate hoy = LocalDate.now();

        // Solo aprendices
        List<User> aprendices = userRepository.findByRoleNombre("APRENDIZ");

        // Verificar si ya tienen asistencia de hoy
        for (User u : aprendices) {
            Optional<Asistencia> asistencia = asistenciaRepository.findByUserAndFecha(u, hoy);
            u.setAsistencias(asistencia.map(List::of).orElse(List.of())); // solo para mostrar si ya tiene asistencia
        }

        model.addAttribute("aprendices", aprendices);
        model.addAttribute("hoy", hoy);
        return "instructor/asistencia/marcar";
    }

    // ================= MARCAR ASISTENCIA =================
    @PostMapping("/marcar")
    public String marcarAsistencia(@RequestParam Long userId,
                                   @RequestParam String estado) {

        LocalDate hoy = LocalDate.now();
        User user = userRepository.findById(userId).orElseThrow();

        Asistencia asistencia = asistenciaRepository.findByUserAndFecha(user, hoy)
                .orElse(new Asistencia());

        asistencia.setUser(user);
        asistencia.setFecha(hoy);
        asistencia.setEstado(EstadoAsistencia.valueOf(estado));
        asistencia.setHoraEntrada(LocalTime.now());

        asistenciaRepository.save(asistencia);

        if (asistencia.getEstado() == EstadoAsistencia.AUSENTE) {
            reporteService.generarReporteSiSuperaFaltas(userId);

            emailService.enviarCorreoMasivo(
                    List.of(user.getEmail()),
                    "Notificación de ausencia",
                    "Has sido marcado como ausente el día " + hoy
            );
        }

        return "redirect:/instructor/asistencia";
    }

}
