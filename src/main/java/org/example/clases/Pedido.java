package org.example.clases;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;


@Entity
public class Pedido {

    @Id
    int id;

    private Date fecha;
    private int idUsuario;


    @Transient
    private Carrito carrito;

    @OneToMany
    List<Producto> productoList;


    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
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
