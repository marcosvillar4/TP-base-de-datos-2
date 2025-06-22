package org.example.clases.Carrito;

import org.example.clases.Producto.Producto;

public class ItemCarrito {
    int id;
    String idProducto;
    int cantidad;

    public ItemCarrito(String producto, int cantidad) {
        this.idProducto = producto;
        this.cantidad = cantidad;
    }


    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


}
