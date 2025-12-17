package com.reservas.hotel.api_gestion_hotelera.entities;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // Clave primaria e identificador (requerido por JPA)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atributos base de la reserva
    private Date fechaIngreso;
    private Date fechaEgreso;

    // Una reserva pertenece a una habitación (Habitacion)
    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    // Relación muchos-a-muchos con Pasajero
    // Se crea una tabla intermedia reserva_pasajero con reserva_id y pasajero_id
    @ManyToMany
    @JoinTable(name = "reserva_pasajero", joinColumns = @JoinColumn(name = "reserva_id"), inverseJoinColumns = @JoinColumn(name = "pasajero_id"))
    @JsonIgnoreProperties({"reservas", "direccion"})
    private Set<Pasajero> pasajeros;

    // Relación con el pasajero responsable de la reserva
    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable = false)
    @JsonIgnoreProperties({"reservas", "direccion"})
    private Pasajero responsable;

}