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


    // Cierra el proceso de compra: genera pedido, factura y registra el pago (incluyendo otras facturas)
    /*
    public void cerrarPedidoYRegistrarPago(int idUsuario, Usuario usuario, MedioPago medioPago, String operador) {
        Pedido pedido = generarYGuardarPedido(idUsuario, usuario);
        if (pedido == null) {
            System.out.println("No se generó el pedido");
            return;
        }

        Factura facturaNueva = facturarPedido(pedido);
        List<Factura> facturasSeleccionadas = obtenerFacturasSeleccionadas(idUsuario);
        List<Factura> todas = new ArrayList<>();
        todas.add(facturaNueva);
        todas.addAll(facturasSeleccionadas);

        registrarPago(todas, medioPago, operador);
        System.out.println("Pago registrado con éxito.");
    }
    */

    // Lista facturas pendientes del usuario
    public List<Factura> buscarFacturasPendientes(int idUsuario) {
        String jpql = """
                SELECT f FROM Factura f
                WHERE f.pedido.usuario.id = :idUsuario AND f.estado = :estado
                """;

        return em.createQuery(jpql, Factura.class)
                .setParameter("idUsuario", idUsuario)
                .setParameter("estado", EstadoFactura.PENDIENTE)
                .getResultList();
    }

    // Listado de pagos realizados por un usuario
    public List<Pago> listarPagos(String idUsuario) {

        String jpql = """
                SELECT DISTINCT p FROM Pago p
                JOIN p.facturasAplicadas f
                JOIN f.pedido ped
                WHERE ped.usuario.id = :idUsuario
                """;

        return em.createQuery(jpql, Pago.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    // Listado completo de facturas del usuario
    public List<Factura> listarFacturas(String idUsuario) {
        String jpql = """
                SELECT f FROM Factura f
                WHERE f.pedido.usuario.id = :idUsuario
                """;

        return em.createQuery(jpql, Factura.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    // Genera un pedido a partir del carrito del usuario y lo persiste
    public void generarYGuardarPedido(String idUsuario, Usuario usuario) {
        Carrito carrito = carritoManager.obtenerCarrito(idUsuario);


        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCarrito(carrito);
        pedido.setFecha(LocalDateTime.now());

        ProductoCatalogoDAO productoCatalogoDAO = new ProductoCatalogoDAO(MongoManager.getDatabase());

        double subtotal = 0;

        for (String s : carrito.getCarrito().keySet()) {
            subtotal = subtotal + carrito.getCarrito().get(s) * Double.parseDouble(productoCatalogoDAO.getProductoById(s).get("precio").toString());
        }

        double impuestos = subtotal * 0.21; //IVA
        double total = subtotal + impuestos;

        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(impuestos);
        pedido.setTotal(total);

        em.getTransaction().begin();
        em.persist(pedido);
        em.getTransaction().commit();
    }

    // Genera y persiste una factura para un pedido
    public Factura facturarPedido(Pedido pedido) {
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

    // Registra un pago por una lista de facturas
    public Pago registrarPago(List<Factura> facturas, MedioPago medioPago, String operador) {
        double montoTotal = facturas.stream().mapToDouble(Factura::getTotal).sum();

        Pago pago = new Pago();
        pago.setFecha(LocalDateTime.now());
        pago.setMonto(montoTotal);
        pago.setMedioPago(medioPago);
        pago.setOperador(operador);
        pago.setFacturasAplicadas(facturas);

        em.getTransaction().begin();
        em.persist(pago);
        for (Factura f : facturas) {
            f.getPagos().add(pago);
            f.setEstado(EstadoFactura.PAGADA);
            em.merge(f);
        }
        em.getTransaction().commit();

        return pago;
    }

    // Pide al usuario qué facturas pendientes desea pagar
    public List<Factura> obtenerFacturasSeleccionadas(int idUsuario) {
        List<Factura> pendientes = buscarFacturasPendientes(idUsuario);
        if (pendientes.isEmpty()) {
            System.out.println("No hay facturas pendientes.");
            return Collections.emptyList();
        }

        System.out.println("Facturas pendientes:");
        for (Factura f : pendientes) {
            System.out.println("ID: " + f.getId() + " | Total: $" + f.getTotal());
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese los IDs de las facturas a pagar (separados por coma): ");
        String input = sc.nextLine();

        String[] idsFacturas = input.split(",");
        List<Integer> idsSeleccionados = new ArrayList<>();
        for (String id : idsFacturas) {
            try {
                idsSeleccionados.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                System.out.println("ID inválido: " + id);
            }
        }

        return pendientes.stream()
                .filter(f -> idsSeleccionados.contains(f.getId()))
                .toList();
    }
}