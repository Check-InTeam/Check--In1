package com.Check_In.Check__In1.controller.instructor;

import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.Reportes;
import com.Check_In.Check__In1.entity.TipoReporte;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.AsistenciaRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.EmailService;
import com.Check_In.Check__In1.service.PdfService;
import com.Check_In.Check__In1.service.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/instructor/reportes")
public class ReportesControllerI {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfService pdfService;

    // ---------------- VALIDACIÓN DE SESIÓN ----------------
    private User validarSesion(HttpSession session) {
        return (User) session.getAttribute("usuarioLogueado");
    }

    // ---------------- LISTAR ----------------
    @GetMapping
    public String listarReportes(Model model, HttpSession session) {

        User user = validarSesion(session);
        if (user == null) return "redirect:/login";

        List<Reportes> reportes = reporteService.getAllReportes();
        model.addAttribute("reportes", reportes);

        return "instructor/reportes/lista-reportes";
    }

    // ---------------- FORMULARIO CREAR ----------------
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model, HttpSession session) {

        User user = validarSesion(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("reporte", new Reportes());

        List<User> aprendices = userRepository.findByRoleNombre("APRENDIZ");
        model.addAttribute("users", aprendices);

        Map<Long, Long> ausenciasMap = new HashMap<>();
        for (User u : aprendices) {
            long ausencias = asistenciaRepository.countByUserIdAndEstado(
                    u.getId(), EstadoAsistencia.AUSENTE);
            ausenciasMap.put(u.getId(), ausencias);
        }
        model.addAttribute("ausenciasMap", ausenciasMap);

        model.addAttribute("tiposReporte", TipoReporte.values());
        return "instructor/reportes/form-reporte";
    }

    // ---------------- GUARDAR ----------------
    @PostMapping("/guardar")
    public String guardarReporte(@ModelAttribute("reporte") Reportes reporteForm, HttpSession session) {

        User user = validarSesion(session);
        if (user == null) return "redirect:/login";

        Reportes reporteGuardado;

        if (reporteForm.getId() != null) {
            reporteGuardado = reporteService.getReporteById(reporteForm.getId())
                    .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
            reporteGuardado.setTitulo(reporteForm.getTitulo());
            reporteGuardado.setDescripcion(reporteForm.getDescripcion());
            reporteGuardado.setTipo(reporteForm.getTipo());
            reporteGuardado.setUser(reporteForm.getUser());
        } else {
            reporteGuardado = reporteForm;
        }

        reporteService.saveReporte(reporteGuardado);

        // Generar PDF
        byte[] pdfBytes = pdfService.generarReportePdf(reporteGuardado);

        // Enviar correo al aprendiz
        if (pdfBytes != null) {
            emailService.enviarCorreoConAdjunto(
                    reporteGuardado.getUser().getEmail(),
                    "Nuevo Reporte Generado",
                    "Se ha generado un nuevo reporte con tu información.",
                    pdfBytes,
                    "reporte_" + reporteGuardado.getUser().getNombre() + ".pdf"
            );
        }

        return "redirect:/instructor/reportes";
    }

    // ---------------- EDITAR ----------------
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {

        User user = validarSesion(session);
        if (user == null) return "redirect:/login";

        Reportes reporte = reporteService.getReporteById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        model.addAttribute("reporte", reporte);

        List<User> aprendices = userRepository.findByRoleNombre("APRENDIZ");
        model.addAttribute("users", aprendices);

        model.addAttribute("tiposReporte", TipoReporte.values());

        return "instructor/reportes/form-reporte";
    }

    // ---------------- ELIMINAR ----------------
    @GetMapping("/eliminar/{id}")
    public String eliminarReporte(@PathVariable Long id, HttpSession session) {

        User user = validarSesion(session);
        if (user == null) return "redirect:/login";

        reporteService.deleteReporteById(id);
        return "redirect:/instructor/reportes";
    }

}
