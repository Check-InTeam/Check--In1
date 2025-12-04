package com.Check_In.Check__In1.patrones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditoriaSingleton {

    // Instancia única (volatile asegura visibilidad en hilos)
    private static volatile AuditoriaSingleton instancia;

    // Lista sincronizada para evitar problemas en concurrencia
    private final List<String> registros;

    // Constructor privado
    private AuditoriaSingleton() {
        this.registros = Collections.synchronizedList(new ArrayList<>());
    }

    // Método para obtener la instancia única (thread-safe con double-checked locking)
    public static AuditoriaSingleton getInstance() {
        if (instancia == null) {
            synchronized (AuditoriaSingleton.class) {
                if (instancia == null) {
                    instancia = new AuditoriaSingleton();
                }
            }
        }
        return instancia;
    }

    // Registrar un evento en la auditoría
    public void registrar(String mensaje) {
        registros.add(mensaje);
        System.out.println("[AUDITORÍA] " + mensaje);
    }


    public List<String> getRegistros() {
        return new ArrayList<>(registros);
    }


    public void limpiar() {
        registros.clear();
    }
}