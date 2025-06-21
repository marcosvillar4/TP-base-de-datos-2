package org.example.clases.Factura;

import java.util.*;
import jakarta.persistence.*;
import org.example.clases.Enums.EstadoFactura;
import org.example.clases.Pedido.Pedido;

@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private Pedido pedido;

    private double subtotal;
    private double total;
    private double impuestos;

    @OneToMany
    private List<Pago> pagos = new ArrayList<>();

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
    public List<Pago> getPagos() {
        return pagos;
    }
    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
    public EstadoFactura getEstado() {
        return estado;
    }
    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }
    public int getId(){ return id;}
}
