package com.Check_In.Check__In1.controller.administrador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrador")
public class ScanController {

    @GetMapping("/scan-carnet")
    public String scan() {
        return "administrador/scan-carnet";   // <-- coincide con templates/administrador/scan-carnet.html
    }
}
