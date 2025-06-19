package org.example.clases;

import java.util.*;
import java.time.LocalDate;

public class Usuario {

    private int id;
    private int dni;
    private String passwd;
    private String nombre;
    private String direccion;
    private LinkedList<Pedido> pedidos;
    private List<Sesion> sesiones;
    private CategoriaUsuario categoria;
    private String condicionIva;

    public Usuario(int id, int dni, String passwd, String nombre, String direccion) {
        this.id = id;
        this.dni = dni;
        this.passwd = passwd;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Usuario() {
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LinkedList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(LinkedList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public CategoriaUsuario getCategoria() {
        return categoria;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCategoria(CategoriaUsuario categoria) {
        this.categoria = categoria;
    }

    public void actualizarCategoria(){
        Map<LocalDate, Long> minutosPorDia = new HashMap<>();

        for(Sesion s : sesiones){
            if(s.getDuracionEnMinutos() > 0){
                LocalDate dia = s.getInicio().toLocalDate();
                minutosPorDia.put(dia, minutosPorDia.getOrDefault(dia, 0L) + s.getDuracionEnMinutos());
            }
        }

        //Encuentra el ultimo dia (mas reciente) en el map minutosPorDia
        Optional<LocalDate> ultimoDia = minutosPorDia.keySet().stream().max(Comparator.naturalOrder()); //Optional porque puede que no haya fechas

        if(ultimoDia.isPresent()){
            long minutos = minutosPorDia.get(ultimoDia.get());
            if(minutos > 240){
                categoria = CategoriaUsuario.TOP;
            } else if(minutos >=120){
                categoria = CategoriaUsuario.MEDIUM;
            } else{
                categoria = CategoriaUsuario.LOW;
            }
        }
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public List<Sesion> getSesiones() {
        return sesiones;
    }

    public String getCondicionIVA(){
        return condicionIva;
    }
}
