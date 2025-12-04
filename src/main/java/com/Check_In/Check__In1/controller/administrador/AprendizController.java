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
@RequestMapping("/administrador/aprendices")
public class AprendizController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public String indexAprendices(@RequestParam(name = "nombre", required = false) String nombre, Model model, HttpSession session) {

        Role roleAprendiz = roleRepository.findByNombreIgnoreCase("APRENDIZ");
        List<User> aprendiz;

        if (nombre!=null && !nombre.isEmpty()) {
            aprendiz = userRepository.findByRoleAndNombreContainingIgnoreCase(roleAprendiz, nombre);
        } else {
            aprendiz = userRepository.findByRole(roleAprendiz);
        }
        model.addAttribute("users", aprendiz);
        model.addAttribute("paramNombre", nombre);

        User user = (User)  session.getAttribute("usuarioLogueado");
        model.addAttribute("user", user);

        return "administrador/aprendices/index";
    }
    @GetMapping("/create")
    public String mostrarFormulario(Model model) {
        model.addAttribute("aprendiz", new User());
        return "administrador/aprendices/create"; //
    }

    @PostMapping("/guardar")
    public String guardarAprendiz(@ModelAttribute User aprendiz, RedirectAttributes redirectAttributes) {
        Role roleAprendiz = roleRepository.findByNombreIgnoreCase("APRENDIZ");

        if (roleAprendiz != null) {
            aprendiz.setRole(roleAprendiz);
            userRepository.save(aprendiz);
            redirectAttributes.addFlashAttribute("mensajeExito", "El aprendiz fue creado exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "El aprendiz no existe");
        }
        return "redirect:/administrador/aprendices";
    }

    @GetMapping("/editar/{id}")
    public String editarAprendiz(@PathVariable("id") Long id, Model model) {
        Optional<User> aprendizOpt = userRepository.findById(id);
        if (aprendizOpt.isPresent()) {
            model.addAttribute("aprendiz", aprendizOpt.get());
            return "administrador/aprendices/edit";
        } else {
            return "redirect:/administrador/aprendices";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarAprendiz(@PathVariable("id") Long id, @ModelAttribute("aprendiz") User aprendizActualizado, RedirectAttributes redirectAttributes) {
        Optional<User> aprendizOpt = userRepository.findById(id);
        if (aprendizOpt.isPresent()) {
            User aprendiz = aprendizOpt.get();
            aprendiz.setNombre(aprendizActualizado.getNombre());
            aprendiz.setEmail(aprendizActualizado.getEmail());
            aprendiz.setPassword(aprendizActualizado.getPassword());

            userRepository.save(aprendiz);
            redirectAttributes.addFlashAttribute("mensajeExito", "El aprendiz fue actualizado");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontro el aprendiz para poder actualizarlo");
        }
        return "redirect:/administrador/aprendices";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAprendiz(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "El aprendiz fue eliminado correctamente.");
        return "redirect:/administrador/aprendices";
    }
}

