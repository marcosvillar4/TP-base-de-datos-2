package org.example.clases.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.clases.Producto.ProductoCatalogo;

import java.util.*;
import java.time.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
public class ProductoCatalogoDAO {
    private final MongoCollection<Document> coleccion;

    public ProductoCatalogoDAO(MongoDatabase db) {
        this.coleccion = db.getCollection("productos");
    }

    public void insertarProducto(ProductoCatalogo prod) {
        Document doc = new Document("nombre", prod.getNombre())
                .append("precio", prod.getPrecio())
                .append("cantidad", prod.getCantidad())
                .append("comentarios", prod.getComentarios())
                .append("historialCambios", List.of());

        coleccion.insertOne(doc);
    }

    public List<Document> getAll(){
        return coleccion.find().into(new ArrayList<>());
    }

    public void actualizarPrecio(String id, double nuevoPrecio, String operador){
        Document prod = coleccion.find(eq("_id",  new ObjectId(id))).first();

        if(prod == null) return;

        double precioViejo =  prod.getDouble("precio");

        coleccion.updateOne(eq("_id", new ObjectId(id)),
                combine(set ("precio", nuevoPrecio),
                        push("historialCambios", new Document("campo", "precio")
                                .append("valorAnterior", precioViejo)
                                .append("valorNuevo", nuevoPrecio)
                                .append("operador", operador)
                                .append("fecha", LocalDateTime.now().toString()))));
    }

    public void eliminarProducto(String idProductoEliminar) {
        coleccion.deleteOne(eq("_id", new ObjectId(idProductoEliminar)));
    }
}
