package com.reservas.hotel.api_gestion_hotelera.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.repository.HabitacionRepository;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    // @Override
    // public Optional<Habitacion> buscarPorId(Long id) {
    //     return habitacionRepository.findById(id);
    // }

    @Override
    public Optional<Habitacion> buscarPorNumero(Integer numero) {
        return habitacionRepository.findByNumero(numero);
    }

    @Override
    public List<Habitacion> buscarDisponibles() {
        return habitacionRepository.findByEstado(EstadoHabitacion.LIBRE);
    }


    @Override
    public Set<Habitacion> buscarTodas() {
        return StreamSupport.stream(habitacionRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public Habitacion guardarHabitacion(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public Set<Habitacion> mostrarPorEstado(EstadoHabitacion estado) {
        return StreamSupport.stream(habitacionRepository.findAll().spliterator(), false)
                .filter(h -> h.getEstado() == estado)
                .collect(Collectors.toSet());
    }

    @Override
    public Habitacion actualizarEstado(Long id, EstadoHabitacion nuevoEstado) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        habitacion.setEstado(nuevoEstado);
        return habitacionRepository.save(habitacion);
    }
}
