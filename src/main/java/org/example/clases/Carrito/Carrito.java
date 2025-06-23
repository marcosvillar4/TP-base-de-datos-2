package org.example.clases.Carrito;

import org.example.clases.Enums.EstadoCarrito;

import java.util.*;



public class Carrito {

    private final Map<String, Integer> carrito;
    //private LinkedList<ItemCarrito> carrito;
    private EstadoCarrito estado;

    public Carrito() {
        carrito = new HashMap<>();
        estado = EstadoCarrito.PENDIENTE;
    }

    public Map<String, Integer> getCarrito() {
        return carrito;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public void agregarItem(String producto, int cantidad) {
        if (carrito.containsKey(producto)){
            carrito.replace(producto, carrito.get(producto) + cantidad);
        } else {
            carrito.put(producto, cantidad);
        }

        /*for(ItemCarrito item : carrito){
            if(item.getIdProducto().equals(producto)){
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }

        ItemCarrito nuevo = new ItemCarrito(producto, cantidad);
        nuevo.setId(carrito.size() + 1);
        carrito.add(nuevo);*/
    }

    public void eliminarItem(String idProducto, int cantidad){

        if (carrito.containsKey(idProducto)){
            if (cantidad <= carrito.get(idProducto)){
                carrito.replace(idProducto, carrito.get(idProducto) - cantidad);
            }
        }

        //carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
    }

    public void modificarCantidad(String idProducto, int cantidad){
        if (carrito.containsKey(idProducto)){
            if(cantidad > 0) {
                carrito.replace(idProducto, cantidad);
            } else{
                carrito.remove(idProducto);
            }
        }
    }

    public void vaciarCarrito(){
        carrito.clear();
    }

    public boolean estaVacio(){
        return carrito.isEmpty();
    }
}
