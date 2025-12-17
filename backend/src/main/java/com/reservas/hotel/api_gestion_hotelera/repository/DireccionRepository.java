package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Direccion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends CrudRepository<Direccion, Long> {
}

