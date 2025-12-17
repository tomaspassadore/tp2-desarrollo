package com.reservas.hotel.api_gestion_hotelera.repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// La entidad que maneja es Factura, y su clave primaria (ID) es Long
@Repository 
public interface FacturaRepository extends CrudRepository<Factura, Long> {

    // Podrías añadir métodos específicos aquí si fuera necesario
    // Ejemplo: Optional<Factura> findByReservaAsociadaId(Long reservaId);

}
