package org.example.clases;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.*;
import java.time.*;
import jakarta.persistence.*;


@Entity
@Entity
public class Pedido {
    @Id
    @GeneratedValue
    private int idPedido;

    @ManyToOne
    private Usuario usuario;

    @Id
    int id;

    private Date fecha;
    private int idUsuario;


    @Transient
    private Carrito carrito;
    private double subtotal;
    private double impuestos;
    private double total;
    private double descuentos;
    private LocalDateTime fecha;

    @OneToMany
    List<Producto> productoList;


    public Date getFecha() {
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

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Carrito getCarrito() {
        return carrito;
    }
    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public void cerrarCarrito(){
        for (ItemCarrito itemCarrito : carrito.getCarrito()) {
            for (int i = 0; i < itemCarrito.getCantidad(); i++) {
                productoList.add(itemCarrito.getProducto());
            }
        }
    }

}
