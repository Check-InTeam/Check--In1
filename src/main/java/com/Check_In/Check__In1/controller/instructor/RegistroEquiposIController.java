package com.Check_In.Check__In1.controller.instructor;


import com.Check_In.Check__In1.entity.RegistroEquipos;
import com.Check_In.Check__In1.entity.TipoEquipo;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.RegistroEquiposService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/instructor/registroEquipos")
public class RegistroEquiposIController {
    @Autowired
    private RegistroEquiposService registroEquiposService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model) {
        List<RegistroEquipos> equipos = registroEquiposService.getAllRegistroEquipos();
        model.addAttribute("equipos", equipos);

        if (!equipos.isEmpty()) {
            model.addAttribute("registroEquipos", equipos.get(0));
        }

        return "instructor/registroEquipos/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("registroEquipos", new RegistroEquipos());
        model.addAttribute("users", userRepository.findAll()); // Puedes filtrar si deseas
        model.addAttribute("tipos", TipoEquipo.values());
        return "instructor/registroEquipos/create";
    }

    @PostMapping("/guardar")
    public String saveRegistroEquipos(@ModelAttribute RegistroEquipos registroEquipos,
                                      RedirectAttributes redirectAttributes) {
        registroEquiposService.saveRegistroEquipos(registroEquipos);
        return "redirect:/instructor/registroEquipos";
    }

}
