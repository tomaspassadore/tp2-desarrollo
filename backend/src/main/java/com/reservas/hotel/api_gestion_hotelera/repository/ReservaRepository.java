package com.reservas.hotel.api_gestion_hotelera.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;

// La entidad que maneja es Reserva, y su clave primaria (ID) es Long
@Repository
public interface ReservaRepository extends CrudRepository<Reserva, Long> {

    @Query("""
    SELECT r FROM Reserva r
    WHERE r.habitacion.numero = :numeroHabitacion
    AND r.fechaIngreso < :fechaEgreso
    AND r.fechaEgreso > :fechaIngreso
    """)
    List<Reserva> buscarReservasSolapadas(
        @Param("numeroHabitacion") Integer numeroHabitacion,
        @Param("fechaIngreso") Date fechaIngreso,
        @Param("fechaEgreso") Date fechaEgreso
    );

    @Query("""
    SELECT DISTINCT r FROM Reserva r
    LEFT JOIN FETCH r.responsable
    LEFT JOIN FETCH r.habitacion
    WHERE LOWER(r.responsable.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
       OR LOWER(r.responsable.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Reserva> buscarPorNombreHuesped(@Param("nombre") String nombre);

    @Query("""
    SELECT DISTINCT r FROM Reserva r
    LEFT JOIN FETCH r.responsable
    LEFT JOIN FETCH r.habitacion
    LEFT JOIN FETCH r.habitacion.tipoHabitacion
    WHERE r.responsable.nroDocumento = :dni
    """)
    List<Reserva> buscarPorDniHuesped(@Param("dni") String dni);

    @Query("""
    SELECT DISTINCT r FROM Reserva r
    LEFT JOIN FETCH r.responsable
    LEFT JOIN FETCH r.habitacion
    """)
    List<Reserva> findAllWithRelations();

}
