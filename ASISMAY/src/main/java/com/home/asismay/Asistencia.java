package com.home.asismay;

public class Asistencia {
    private String nombre;
    private String asistencia;
    private String[] asistenciaDias;

    public Asistencia(String nombre, String asistencia) {
        this.nombre = nombre;
        this.asistencia = asistencia;
        this.asistenciaDias = new String[31]; // Un arreglo para los 31 días del mes
        // Inicializa todos los días con un valor predeterminado (por ejemplo, "No Asistió")
        for (int i = 0; i < asistenciaDias.length; i++) {
            asistenciaDias[i] = "No Asistió";
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getAsistencia() {
        return asistencia;
    }

    public String getAsistenciaDia(int dia) {
        // Retorna la asistencia del trabajador para un día específico
        if (dia >= 1 && dia <= asistenciaDias.length) {
            return asistenciaDias[dia - 1]; // El arreglo comienza en 0, por lo que restamos 1
        }
        return "No Asistió";
    }

    public void setAsistenciaDia(int dia, String estado) {
        if (dia >= 1 && dia <= asistenciaDias.length) {
            asistenciaDias[dia - 1] = estado;
        }
    }
}
