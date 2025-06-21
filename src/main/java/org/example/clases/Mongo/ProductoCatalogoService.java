package org.example.clases.Mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class ProductoCatalogoService {
        private final MongoCollection<Document> coleccion;
        private final HistorialCambiosService historial;

        public ProductoCatalogoService(MongoCollection<Document> coleccion) {
            this.coleccion = coleccion;
            this.historial = new HistorialCambiosService(coleccion);
        }

        public void actualizarPrecio(String id, double nuevoPrecio, String operador) {
            Document prod = getProducto(id);
            if (prod == null || nuevoPrecio < 0) return;

            double viejo = prod.getDouble("precio");
            actualizarCampo(id, "precio", nuevoPrecio, viejo, operador);
        }

        public void actualizarNombre(String id, String nuevoNombre, String operador) {
            Document prod = getProducto(id);
            if (prod == null || nuevoNombre == null || nuevoNombre.isBlank()) return;

            String viejo = prod.getString("nombre");
            actualizarCampo(id, "nombre", nuevoNombre, viejo, operador);
        }

        private void actualizarCampo(String id, String campo, Object nuevoValor, Object valorAnterior, String operador) {
            coleccion.updateOne(eq("_id", new ObjectId(id)), set(campo, nuevoValor));
            historial.registrarCambio(id, campo, valorAnterior, nuevoValor, operador);
        }

        private Document getProducto(String id) {
            return coleccion.find(eq("_id", new ObjectId(id))).first();
        }
}

