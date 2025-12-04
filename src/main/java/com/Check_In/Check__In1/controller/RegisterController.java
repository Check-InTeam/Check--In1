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
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model, HttpSession session) {
        try{
            if (userService.emailExists(user.getEmail())) {
                model.addAttribute("error", "Este correo ya se encuentra registrado");
                return "register";
            }

            //guardar el usuario con rol asignado
        User newUser = userService.saveUser(user);
        session.setAttribute("usuarioLogueado", newUser);

        //redirigir segun el rol
        String role = newUser.getRole().getNombre();
        if ("ADMINISTRADOR".equalsIgnoreCase(role)) {
            return "redirect:/administrador/dashboard";
        } else if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            return "redirect:/instructor/dashboard";
        } else {
            return "redirect:/aprendiz/dashboard";
        }
    } catch (IllegalArgumentException e) {
        //Capturar el error cuando correo no coincide
        model.addAttribute("error", e.getMessage());
        return "register";
        } catch (Exception e){
            // Captura cualquier otro errror
            model.addAttribute("error", "Ocurrió un error al registrar el usuario.");
            return "register";
        }
    }
}

