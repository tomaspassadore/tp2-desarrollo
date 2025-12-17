package com.reservas.hotel.api_gestion_hotelera.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;

@Repository
public interface HabitacionRepository extends CrudRepository<Habitacion, Long> {

    List<Habitacion> findByEstado(EstadoHabitacion estado);
    
    Optional<Habitacion> findByNumero(Integer numero);
}
