package com.Check_In.Check__In1.controller.aprendiz;

import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.ReportesRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/aprendiz/reportes")
public class ReportesControllerA {

    @Autowired
    private ReportesRepository reportesRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReporteService reporteService;

    @GetMapping()
    public String ListarReportes(Model model, HttpSession session) {

        User user = (User) session.getAttribute("usuarioLogueado");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // Lista de reportes
        List<Reportes> reportes = reportesRepository.findAllByUser(user);
        model.addAttribute("reportes", reportes);

        // ===============================
        //     FALLAS REALES (AUSENTE)
        // ===============================
        long fallas = asistenciaRepository.countByUserIdAndEstado(
                user.getId(),
                EstadoAsistencia.AUSENTE
        );

        model.addAttribute("fallas", fallas);

        // Mensajes dinámicos
        if (fallas == 0) {
            model.addAttribute("estado", "verde");
            model.addAttribute("mensaje", "No tienes fallas. ¡Excelente desempeño!");
        } else if (fallas <= 3) {
            model.addAttribute("estado", "amarillo");
            model.addAttribute("mensaje", "Tienes " + fallas + " fallas registradas.");
        } else {
            model.addAttribute("estado", "rojo");
            model.addAttribute("mensaje", "Atención: tienes más de 3 fallas.");
        }

        return "aprendiz/reportes/index1";
    }
}
