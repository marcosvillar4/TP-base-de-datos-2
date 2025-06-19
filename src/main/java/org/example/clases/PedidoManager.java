package org.example.clases;

import org.example.clases.*;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
public class PedidoManager {
    private final EntityManager em;
    private final CarritoManager carritoManager;

    public PedidoManager(EntityManager em, CarritoManager carritoManager) {
        this.em = em;
        this.carritoManager = carritoManager;
    }

    //Genera y persiste el pedido final

    public Pedido generarYGuardarPedido(int idUsuario, Usuario usuario) {}
}
