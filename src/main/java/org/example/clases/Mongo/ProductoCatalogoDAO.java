package org.example.clases.Mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.clases.Producto.Producto;

import java.util.*;
import java.time.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
public class ProductoCatalogoDAO {
    private final MongoCollection<Document> coleccion;
    //private final HistorialCambiosService historialService;

    public ProductoCatalogoDAO(MongoDatabase db) {
        this.coleccion = db.getCollection("productos");
        //this.historialService = new HistorialCambiosService(coleccion);
    }

    //Inserta el producto en la base
    public void insertarProducto(Producto prod) {
        Document doc = new Document("nombre", prod.getNombre())
                .append("descripcion", prod.getDescripcion())
                .append("precio", prod.getPrecio())
                .append("cantidad", prod.getCantidad())
                .append("comentarios", prod.getComentarios())
                .append("historialCambios", List.of());

        coleccion.insertOne(doc);
    }

    public ArrayList<Document> getAll(){
        return coleccion.find().into(new ArrayList<>());
    }

    //Actualiza el precio de un producto en base a su id y almacena el cambio en el historial
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

    //Elimina el producto de la base
    public void eliminarProducto(String idProductoEliminar) {
        DeleteResult check = coleccion.deleteOne(eq("_id", new ObjectId(idProductoEliminar)));
        if (check.getDeletedCount() == 0){
            System.out.println("No existe el id ingresado.");
        }else {
            System.out.println("Producto eliminado.");
        }
    }

    public boolean encontrarProducto(String idProductoBuscar) {
        return coleccion.find(eq("_id", new ObjectId(idProductoBuscar))).first() != null;
    }

    public Document getProductoById(String id){
        return coleccion.find(eq("_id", new ObjectId(id))).first();
    }
}
