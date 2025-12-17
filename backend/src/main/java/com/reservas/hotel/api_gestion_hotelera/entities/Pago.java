package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double montoAcumulado;
    private Double vuelto;
    private java.util.Date fechaDeRealizacion;
    
    // (Relaciones con MedioDePago se añadirán luego)
}
