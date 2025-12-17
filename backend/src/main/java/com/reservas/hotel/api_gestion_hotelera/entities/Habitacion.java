package com.reservas.hotel.api_gestion_hotelera.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {
    
    @Id
    private Integer numero;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    @JsonIgnoreProperties({"cantidadDisponible"})
    private TipoHabitacion tipoHabitacion;
    
}