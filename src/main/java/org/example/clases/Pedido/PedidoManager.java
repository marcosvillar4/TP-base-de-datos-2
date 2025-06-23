package org.example.clases.Pedido;

import jakarta.persistence.EntityManager;
import org.bson.Document;
import org.example.clases.Carrito.Carrito;
import org.example.clases.Carrito.CarritoManager;
import org.example.clases.Enums.EstadoFactura;
import org.example.clases.Enums.MedioPago;
import org.example.clases.Factura.Factura;
import org.example.clases.Factura.Pago;
import org.example.clases.Mongo.MongoManager;
import org.example.clases.Mongo.ProductoCatalogoDAO;
import org.example.clases.Mongo.ProductoCatalogoService;
import org.example.clases.Usuario.Usuario;

import java.time.LocalDateTime;
import java.util.*;

public class PedidoManager {
    private final EntityManager em;
    private final CarritoManager carritoManager;

    public PedidoManager(EntityManager em, CarritoManager carritoManager) {
        this.em = em;
        this.carritoManager = carritoManager;
    }

    // Listado de pagos realizados por un usuario
    public List<Pago> listarPagos(String idUsuario) {

        String jpql = """
                SELECT DISTINCT p FROM Usuario u
                JOIN u.pagos p
                WHERE u.id = :idUsuario
                """;

        return em.createQuery(jpql, Pago.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    // Listado completo de facturas del usuario
    public List<Factura> listarFacturas(String idUsuario) {
        String jpql = """
                SELECT f FROM Usuario u JOIN u.facturas f
                                WHERE u.id=:idUsuario
                """;


        return em.createQuery(jpql, Factura.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    // Genera un pedido a partir del carrito del usuario y lo persiste
    /*
    public void generarYGuardarPedido(String idUsuario, Usuario usuario) {
        Carrito carrito = carritoManager.obtenerCarrito(idUsuario);


        Pedido pedido = new Pedido();

        pedido.setCarrito(carrito);
        pedido.setFecha(LocalDateTime.now());

        ProductoCatalogoDAO productoCatalogoDAO = new ProductoCatalogoDAO(MongoManager.getDatabase());

        double subtotal = 0;
        double iva = 0;
        double total = 0;

        ProductoCatalogoService productoCatalogoService = new ProductoCatalogoService(MongoManager.getDatabase().getCollection("productos"));
        for (String s : carrito.getCarrito().keySet()) {
            subtotal = subtotal + carrito.getCarrito().get(s) * Double.parseDouble(productoCatalogoDAO.getProductoById(s).get("precio").toString());
            boolean ok = productoCatalogoService.descontarStock(s, carrito.getCarrito().get(s), usuario.getId());
            if (!ok) {
                System.out.println("No hay suficiente stock para el producto con id: " + s);
            }
        }
        pedido.setCarritoFinal(carrito.getCarrito());

        switch(usuario.getCondicionIVA()){
            case "Responsable inscripto":
                iva = subtotal * 0.21;
                total = subtotal + iva;
                break;
            case "Consumidor final":
            case "Monotributista":
                //El precio total ya incluye el IVA, se calcula al reves
                // Si el subtotal es neto, para mostrtar el total con iva:
                iva = subtotal * 0.21;
                total = subtotal + iva; // El total ya incluye iva
                iva = 0;
                break;
            case "Exento":
                total = subtotal;
                iva = 0;
                break;
        }

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(iva);
        pedido.setTotal(total);

        facturarPedido(pedido, usuario);

        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();


    }*/

    public boolean generarYGuardarPedido(String idUsuario, Usuario usuario) {
        Carrito carrito = carritoManager.obtenerCarrito(idUsuario);

        Pedido pedido = new Pedido();

        pedido.setCarrito(carrito);
        pedido.setFecha(LocalDateTime.now());

        ProductoCatalogoDAO productoCatalogoDAO = new ProductoCatalogoDAO(MongoManager.getDatabase());
        ProductoCatalogoService productoCatalogoService = new ProductoCatalogoService(MongoManager.getDatabase().getCollection("productos"));

        double subtotal = 0;
        double iva = 0;
        double total = 0;

        // chequea si hay stock suficiente para TODOS los productos:
        boolean stockOk = true;
        for (String s : carrito.getCarrito().keySet()) {
            int cantidadDeseada = carrito.getCarrito().get(s);
            Document prod = productoCatalogoDAO.getProductoById(s);
            int stock = Integer.parseInt(prod.get("cantidad").toString());
            if (stock < cantidadDeseada) {
                System.out.println("No hay suficiente stock para el producto: " + prod.get("nombre"));
                stockOk = false;
            }
        }

        if (!stockOk) {
            System.out.println("El pedido no se pudo completar por falta de stock.");
            return false;
        }

        // Si hay stock, descontar de forma segura
        for (String s : carrito.getCarrito().keySet()) {
            int cantidad = carrito.getCarrito().get(s);

            // metodo seguro
            boolean ok = productoCatalogoService.descontarStock(s, cantidad, usuario.getId());
            if (!ok) {
                System.out.println("Otro usuario se adelantó y no queda suficiente stock del producto con id: " + s);
                System.out.println("El pedido no se pudo completar.");
                return false;
            }

            subtotal += cantidad * Double.parseDouble(productoCatalogoDAO.getProductoById(s).get("precio").toString());
        }

        pedido.setCarritoFinal(carrito.getCarrito());

        // Cálculo de impuestos
        switch(usuario.getCondicionIVA()){
            case "Responsable inscripto":
                iva = subtotal * 0.21;
                total = subtotal + iva;
                break;
            case "Consumidor final":
            case "Monotributista":
                iva = subtotal * 0.21;
                total = subtotal + iva;
                iva = 0;
                break;
            case "Exento":
                total = subtotal;
                iva = 0;
                break;
        }

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(iva);
        pedido.setTotal(total);

        facturarPedido(pedido, usuario);

        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
        return true;
    }

    // Genera y persiste una factura para un pedido
    private Factura facturarPedido(Pedido pedido, Usuario currentUser) {
        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setSubtotal(pedido.getSubtotal());
        factura.setImpuestos(pedido.getImpuestos());
        factura.setTotal(pedido.getTotal());
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setId(UUID.randomUUID().toString());
        currentUser.getFacturas().add(factura);

        em.getTransaction().begin();
        em.persist(factura);
        em.persist(pedido);
        em.getTransaction().commit();

        return factura;
    }

    // Registra un pago por una lista de facturas
    public Pago registrarPago(List<Factura> facturas, MedioPago medioPago, String operador, Usuario currentUser) {
        double montoTotal = facturas.stream().mapToDouble(Factura::getTotal).sum();

        Pago pago = new Pago();
        pago.setId(UUID.randomUUID().toString());
        pago.setFecha(LocalDateTime.now());
        pago.setMonto(montoTotal);
        pago.setMedioPago(medioPago);
        pago.setOperador(operador);
        pago.setFacturasAplicadas(facturas);
        em.getTransaction().begin();

        for (Factura f : facturas) {
            f.setEstado(EstadoFactura.PAGADA);
        }
        //em.persist(pago.getFacturasAplicadas());
        currentUser.getPagos().add(pago);


        em.persist(pago);
        em.getTransaction().commit();

        return pago;
    }
}