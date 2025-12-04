package com.Check_In.Check__In1.controller.administrador;


import com.Check_In.Check__In1.entity.RegistroEquipos;
import com.Check_In.Check__In1.entity.TipoEquipo;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.RegistroEquiposService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/administrador/registroEquipos")
public class RegistroEquiposController {

    @Autowired
    private RegistroEquiposService registroEquiposService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String indexRegistroEquipos(@RequestParam(required = false) String usuario,
                                       @RequestParam(required = false) String serialEquipo,
                                       Model model) {

        List<RegistroEquipos> equipos = registroEquiposService.getAllRegistroEquipos();

        if (usuario != null && !usuario.isEmpty()) {
            equipos = equipos.stream()
                    .filter(e -> e.getUser() != null &&
                            e.getUser().getNombre() != null &&
                            e.getUser().getNombre().toLowerCase().contains(usuario.toLowerCase()))
                    .toList();
        }

        if (serialEquipo != null && !serialEquipo.isEmpty()) {
            equipos = equipos.stream()
                    .filter(e -> e.getSerialEquipo() != null &&
                            e.getSerialEquipo().contains(serialEquipo))
                    .toList();
        }

        model.addAttribute("equipos", equipos);
        return "administrador/registroEquipos/index";
    }


    // FORMULARIO CREAR
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("registroEquipos", new RegistroEquipos());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("tipos", TipoEquipo.values());
        return "administrador/registroEquipos/create";
    }

    // GUARDAR
    @PostMapping
    public String saveRegistroEquipo(@ModelAttribute RegistroEquipos registroEquipos,
                                     @RequestParam("user.id") Long userId,
                                     RedirectAttributes redirectAttributes) {
        userRepository.findById(userId).ifPresent(registroEquipos::setUser);
        registroEquiposService.saveRegistroEquipos(registroEquipos);
        redirectAttributes.addFlashAttribute("messageExit", "¡Equipo registrado correctamente!");
        return "redirect:/administrador/registroEquipos";
    }

    // FORMULARIO EDITAR
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        Optional<RegistroEquipos> equipoOpt = registroEquiposService.getRegistroEquiposById(id);
        if (equipoOpt.isPresent()) {
            model.addAttribute("registroEquipos", equipoOpt.get());
            model.addAttribute("users", userRepository.findAll());
            return "administrador/registroEquipos/edit";  // Sin slash inicial
        } else {
            return "redirect:/administrador/registroEquipos";
        }
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String updateRegistroEquipo(@PathVariable int id,
                                       @ModelAttribute RegistroEquipos registroActualizado,
                                       RedirectAttributes redirectAttributes) {
        Optional<RegistroEquipos> equipoOpt = registroEquiposService.getRegistroEquiposById(id);
        if (equipoOpt.isPresent()) {
            RegistroEquipos actual = equipoOpt.get();
            actual.setTipo(registroActualizado.getTipo());
            actual.setMarca(registroActualizado.getMarca());
            actual.setSerialEquipo(registroActualizado.getSerialEquipo());
            actual.setDescripcion(registroActualizado.getDescripcion());
            actual.setUser(registroActualizado.getUser());

            registroEquiposService.saveRegistroEquipos(actual);
            redirectAttributes.addFlashAttribute("messageExit", "¡Equipo actualizado correctamente!");
        } else {
            redirectAttributes.addFlashAttribute("messageError", "Equipo no encontrado para actualizar");
        }

        return "redirect:/administrador/registroEquipos";
    }

    // ELIMINAR
    @PostMapping("/eliminar/{id}")
    public String deleteRegistroEquipo(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            registroEquiposService.deleteRegistroEquiposById(id);
            redirectAttributes.addFlashAttribute("messageExit", "¡Equipo eliminado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageError", "Error al eliminar el equipo");
        }
        return "redirect:/administrador/registroEquipos";
    }

    // MOSTRAR DETALLE
    @GetMapping("/show/{id}")
    public String showRegistroEquipo(@PathVariable int id, Model model) {
        Optional<RegistroEquipos> equipoOpt = registroEquiposService.getRegistroEquiposById(id);
        if (equipoOpt.isPresent()) {
            model.addAttribute("registroEquipos", equipoOpt.get());
            return "administrador/registroEquipos/show";  // Sin slash inicial
        } else {
            return "redirect:/administrador/registroEquipos";
        }
    }
}

