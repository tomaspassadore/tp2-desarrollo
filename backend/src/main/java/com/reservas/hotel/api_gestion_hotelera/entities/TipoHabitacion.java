package com.reservas.hotel.api_gestion_hotelera.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TipoHabitacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false)
    private BigDecimal costoPorNoche;
    
    @Column(nullable = false)
    private Integer cantidadDisponible;
    
    public TipoHabitacion(String nombre, BigDecimal costoPorNoche, Integer cantidadDisponible) {
        this.nombre = nombre;
        this.costoPorNoche = costoPorNoche;
        this.cantidadDisponible = cantidadDisponible;
    }
}