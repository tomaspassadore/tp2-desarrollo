package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;

public interface ReservaService {

    Reserva crearReserva(Reserva reserva);
    
    // Check-in
    Reserva realizarCheckIn(Reserva reserva); 

    // Modificar reserva
    Reserva modificarReserva(Long id, Reserva datosActualizados);

    // Consultas
    Set<Reserva> buscarTodas();
    
    Optional<Reserva> buscarPorId(Long id);

    /**
     * Buscar reservas por nombre/apellido de huésped o responsable.
     */
    List<Reserva> buscarPorNombreHuesped(String nombre);

    /**
     * Buscar reservas por DNI del huésped responsable.
     */
    List<Reserva> buscarPorDniHuesped(String dni);

    Factura facturar(Long id);

    void cancelarReserva(Long id);

    //void darBajaPasajero(Long idPasajero);
}
