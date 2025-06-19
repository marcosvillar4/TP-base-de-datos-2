package org.example.clases;

import java.time.LocalDateTime;
import java.time.Duration;

public class Sesion {
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private Duration duracion;

    public Sesion(LocalDateTime inicio) {
       this.inicio = inicio;
    }

    public void cerrarSesion(){
        this.fin = LocalDateTime.now();
        this.duracion = Duration.between(this.inicio, this.fin);
    }

    public long getDuracionEnMinutos(){
        return duracion != null ? this.duracion.toMinutes() : 0;
    }

    //Getters y setters
    public LocalDateTime getInicio() {return inicio;}
    public LocalDateTime getFin() {return fin;}
}
