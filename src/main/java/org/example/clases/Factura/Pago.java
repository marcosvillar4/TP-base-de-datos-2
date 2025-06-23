package org.example.clases.Factura;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import org.example.clases.Enums.MedioPago;

@Entity
public class Pago {
    @Id
    private String id;
    private LocalDateTime fecha;
    private double monto;

    @Enumerated(EnumType.STRING)
    private MedioPago medioPago;
    private String operador;

    @ManyToMany
    private List<Factura> facturasAplicadas;

    public Pago(String id, LocalDateTime fecha, double monto, MedioPago medioPago, List<Factura> facturasAplicadas) {
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
        this.facturasAplicadas = facturasAplicadas;
    }

    public Pago() {}

    //Getters y setters
    public String getId() {return id;}
    public void  setId(String id) {this.id=id;}
    public LocalDateTime getFecha() {return fecha;}
    public void setFecha(LocalDateTime fecha) {this.fecha=fecha;}
    public double getMonto() {return monto;}
    public void setMonto(double monto) {this.monto=monto;}
    public MedioPago getMedioPago() {return medioPago;}
    public void setMedioPago(MedioPago medioPago) {this.medioPago=medioPago;}
    public List<Factura> getFacturasAplicadas() {return facturasAplicadas;}
    public void setFacturasAplicadas(List<Factura> facturasAplicadas) { this.facturasAplicadas = facturasAplicadas; }
    public void setOperador(String operador) {this.operador=operador;}
    public String getOperador() {return operador;}
}
