package com.reservas.hotel.api_gestion_hotelera.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Usamos EnumType.STRING para guardar el nombre del Enum en la BD
    @Enumerated(EnumType.STRING)
    private TipoFactura tipo; // <<Enum>> TipoFactura [7]

    private Double importeTotal; // Usamos Double para Real, aunque BigDecimal es común para dinero [5]
    private Date fechaDeEmision; // [5]

    // Relación con la Reserva (0..1) [5]
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    @JsonIgnoreProperties({"habitacion","pasajeros","responsable"})
    private Reserva reservaAsociada;

    
    // Relación con los items/servicios facturados (ItemFactura) [7]
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemFactura> items = new ArrayList<>();

    // ... otros atributos y relaciones

}
