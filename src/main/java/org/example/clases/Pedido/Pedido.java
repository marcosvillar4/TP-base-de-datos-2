package org.example.clases.Pedido;

import jakarta.persistence.*;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

import org.example.clases.Carrito.Carrito;
import org.example.clases.Usuario.Usuario;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;

    @Transient
    private Carrito carrito;

    private double subtotal;
    private double impuestos;
    private double total;
    private double descuentos;
    private LocalDateTime fecha;


    @ElementCollection
    private Map<String, Integer> carritoFinal;


    //Getters y setters
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIdPedido() {return idPedido;}
    public void setIdPedido(int idPedido) {this.idPedido = idPedido;}
    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    public double getImpuestos() {
        return impuestos;
    }
    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public double getDescuentos() {
        return descuentos;
    }
    public void setDescuentos(double descuentos) {
        this.descuentos = descuentos;
    }
    public Carrito getCarrito() {
        return carrito;
    }
    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Map<String, Integer> getCarritoFinal() {
        return carritoFinal;
    }

    public void setCarritoFinal(Map<String, Integer> carritoFinal) {
        this.carritoFinal = carritoFinal;
    }
}
