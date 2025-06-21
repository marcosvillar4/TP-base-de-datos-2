package org.example.clases.Carrito;

import org.example.clases.Enums.EstadoCarrito;
import org.example.clases.Producto.Producto;

import java.util.*;



public class Carrito {

    private LinkedList<ItemCarrito> carrito;
    private EstadoCarrito estado;

    public Carrito() {
        carrito = new LinkedList<>();
        estado = EstadoCarrito.PENDIENTE;
    }

    public LinkedList<ItemCarrito> getCarrito() {
        return carrito;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public void agregarItem(Producto producto, int cantidad) {
        for(ItemCarrito item : carrito){
            if(item.getProducto().getIdProducto() == producto.getIdProducto()){
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }

        ItemCarrito nuevo = new ItemCarrito(producto, cantidad);
        nuevo.setId(carrito.size() + 1);
        carrito.add(nuevo);
    }

    public void eliminarItem(int idProducto){
        carrito.removeIf(item -> item.getProducto().getIdProducto() == idProducto);
    }

    public void modificarCantidad(int idProducto, int cantidad){
        for(ItemCarrito item : carrito){
            if(item.getProducto().getIdProducto() == idProducto){
                item.setCantidad(cantidad);
                return;
            }
        }
    }

    public void vaciarCarrito(){
        carrito.clear();
    }
}
