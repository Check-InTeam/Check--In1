package com.Check_In.Check__In1.controller.administrador;


import com.Check_In.Check__In1.entity.Programacion;
import com.Check_In.Check__In1.entity.User;
import com.Check_In.Check__In1.patrones.AuditoriaSingleton;
import com.Check_In.Check__In1.repository.UserRepository;
import com.Check_In.Check__In1.service.ProgramacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/administrador/programming")
public class ProgramacionAdministradorController {

    @Autowired
    private ProgramacionService programacionService;

    @Autowired
    private UserRepository userRepository;

    // ADMINISTRADOR
    @GetMapping("")
    public String indexProgramming(@RequestParam(required = false) String ficha,
                                   @RequestParam(required = false) String nombreAsignatura,
                                   Model model,
                                   @ModelAttribute("messageExit") String messageExit,
                                   @ModelAttribute("messageError") String messageError) {

        List<Programacion> resultados;

        if (ficha != null && !ficha.isEmpty()) {
            resultados = programacionService.getProgramacionByFicha(ficha);
        } else if (nombreAsignatura != null && !nombreAsignatura.isEmpty()) {
            resultados = programacionService.getProgramacionByNombreAsignatura(nombreAsignatura);
        } else {
            resultados = programacionService.getAllProgramacion();
        }

        model.addAttribute("programming", resultados);

        if (messageExit != null && !messageExit.isEmpty()) {
            model.addAttribute("messageExit", messageExit);
        }
        if (messageError != null && !messageError.isEmpty()) {
            model.addAttribute("messageError", messageError);
        }

        return "administrador/programming/index";
    }


    @GetMapping("/create")
    public String createProgrammingForm(Model model) {
        model.addAttribute("programming", new Programacion());
        model.addAttribute("users", userRepository.findByRoleNombre("INSTRUCTOR"));
        return "/administrador/programming/create";
    }

    @PostMapping
    public String saveProgramming(@ModelAttribute("programming") Programacion programacion,
                                  @RequestParam("user.id") Long userId,
                                  RedirectAttributes redirectAttributes) {
        userRepository.findById(userId).ifPresent(programacion::setUser);
        programacionService.saveProgramacion(programacion);

        AuditoriaSingleton.getInstance().registrar(
                "Programación creada manualmente: " + programacion.getNombreAsignatura()
        );

        redirectAttributes.addFlashAttribute("messageExit", "¡Programación creada correctamente!");
        return "redirect:/administrador/programming";
    }

    @GetMapping("/edit/{id}")
    public String editProgramming(@PathVariable int id, Model model) {
        Optional<Programacion> programmingOpt = programacionService.getProgramacionById(id);
        if (programmingOpt.isPresent()) {
            model.addAttribute("programming", programmingOpt.get());
            model.addAttribute("users", userRepository.findByRoleNombre("INSTRUCTOR"));
            return "administrador/programming/edit";
        } else {
            return "redirect:/administrador/programming";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String updateProgramming(@PathVariable int id,
                                    @ModelAttribute Programacion programmingUpdate,
                                    RedirectAttributes redirectAttributes) {
        Optional<Programacion> programmingOpt = programacionService.getProgramacionById(id);
        if (programmingOpt.isPresent()) {
            Programacion programming = programmingOpt.get();

            programming.setNombreAsignatura(programmingUpdate.getNombreAsignatura());
            programming.setDescripcion(programmingUpdate.getDescripcion());
            programming.setFicha(programmingUpdate.getFicha());
            programming.setFechaInicio(programmingUpdate.getFechaInicio());
            programming.setFechaFin(programmingUpdate.getFechaFin());
            programming.setHoraInicio(programmingUpdate.getHoraInicio());
            programming.setHoraFin(programmingUpdate.getHoraFin());
            programming.setAmbiente(programmingUpdate.getAmbiente());
            programming.setUser(programmingUpdate.getUser());

            programacionService.saveProgramacion(programming);

            AuditoriaSingleton.getInstance().registrar(
                    "Programación actualizada: " + programming.getNombreAsignatura()
            );

            redirectAttributes.addFlashAttribute("messageExit", "¡La programación fue actualizada correctamente!");
        } else {
            redirectAttributes.addFlashAttribute("messageError", "No se encontró esa programación para poder actualizarla");
        }
        return "redirect:/administrador/programming";
    }


    @PostMapping("/eliminar/{id}")
    public String deleteProgramming(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            programacionService.deleteProgramacion(id);

            AuditoriaSingleton.getInstance().registrar(
                    "Programación eliminada con id: " + id
            );

            redirectAttributes.addFlashAttribute("messageExit", "¡Programación eliminada correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageError", "Ocurrió un error al eliminar la programación");
        }
        return "redirect:/administrador/programming";
    }


    @GetMapping("/show/{id}")
    public String showProgramming(@PathVariable int id, Model model) {
        Optional<Programacion> programmingOpt = programacionService.getProgramacionById(id);
        if (programmingOpt.isPresent()) {
            model.addAttribute("programming", programmingOpt.get());
            return "/administrador/programming/show";
        } else {
            return "redirect:/administrador/programming";
        }
    }


    // SUBIR CSV o Excel
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            User user = (User) session.getAttribute("usuarioLogueado");

            if (user == null) {
                redirectAttributes.addFlashAttribute("messageError", "No hay un usuario en sesión. Inicie sesión primero.");
                return "redirect:/login";
            }

            List<Programacion> programaciones = programacionService.cargarDesdeArchivo(file, user);

            AuditoriaSingleton.getInstance().registrar(
                    "Carga masiva de programaciones (" + programaciones.size() + ")"
            );

            redirectAttributes.addFlashAttribute("messageExit",
                    "¡Se cargaron " + programaciones.size() + " programaciones correctamente!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageError",
                    "Error al cargar el archivo: " + e.getMessage());
        }

        return "redirect:/administrador/programming";
    }


}

