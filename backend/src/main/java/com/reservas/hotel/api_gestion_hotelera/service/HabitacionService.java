package com.reservas.hotel.api_gestion_hotelera.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

public interface HabitacionService {

    //Optional<Habitacion> buscarPorId(Long id);
    
    Optional<Habitacion> buscarPorNumero(Integer numero);

    Set<Habitacion> buscarTodas();

    Habitacion guardarHabitacion(Habitacion habitacion);

    Set<Habitacion> mostrarPorEstado(EstadoHabitacion estado);

    Habitacion actualizarEstado(Long id, EstadoHabitacion nuevoEstado);

    List<Habitacion> buscarDisponibles();
}
