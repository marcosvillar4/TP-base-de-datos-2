package org.example.clases;

import java.time.LocalDateTime;
import java.util.List;

public class Pago {
    private int id;
    private LocalDateTime fecha;
    private double monto;
    private MedioPago medioPago;
    private List<Factura> facturasAplicadas;

    public Pago(int id, LocalDateTime fecha, double monto, MedioPago medioPago, List<Factura> facturasAplicadas) {
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
        this.facturasAplicadas = facturasAplicadas;
    }


    //Getters y setters
    public int getId() {return id;}
    public void  setId(int id) {this.id=id;}
    public LocalDateTime getFecha() {return fecha;}
    public void setFecha(LocalDateTime fecha) {this.fecha=fecha;}
    public double getMonto() {return monto;}
    public void setMonto(double monto) {this.monto=monto;}
    public MedioPago getMedioPago() {return medioPago;}
    public void setMedioPago(MedioPago medioPago) {this.medioPago=medioPago;}
    public List<Factura> getFacturasAplicadas() {return facturasAplicadas;}

}
