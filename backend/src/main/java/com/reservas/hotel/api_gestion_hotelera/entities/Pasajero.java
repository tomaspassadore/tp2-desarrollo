package com.reservas.hotel.api_gestion_hotelera.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Entity
public class Pasajero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String nroDocumento;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.util.Date fechaDeNacimiento;
    
    private String nacionalidad;
    private String telefono;
    private String ocupacion;

    @Column(nullable = true)
    private String cuit;
    
    @Column(nullable = true)
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;
    
    @Enumerated(EnumType.STRING)
    private EstadoPasajero estado;

    @OneToMany(mappedBy = "responsable")
    @JsonIgnoreProperties({"responsable", "habitacion"})
    private List<Reserva> reservas = new ArrayList<>();

    // Relación muchos-a-muchos con Reserva (lado inverso)
    // La tabla intermedia se define en Reserva con @JoinTable
    // @ManyToMany(mappedBy = "pasajeros")
    // private Set<Reserva> reservas;

}
