package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Conserje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String usuario;
    private String contrasenia;
    
    // (Relaciones con Reserva se añadirán luego)
}
