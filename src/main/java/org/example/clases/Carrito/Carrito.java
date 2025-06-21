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
            if(item.getProducto().getId().equals(producto.getId())){
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }

        ItemCarrito nuevo = new ItemCarrito(producto, cantidad);
        nuevo.setId(carrito.size() + 1);
        carrito.add(nuevo);
    }

    public void eliminarItem(String idProducto){
        carrito.removeIf(item -> item.getProducto().getId().equals(idProducto));
    }

    public void modificarCantidad(String idProducto, int cantidad){
        for(ItemCarrito item : carrito){
            if(item.getProducto().getId().equals(idProducto) ){
                item.setCantidad(cantidad);
                return;
            }
        }
    }

    public void vaciarCarrito(){
        carrito.clear();
    }
}
