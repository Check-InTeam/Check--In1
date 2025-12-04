package com.Check_In.Check__In1.controller.administrador;


import com.Check_In.Check__In1.entity.Role;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.repository.RoleRepository;
import com.Check_In.Check__In1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/administrador/instructors")
public class InstructorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public String indexInstructors(@RequestParam(name = "nombre", required = false) String nombre, Model model, HttpSession session) {

        Role roleInstructor = roleRepository.findByNombreIgnoreCase("INSTRUCTOR");
        List<User> instructors;

        if (nombre!=null && !nombre.isEmpty()) {
            instructors = userRepository.findByRoleAndNombreContainingIgnoreCase(roleInstructor, nombre);
        } else {
            instructors = userRepository.findByRole(roleInstructor);
        }
        model.addAttribute("users", instructors);
        model.addAttribute("paramNombre", nombre);

        User user = (User)  session.getAttribute("usuarioLogueado");
        model.addAttribute("user", user);

        return "administrador/instructors/index";
    }
    @GetMapping("/create")
    public String mostrarForm(Model model) {
        model.addAttribute("instructors", new User());
        return "administrador/instructors/create"; //
    }

    @PostMapping("/guardar")
    public String guardarInstructores(@ModelAttribute User instructors, RedirectAttributes redirectAttributes) {
        Role roleInstructors = roleRepository.findByNombreIgnoreCase("INSTRUCTOR");

        if (roleInstructors != null) {
            instructors.setRole(roleInstructors);
            userRepository.save(instructors);
            redirectAttributes.addFlashAttribute("mensajeExito", "El instructor fue creado exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "El instructor no existe");
        }
        return "redirect:/administrador/instructors";
    }

    @GetMapping("/editar/{id}")
    public String editInstructors(@PathVariable("id") Long id, Model model) {
        Optional<User> instructorsOpt = userRepository.findById(id);
        if (instructorsOpt.isPresent()) {
            model.addAttribute("instructors", instructorsOpt.get());
            return "administrador/instructors/edit";
        } else {
            return "redirect:/administrador/instructors";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarInstructors(@PathVariable("id") Long id, @ModelAttribute("instructors") User instructorsActualizado, RedirectAttributes redirectAttributes) {
        Optional<User> instructorsOpt = userRepository.findById(id);
        if (instructorsOpt.isPresent()) {
            User instructors = instructorsOpt.get();
            instructors.setNombre(instructorsActualizado.getNombre());
            instructors.setEmail(instructorsActualizado.getEmail());
            instructors.setPassword(instructorsActualizado.getPassword());

            userRepository.save(instructors);
            redirectAttributes.addFlashAttribute("mensajeExito", "El instructor fue actualizado");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontro el instructor para poder actualizarlo");
        }
        return "redirect:/administrador/instructors";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarInstructors(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "El instructor fue eliminado correctamente.");
        return "redirect:/administrador/instructors";
    }
}

