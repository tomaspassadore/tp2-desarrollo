package com.reservas.hotel.api_gestion_hotelera.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    /**
     * CU05 - Obtener todas las habitaciones
     */
    @GetMapping("/listar")
    public ResponseEntity<Set<Habitacion>> obtenerTodas() {
        Set<Habitacion> habitaciones = habitacionService.buscarTodas();
        return new ResponseEntity<>(habitaciones, HttpStatus.OK);
    }

    /**
     * CU05 - Obtener habitaciones por estado
     * Ejemplo: /api/habitaciones/estado/LIBRE
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Set<Habitacion>> obtenerPorEstado(@PathVariable String estado) {
        try {
            EstadoHabitacion estadoHabitacion = EstadoHabitacion.valueOf(estado.toUpperCase());
            Set<Habitacion> habitaciones = habitacionService.mostrarPorEstado(estadoHabitacion);
            return new ResponseEntity<>(habitaciones, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Habitacion>> obtenerDisponibles() {
        return ResponseEntity.ok(habitacionService.buscarDisponibles());
    }
}
