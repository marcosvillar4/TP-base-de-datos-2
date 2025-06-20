package org.example.clases;

import org.example.clases.*;

import jakarta.persistence.EntityManager;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.*;
public class PedidoManager {
    private final EntityManager em;
    private final CarritoManager carritoManager;

    public PedidoManager(EntityManager em, CarritoManager carritoManager) {
        this.em = em;
        this.carritoManager = carritoManager;
    }

    public Factura facturarPedido(Pedido pedido){
        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setSubtotal(pedido.getSubtotal());
        factura.setImpuestos(pedido.getImpuestos());
        factura.setTotal(pedido.getTotal());
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setPagos(new ArrayList<>());

        em.getTransaction().begin();
        em.persist(factura);
        em.getTransaction().commit();

        return factura;
    }

    public Pago registrarPago(Factura factura, MedioPago medioPago, String operador){
        Pago pago = new Pago();
        pago.setFecha(LocalDateTime.now());
        pago.setMonto(factura.getTotal());
        pago.setMedioPago(medioPago);
        pago.setOperador(operador);
        pago.setFacturasAplicadas(List.of(factura));

        factura.getPagos().add(pago);
        factura.setEstado(EstadoFactura.PAGADA);

        em.getTransaction().begin();
        em.persist(pago);
        em.merge(factura); // Esto actualiza la factura con el nuevo pago
        em.getTransaction().commit();

        return pago;
    }

    //Genera y persiste el pedido final
    public Pedido generarYGuardarPedido(int idUsuario, Usuario usuario) {
        //Obtiene el carrito activo
        Carrito carrito = carritoManager.obtenerCarrito(idUsuario);

        if (carrito == null || carrito.getCarrito().isEmpty()) {
            System.out.println("Carrito vacio, no se puede generar el pedido");
            return null;
        }

        //Crear pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCarrito(carrito);
        pedido.setFecha(LocalDateTime.now());

        // Calcular subtotal
        double subtotal = 0;
        for (ItemCarrito item : carrito.getCarrito()) {
            subtotal += item.getProducto().getPrecioActual() * item.getCantidad();
        }

        double impuestos = subtotal * 0.21; //IVA: 21%
        double total = subtotal + impuestos;

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(impuestos);
        pedido.setTotal(total);

        //Gnerar la lista de productos en productoList
        pedido.getProductoList();

        //Guardar pedido en la base

        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();

        return pedido;
    }

    public void cerrarPedidoYRegistrarPago(int idUsuario, Usuario usuario, MedioPago medioPago, String operador){
        Pedido pedido = generarYGuardarPedido(idUsuario, usuario);
        if(pedido == null){
            System.out.println("No se generó el pedido");
            return;
        }

        Factura factura = facturarPedido(pedido);
        registrarPago(factura, medioPago, operador);
    }

    public void listarPagos(Usuario usuario){
        String jpql = """
            SELECT DISTINCT pago FROM Pago pago
            JOIN pago.facturasAplicadas factura
            JOIN factura.pedido pedido
            WHERE pedido.usuario = :usuario
        """;

        List<Pago> pagos = em.createQuery(jpql, Pago.class).setParameter("usuario", usuario).getResultList();

        if(pagos.isEmpty()){
            System.out.println("El usuario no tiene pagos registrados");
            return;
        }

        System.out.println("PAGOS REGISTRADOS DE: " + usuario.getNombre());
        System.out.println("DNI:" + usuario.getDni());
        for (Pago pago : pagos) {
            System.out.println("---------------------");
            System.out.println("ID PAGO: " + pago.getId());
            System.out.println("Fecha: " + pago.getFecha());
            System.out.println("Monto: $" + pago.getMonto());
            System.out.println("Medio: " + pago.getMedioPago());
            System.out.println("Operador: " + (pago.getOperador() != null ? pago.getOperador() : "N/A"));
            System.out.print("Facturas asociadas (IDs): ");
            for (Factura f : pago.getFacturasAplicadas()) {
                System.out.print(f.getId() + " ");
            }
            System.out.println();
        }
    }
}
