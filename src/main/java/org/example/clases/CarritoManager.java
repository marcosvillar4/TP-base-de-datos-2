package org.example.clases;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

public class CarritoManager {
    private static final String PREFIX = "carrito:";
    private final Jedis redis;
    private final Gson gson = new Gson();

    public CarritoManager(){
        this.redis = new Jedis("localhost", 6379);
    }

    public Carrito obtenerCarrito(int idUsuario){
        String json = redis.get(PREFIX + idUsuario);
        return json != null ? gson.fromJson(json, Carrito.class) : new Carrito();
    }

    public void guardarCarrito(int idUsuario, Carrito carrito){
        String json = gson.toJson(carrito);
        redis.set(PREFIX+idUsuario, json);
    }

    public void eliminarCarrito(int idUsuario){
        redis.del(PREFIX+idUsuario);
    }

    public void snapshotCarrito(int idUsuario, Carrito carrito){
        String key = PREFIX+"snapshots:"+idUsuario;
        redis.lpush(key, gson.toJson(carrito));
    }

    public Carrito deshacer(int idUsuario){
        String key = PREFIX+"snapshots:"+idUsuario;
        String lastVersion = redis.lpop(key);
        return lastVersion != null ? gson.fromJson(lastVersion, Carrito.class) : new Carrito();
    }
}
