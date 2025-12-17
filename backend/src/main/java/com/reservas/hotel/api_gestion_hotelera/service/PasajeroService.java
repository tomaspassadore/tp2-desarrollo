package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero; 

public interface PasajeroService {
    
    Optional<Pasajero> buscarPorId(Long id);
    Set<Pasajero> buscarTodos();
    
    void darDeBajaPasajero(Long id); 
    
    Pasajero registrarPasajero(Pasajero pasajero);

    Pasajero actualizarPasajero(Long id, Pasajero pasajero);
    
    // Buscar huéspedes por criterio (dni, nombre o apellido)
    List<Pasajero> buscarHuesped(String criterio, String valor);
}
