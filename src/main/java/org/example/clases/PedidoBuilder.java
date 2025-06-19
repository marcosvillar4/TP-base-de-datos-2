package org.example.clases;


import java.util.*;
import java.time.*;

public class PedidoBuilder {
    public static class Totales{
        public double subtotal, descuentos, impuestos, total;
    }

    //Metodo principal: construye un Pedido completo
    public static Pedido buildPedido(Carrito carrito, Usuario usuario){
        Totales totales = new Totales();
        List<ItemPedido> items = new ArrayList<>();

        for(ItemCarrito itemCarrito : carrito.getCarrito()){
            ItemPedido item = convertirItem(itemCarrito, usuario);
            items.add(item);

            double precioTotalItem = (item.getPrecioUnitario() - item.getDescuentoAplicado()) * item.getCantidad();
            totales.subtotal += precioTotalItem;
            totales.descuentos += item.getDescuentoAplicado() * item.getCantidad();
        }

        totales.impuestos = calcularImpuesto(totales.subtotal, usuario.getCondicionIVA());
        totales.total = totales.subtotal + totales.impuestos;

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setItems(items);
        pedido.setFecha(LocalDateTime.now());
        pedido.setSubtotal(totales.subtotal);
        pedido.setDescuentos(totales.descuentos);
        pedido.setImpuestos(totales.impuestos);
        pedido.setTotal(totales.total);

        return pedido;
    }

    private static ItemPedido convertirItem(ItemCarrito itemcarrito, Usuario usuario){
        Producto producto = itemcarrito.getProducto();
        int cantidad = itemcarrito.getCantidad();
        double precioUnitario = producto.getPrecioActual();
        double descuento = calcularDescuento(producto, usuario, cantidad);

        return new ItemPedido(producto, cantidad, precioUnitario, descuento);
    }

    private static double calcularDescuento(Producto p, Usuario u, int cantidad){
        return cantidad >= 10 ? p.getPrecioActual() * 0.1 : 0;
    }

    private static double calcularImpuesto(double monto, String condicionIVA){
        return condicionIVA.equalsIgnoreCase("Responsable Inscripto") ? monto * 0.21 : 0;
    }
}
