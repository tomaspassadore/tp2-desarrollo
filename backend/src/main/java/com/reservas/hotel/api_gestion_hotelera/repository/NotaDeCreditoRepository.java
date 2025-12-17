package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.NotaDeCredito;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// La entidad que maneja es NotaDeCredito, y su clave primaria (ID) es Long
@Repository 
public interface NotaDeCreditoRepository extends CrudRepository<NotaDeCredito, Long> {

    // Ejemplo: Optional<NotaDeCredito> findByIDNota(String idNota);

}
