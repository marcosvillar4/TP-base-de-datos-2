package org.example.clases.Producto;

import java.util.*;

public class ProductoCatalogo {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private List<String> comentarios;
    private List<CambioProducto> historialCambios;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<String> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<String> comentarios) {
        this.comentarios = comentarios;
    }

    public List<CambioProducto> getHistorialCambios() {
        return historialCambios;
    }

    public void setHistorialCambios(List<CambioProducto> historialCambios) {
        this.historialCambios = historialCambios;
    }
}
