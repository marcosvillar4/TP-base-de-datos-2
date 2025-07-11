package org.example.clases.Carrito;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import java.net.URI;

public class CarritoManager {
    private static final String PREFIX = "carrito:";
    private final Jedis redis;
    private final Gson gson = new Gson();

    /*public CarritoManager(String host, int port){
        this.redis = new Jedis(host, port);
    }*/

    public CarritoManager(String uri){
        this.redis = new Jedis(URI.create(uri));
    }

    public Carrito obtenerCarrito(String idUsuario){
        String json = redis.get(PREFIX + idUsuario);
        return json != null ? gson.fromJson(json, Carrito.class) : new Carrito();
    }

    public void guardarCarrito(String idUsuario, Carrito carrito){
        String json = gson.toJson(carrito);
        redis.set(PREFIX+idUsuario, json);
    }

    public void eliminarCarrito(String idUsuario){
        redis.del(PREFIX+idUsuario);
    }

    public void snapshotCarrito(String idUsuario, Carrito carrito){
        String key = PREFIX+"snapshots:"+idUsuario;
        redis.lpush(key, gson.toJson(carrito));
    }

    public Carrito deshacer(String idUsuario){
        String key = PREFIX+"snapshots:"+idUsuario;
        String lastVersion = redis.lpop(key);
        return lastVersion != null ? gson.fromJson(lastVersion, Carrito.class) : new Carrito();
    }

    public void eliminarSnapshots(String idUsuario){
        String key = PREFIX+"snapshots:"+idUsuario;
        redis.del(key);
    }
}
