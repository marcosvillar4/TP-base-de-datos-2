package org.example.clases.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.example.clases.Enums.CategoriaUsuario;
import org.example.clases.Factura.Factura;
import org.example.clases.Factura.Pago;
import org.example.clases.Pedido.Pedido;

import javax.annotation.processing.Generated;
import java.io.Serializable;
import java.util.*;
import java.time.LocalDate;



@Entity
public class Usuario implements Serializable {

    @Id
    private String id;

    private int dni;
    private String passwd;
    private String nombre;
    private String direccion;

    @OneToMany
    private List<Factura> facturas;

    @OneToMany
    private List<Sesion> sesiones;

    @OneToMany
    private List<Pago> pagos;


    private CategoriaUsuario categoria;
    private String condicionIva;

    public Usuario(String id, int dni, String passwd, String nombre, String direccion) {
        this.id = id;
        this.dni = dni;
        this.passwd = passwd;
        this.nombre = nombre;
        this.direccion = direccion;
        this.facturas = new LinkedList<>();
        this.pagos = new LinkedList<>();
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

    public List<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    public CategoriaUsuario getCategoria() {
        return categoria;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
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


    public void setSesiones(List<Sesion> sesiones) {
        this.sesiones = sesiones;
    }

    public String getCondicionIVA(){
        return condicionIva;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public String getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(String condicionIva) {
        this.condicionIva = condicionIva;
    }
}
