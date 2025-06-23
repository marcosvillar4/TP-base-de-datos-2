package org.example.clases.Pedido;

import jakarta.persistence.EntityManager;
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
    public void generarYGuardarPedido(String idUsuario, Usuario usuario) {
        Carrito carrito = carritoManager.obtenerCarrito(idUsuario);


        Pedido pedido = new Pedido();

        pedido.setCarrito(carrito);
        pedido.setFecha(LocalDateTime.now());

        ProductoCatalogoDAO productoCatalogoDAO = new ProductoCatalogoDAO(MongoManager.getDatabase());

        double subtotal = 0;
        ProductoCatalogoService productoCatalogoService = new ProductoCatalogoService(MongoManager.getDatabase().getCollection("productos"));
        for (String s : carrito.getCarrito().keySet()) {
            subtotal = subtotal + carrito.getCarrito().get(s) * Double.parseDouble(productoCatalogoDAO.getProductoById(s).get("precio").toString());
            productoCatalogoService.actualizarCantidad(s, (Integer.parseInt(productoCatalogoDAO.getProductoById(s).get("cantidad").toString()) - carrito.getCarrito().get(s)), usuario.getId());

        }
        pedido.setCarritoFinal(carrito.getCarrito());





        double impuestos = Objects.equals(usuario.getCondicionIVA(), "Responsable inscripto") ?  subtotal * 0.21 : 0; //IVA
        double total = subtotal + impuestos;

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(impuestos);
        pedido.setTotal(total);

        facturarPedido(pedido, usuario);

        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();


    }

    // Genera y persiste una factura para un pedido
    private Factura facturarPedido(Pedido pedido, Usuario currentUser) {
        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setSubtotal(pedido.getSubtotal());
        factura.setImpuestos(pedido.getImpuestos());
        factura.setTotal(pedido.getTotal());
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setId(String.valueOf(currentUser.getFacturas().size() + 1));
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
        pago.setId(String.valueOf(currentUser.getPagos().size()+1));
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