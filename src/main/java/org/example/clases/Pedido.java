package org.example.clases;

import java.util.*;
import java.time.*;
import jakarta.persistence.*;

@Entity
public class Pedido {
    @Id
    @GeneratedValue
    private int idPedido;

    @ManyToOne
    private Usuario usuario;

    private double subtotal;
    private double impuestos;
    private double total;
    private double descuentos;
    private LocalDateTime fecha;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemPedido> items;

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemPedido> getItems() {
        return items;
    }
    public void setItems(List<ItemPedido> items) {
        this.items = items;
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
    public void setDescuentos(double descuentos) {this.descuentos = descuentos;}
    public double getDescuentos() {return descuentos;}
}
