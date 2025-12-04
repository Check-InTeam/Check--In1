package com.Check_In.Check__In1.entity;

public enum TipoJustificacion {
    CITA_MEDICA("Cita Medica"),
    INCAPACIDAD("Incapacidad"),
    OTRO("Otro");

    private final String dbValue;

    TipoJustificacion(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
