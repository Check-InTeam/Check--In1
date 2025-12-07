package com.Check_In.Check__In1.controller.aprendiz;


import com.Check_In.Check__In1.entity.Programacion;
import com.Check_In.Check__In1.service.ProgramacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/aprendiz/programming")
public class ProgramacionAprendizController {

    @Autowired
    private ProgramacionService programacionService;

    @GetMapping("")
    public String indexAprendiz(@RequestParam(required = false) String ficha,
                                @RequestParam(required = false) String nombreAsignatura,
                                Model model) {
        List<Programacion> resultados;

        if (ficha != null && !ficha.isEmpty()) {
            resultados = programacionService.getProgramacionByFicha(ficha);
        } else if (nombreAsignatura != null && !nombreAsignatura.isEmpty()) {
            resultados = programacionService.getProgramacionByNombreAsignatura(nombreAsignatura);
        } else {
            resultados = programacionService.getAllProgramacion();
        }

        model.addAttribute("programming", resultados);
        return "aprendiz/programming/index";
    }

    @GetMapping("/show/{id}")
    public String showAprendiz(@PathVariable int id, Model model) {
        Programacion programacion = programacionService.getProgramacionById(id)
                .orElse(null);

        if (programacion != null) {
            model.addAttribute("programming", programacion);
            return "aprendiz/programming/show";
        } else {
            return "redirect:/aprendiz/programming";
        }
    }
}
