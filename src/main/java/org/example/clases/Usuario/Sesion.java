package org.example.clases.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.Duration;


@Entity
public class Sesion {


    private LocalDateTime inicio;
    private LocalDateTime fin;
    private Duration duracion;

    @Id
    private int id;

    public Sesion(LocalDateTime inicio, int id) {
        this.inicio = inicio;
        this.id = id;
    }

    public Sesion() {

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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }
}
