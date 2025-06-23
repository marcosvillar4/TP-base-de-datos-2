package org.example.clases.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
public class Sesion {


    private LocalDateTime inicio;
    private LocalDateTime fin;
    private long duracionMin;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Sesion(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public Sesion() {

    }

    public void cerrarSesion(){
        this.fin = LocalDateTime.now();
        this.duracionMin = Duration.between(this.inicio, this.fin).toMinutes();
    }

    public long getDuracionEnMinutos(){
        return duracionMin;
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
}
