package com.Check_In.Check__In1.controller;


import com.Check_In.Check__In1.entity.EstadoJustificacion;
import com.Check_In.Check__In1.entity.Justificacion;
import com.Check_In.Check__In1.entity.RegistroEquipos;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.JustificacionRepository;
import com.Check_In.Check__In1.repository.RegistroEquiposRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistroEquiposRepository RegistroEquiposRepository;

    @Autowired
    private JustificacionRepository justificacionRepository;

    @GetMapping("/administrador/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("usuarioLogueado");

        if (user == null || !"ADMINISTRADOR".equalsIgnoreCase(user.getRole().getNombre())) {
            return "redirect:/login";
        }

        long totalAprendices = userRepository.countByRole_NombreIgnoreCase("APRENDIZ");
        long totalInstructores = userRepository.countByRole_NombreIgnoreCase("INSTRUCTOR");
        long totalEquipos = RegistroEquiposRepository.count();

        List<RegistroEquipos> ultimosEquipos = RegistroEquiposRepository.findTop5ByOrderByIdDesc();

        List<Justificacion> justificacionesPendientes = justificacionRepository.findByEstado(EstadoJustificacion.EnProceso);

        model.addAttribute("usuario", user);
        model.addAttribute("totalAprendices", totalAprendices);
        model.addAttribute("totalInstructores", totalInstructores);
        model.addAttribute("totalEquipos", totalEquipos);
        model.addAttribute("ultimosEquipos", ultimosEquipos);
        model.addAttribute("justificacionesPendientes", justificacionesPendientes);

        return "administrador/dashboard";
    }

    @GetMapping("/instructor/dashboard")
    public String instructorDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogueado");

        if (user == null || !"INSTRUCTOR".equalsIgnoreCase(user.getRole().getNombre())) {
            return "redirect:/login";
        }

        List<RegistroEquipos> Equipos = RegistroEquiposRepository.findByUserId(user.getId());

        List<Justificacion> justifications = justificacionRepository.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("Equipos", Equipos);
        model.addAttribute("justifications", justifications);
        return "instructor/dashboard";
    }

    @GetMapping("/aprendiz/dashboard")
    public String aprendizDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("usuarioLogueado");

        if (user == null || !"APRENDIZ".equalsIgnoreCase(user.getRole().getNombre())) {
            return "redirect:/login";
        }

        List<RegistroEquipos> equipos = RegistroEquiposRepository.findByUserId(user.getId());
        List<Justificacion> justificaciones = justificacionRepository.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("equipos", equipos);
        model.addAttribute("justificaciones", justificaciones);

        return "aprendiz/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}

