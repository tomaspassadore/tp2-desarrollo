package com.reservas.hotel.api_gestion_hotelera.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.reservas.hotel.api_gestion_hotelera.entities.Conserje;

@Repository
// Mapea la Entidad Conserje con su ID de tipo Long
public interface ConserjeRepository extends CrudRepository<Conserje, Long> {
    
    // En este repositorio podrías necesitar en el futuro un método para buscar
    // por el nombre de usuario para el login, por ejemplo.
    // Ejemplo (opcional): Optional<Conserje> findByUsuario(String usuario);
}
