package org.example.clases.Factura;

import java.util.*;
import jakarta.persistence.*;
import org.example.clases.Enums.EstadoFactura;
import org.example.clases.Pedido.Pedido;

@Entity
public class Factura {

    @Id
    private String id;

    @OneToOne
    private Pedido pedido;

    private double subtotal;
    private double total;
    private double impuestos;



    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    //Getters y setters
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public double getImpuestos() {
        return impuestos;
    }
    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }
    public EstadoFactura getEstado() {
        return estado;
    }
    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }
    public String getId(){ return id;}

    public void setId(String id) {
        this.id = id;
    }
}
