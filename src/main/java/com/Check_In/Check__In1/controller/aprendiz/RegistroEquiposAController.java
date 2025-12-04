package com.Check_In.Check__In1.controller.aprendiz;


import com.Check_In.Check__In1.entity.RegistroEquipos;
import com.Check_In.Check__In1.entity.TipoEquipo;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.RegistroEquiposService;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/aprendiz/registroEquipos")
public class RegistroEquiposAController {

    @Autowired
    private RegistroEquiposService registroEquiposService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model, HttpSession session) {
        User aprendiz = (User) session.getAttribute("usuarioLogueado");

        if (aprendiz == null) {
            return "redirect:/login"; // por seguridad
        }

        List<RegistroEquipos> equipos = registroEquiposService.getEquiposByUser(aprendiz);
        model.addAttribute("equipos", equipos);

        if (!equipos.isEmpty()) {
            model.addAttribute("registroEquipos", equipos.get(0));
        }

        return "aprendiz/registroEquipos/index";
    }


    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("registroEquipos", new RegistroEquipos());
        model.addAttribute("users", userRepository.findAll()); // Puedes filtrar si deseas
        model.addAttribute("tipos", TipoEquipo.values());
        return "aprendiz/registroEquipos/create";
    }

    @PostMapping("/guardar")
    public String saveRegistroEquipo(@ModelAttribute RegistroEquipos registroEquipos,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User aprendiz = (User) session.getAttribute("usuarioLogueado");

        if (aprendiz == null) {
            return "redirect:/login";
        }

        registroEquipos.setUser(aprendiz);
        registroEquiposService.saveRegistroEquipos(registroEquipos);

        return "redirect:/aprendiz/registroEquipos";
    }

}

