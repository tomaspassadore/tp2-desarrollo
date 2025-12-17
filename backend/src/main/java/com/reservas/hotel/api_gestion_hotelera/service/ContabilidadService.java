package com.reservas.hotel.api_gestion_hotelera.service;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.NotaDeCredito;
import com.reservas.hotel.api_gestion_hotelera.entities.Pago;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface ContabilidadService {

    // MÉTODOS DE FACTURA (Implementan el contrato)
    Factura generarFactura(Reserva reserva);
    
    /**
     * Crea una factura con los datos especificados.
     * @param reservaId ID de la reserva asociada
     * @param importeTotal Importe total de la factura
     * @param fechaDeEmision Fecha de emisión de la factura
     * @param tipo Tipo de factura (por defecto A)
     * @return Factura creada
     */
    Factura crearFactura(Long reservaId, Double importeTotal, Date fechaDeEmision, TipoFactura tipo);
    
    // Firma 1: Debe devolver Optional<Factura> para búsqueda por ID
    Optional<Factura> buscarFacturaPorId(Long id); // <--- FIRMA CRÍTICA
    
    // Firma 2: Debe devolver Set<Factura> para todas las búsquedas
    Set<Factura> buscarTodasFacturas();            // <--- FIRMA CRÍTICA

    // MÉTODOS DE NOTA DE CRÉDITO (Implementan el contrato)
    NotaDeCredito registrarNotaDeCredito(NotaDeCredito nota);
    
    // Firma 3: Debe devolver Optional<NotaDeCredito> para búsqueda por ID
    Optional<NotaDeCredito> buscarNotaDeCreditoPorId(Long id); // <--- FIRMA CRÍTICA
    
    // Firma 4: Debe devolver Set<NotaDeCredito> para todas las búsquedas
    Set<NotaDeCredito> buscarTodasNotasDeCredito();            // <--- FIRMA CRÍTICA

    // MÉTODOS DE PAGO (ya revisados)
    Pago registrarPago(Pago pago);
    Optional<Pago> buscarPagoPorId(Long id);
    
    // Si tu interfaz incluye este método, debe estar:
    // Set<Pago> buscarTodosPagos(); 
}