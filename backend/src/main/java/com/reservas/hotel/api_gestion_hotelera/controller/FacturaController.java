package com.reservas.hotel.api_gestion_hotelera.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private ContabilidadService contabilidadService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearFacturas(@RequestBody List<CrearFacturaRequest> requests) {
        try {
            List<Factura> facturasCreadas = requests.stream()
                    .map(req -> {
                        Date fechaEmision = req.getFechaDeEmision() != null 
                                ? new Date(req.getFechaDeEmision()) 
                                : new Date();
                        TipoFactura tipo = req.getTipo() != null 
                                ? TipoFactura.valueOf(req.getTipo()) 
                                : TipoFactura.A;
                        
                        return contabilidadService.crearFactura(
                                req.getIdReserva(),
                                req.getImporteTotal(),
                                fechaEmision,
                                tipo
                        );
                    })
                    .toList();
            
            return new ResponseEntity<>(facturasCreadas, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear las facturas: " + e.getMessage());
        }
    }

    // Clase interna para el request
    public static class CrearFacturaRequest {
        private Long idReserva;
        private Double importeTotal;
        private Long fechaDeEmision; // Timestamp en milisegundos
        private String tipo; // "A" o "B"

        public Long getIdReserva() {
            return idReserva;
        }

        public void setIdReserva(Long idReserva) {
            this.idReserva = idReserva;
        }

        public Double getImporteTotal() {
            return importeTotal;
        }

        public void setImporteTotal(Double importeTotal) {
            this.importeTotal = importeTotal;
        }

        public Long getFechaDeEmision() {
            return fechaDeEmision;
        }

        public void setFechaDeEmision(Long fechaDeEmision) {
            this.fechaDeEmision = fechaDeEmision;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }
}

