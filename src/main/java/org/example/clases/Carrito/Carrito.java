package org.example.clases.Carrito;

import java.util.*;

public class Carrito {

    private final Map<String, Integer> carrito;

    public Carrito() {
        carrito = new HashMap<>();
    }

    public Map<String, Integer> getCarrito() {
        return carrito;
    }


    public void agregarItem(String producto, int cantidad) {
        if (carrito.containsKey(producto)){
            carrito.replace(producto, carrito.get(producto) + cantidad);
        } else {
            carrito.put(producto, cantidad);
        }
    }

    public void eliminarItem(String idProducto){
        carrito.remove(idProducto);
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
