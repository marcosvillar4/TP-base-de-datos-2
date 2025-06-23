package org.example.clases.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sun.jdi.ArrayReference;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.time.LocalDateTime;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.push;

public class HistorialCambiosService {
    private final MongoCollection<Document> coleccion;

    public HistorialCambiosService(MongoCollection<Document> coleccion) {
        this.coleccion = coleccion;
    }

    public void registrarCambio(String id, String campo, Object valorAnterior, Object valorNuevo, String operador){
        String valorCambiado = switch(campo){
            case "nombre" -> "Se actualizó el nombre";
            case "precio" -> "Se actualizó el precio";
            case "cantidad" -> "Se actualizó la cantidad";
            case "multimedia" -> "Se actualizó el archivo multimedia";
            case "comentarios" -> "Se actualizaron los comentarios";
            default -> "Se actualizó el campo: " + campo;
        };

        Document cambio = new Document("campo", campo)
                .append("valorAnterior", valorAnterior)
                .append("valorNuevo", valorNuevo)
                .append("valorCambiado", valorCambiado)
                .append("operador", operador)
                .append("fecha", LocalDateTime.now().toString());

        coleccion.updateOne(eq("_id", new ObjectId(id)), push("historialCambios", cambio));
    }

    public List<Document> obtenerHistorialDelProducto(String idProducto){
        Document producto = coleccion.find(eq("_id", new ObjectId(idProducto))).first();
        if(producto == null) return Collections.emptyList();

        List<Document> historial = (List<Document>) producto.get("historialCambios");
        if(historial == null) return Collections.emptyList();

        return historial;
    }
}
