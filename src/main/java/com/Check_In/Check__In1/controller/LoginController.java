package com.Check_In.Check__In1.controller;


import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("usuario", new User());
        return "login";
    }


    @PostMapping("/login")
    public String processLogin(@ModelAttribute("usuario") User usuario, Model model, HttpSession session) {

        User user = userService.authenticate(usuario.getEmail(), usuario.getPassword());

        if (user != null) {

            session.setAttribute("usuarioLogueado", user);
            String role = user.getRole().getNombre();

            if ("ADMINISTRADOR".equalsIgnoreCase(role)) {
                return "redirect:/administrador/dashboard";
            } else if ("INSTRUCTOR".equalsIgnoreCase(role)) {
                return "redirect:/instructor/dashboard";
            } else {
                return "redirect:/aprendiz/dashboard";
            }
        } else {
            System.out.println("Credenciales inválidas");
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
    }
}

