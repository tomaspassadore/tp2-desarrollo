package com.reservas.hotel.api_gestion_hotelera.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.reservas.hotel.api_gestion_hotelera.entities.Conserje;

@Repository
// Mapea la Entidad Conserje con su ID de tipo Long
public interface ConserjeRepository extends CrudRepository<Conserje, Long> {   
}
