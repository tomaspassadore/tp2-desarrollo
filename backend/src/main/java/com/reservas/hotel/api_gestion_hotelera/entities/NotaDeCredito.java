package com.reservas.hotel.api_gestion_hotelera.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaDeCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Atributos base de la nota de crédito [6]
    private Date fechaRealizacion;
    
    @Column(unique = true) // El IDNota podría ser un identificador único externo
    private String IDNota;
    
    private Double monto; // [6]

    // Podría incluir una relación a la Factura que corrige (si aplica)
    // @ManyToOne 
    // private Factura facturaAsociada; 
    
    // ... otros atributos y relaciones
}
