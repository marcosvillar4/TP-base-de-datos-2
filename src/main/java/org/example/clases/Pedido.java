package org.example.clases;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.*;
import java.time.*;
import jakarta.persistence.*;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;

    @ManyToOne
    private Usuario usuario;

    @Transient
    private Carrito carrito;

    private double subtotal;
    private double impuestos;
    private double total;
    private double descuentos;
    private LocalDateTime fecha;

    @OneToMany
    List<Producto> productoList;

    private void cerrarCarrito(){
        //Inicializar si todavia productoList no esta creado
        if(productoList == null){
            productoList = new ArrayList<>();
        }

        //Vacia la lista antes de volver a llenarla
        productoList.clear();
        for (ItemCarrito itemCarrito : carrito.getCarrito()) {
            for (int i = 0; i < itemCarrito.getCantidad(); i++) {
                productoList.add(itemCarrito.getProducto());
            }
        }
    }

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

    public List<Producto> getProductoList() {
        cerrarCarrito();
        return productoList;
    }
}
