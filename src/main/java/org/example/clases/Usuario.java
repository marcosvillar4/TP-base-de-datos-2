package org.example.clases;

import java.util.LinkedList;

public class Usuario {

    int id;
    int dni;
    String nombre;
    String direccion;
    LinkedList<Pedido> pedidos;
    String categoria;

    public Usuario(int dni, String nombre, String direccion, LinkedList<Pedido> pedidos, String categoria) {
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.pedidos = pedidos;
        this.categoria = categoria;
    }

    public Usuario() {
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LinkedList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(LinkedList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
