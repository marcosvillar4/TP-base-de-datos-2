package org.example.clases;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private String comentariosProducto;
    private double precioActual;

    public String getNombreProducto() {
        return nombreProducto;
    }
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    public String getDescripcionProducto() {
        return descripcionProducto;
    }
    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }
    public String getComentariosProducto() {
        return comentariosProducto;
    }
    public void setComentariosProducto(String comentariosProducto) {
        this.comentariosProducto = comentariosProducto;
    }
    public int getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    public void setPrecioActual(double precioActual) {this.precioActual = precioActual;}
    public double getPrecioActual() {return precioActual;}
}
