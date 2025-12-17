package com.reservas.hotel.api_gestion_hotelera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero; 

@Repository
// Pasajero es la Entidad, Long es el tipo de su ID
public interface PasajeroRepository extends CrudRepository<Pasajero, Long> {
    
  @Query("SELECT p FROM Pasajero p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%'))")
  List<Pasajero> buscarPorNombre(@Param("q") String q);
  
  @Query("SELECT p FROM Pasajero p WHERE LOWER(p.apellido) LIKE LOWER(CONCAT('%', :q, '%'))")
  List<Pasajero> buscarPorApellido(@Param("q") String q);
  
  @Query("SELECT p FROM Pasajero p WHERE p.nroDocumento LIKE CONCAT('%', :q, '%')")
  List<Pasajero> buscarPorDni(@Param("q") String q);
}
