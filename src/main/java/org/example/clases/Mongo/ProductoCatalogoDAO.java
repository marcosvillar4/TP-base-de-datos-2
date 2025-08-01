package org.example.clases.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.clases.Producto.Producto;
import java.util.*;
import static com.mongodb.client.model.Filters.eq;

public class ProductoCatalogoDAO {
    private final MongoCollection<Document> coleccion;

    public ProductoCatalogoDAO(MongoDatabase db) {
        this.coleccion = db.getCollection("productos");
    }

    //Inserta el producto en la base
    public void insertarProducto(Producto prod, String multimedia) {
        Document doc = new Document("nombre", prod.getNombre())
                .append("descripcion", prod.getDescripcion())
                .append("precio", prod.getPrecio())
                .append("cantidad", prod.getCantidad())
                .append("comentarios", prod.getComentarios())
                .append("multimedia", multimedia)
                .append("historialCambios", List.of());


        coleccion.insertOne(doc);
    }

    public ArrayList<Document> getAll(){
        return coleccion.find().into(new ArrayList<>());
    }

    //Elimina el producto de la base
    public void eliminarProducto(String idProductoEliminar) {
        DeleteResult check = coleccion.deleteOne(eq("_id", new ObjectId(idProductoEliminar)));
        if (check.getDeletedCount() == 0){
            System.out.println("No existe el id ingresado.");
        }else {
            System.out.println("Producto eliminado.");
        }
    }

    public boolean existeProducto(String idProductoBuscar) {
        return coleccion.find(eq("_id", new ObjectId(idProductoBuscar))).first() != null;
    }

    public Document getProductoById(String id){
        return coleccion.find(eq("_id", new ObjectId(id))).first();
    }
}
