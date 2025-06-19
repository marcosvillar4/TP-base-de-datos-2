package org.example.clases;

import jakarta.persistence.*;
@Entity
public class ItemPedido {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Producto producto;

    private int cantidad;
    private double precioUnitario;
    private double descuentoAplicado;

    //Constructor

    public ItemPedido(Producto producto, int cantidad, double precioUnitario, double descuentoAplicado) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(double descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
}
