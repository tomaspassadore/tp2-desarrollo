package com.reservas.hotel.api_gestion_hotelera.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.exception.ConflictoReservaException;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import com.reservas.hotel.api_gestion_hotelera.service.PasajeroService;
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private PasajeroService pasajeroService;

    @Autowired
    private ContabilidadService contabilidadService;

    @Override
    @Transactional
        public Reserva crearReserva(Reserva reserva) {
        // Validar datos básicos
        if (reserva.getFechaIngreso() == null || reserva.getFechaEgreso() == null) {
            throw new IllegalArgumentException("Las fechas son requeridas");
        }

        if (reserva.getHabitacion() == null || reserva.getHabitacion().getNumero() == null) {
            throw new IllegalArgumentException("El número de habitación es requerido");
        }

        // Buscar habitación por número
        Habitacion habitacion = habitacionService.buscarPorNumero(reserva.getHabitacion().getNumero())
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró una habitación con número: " + reserva.getHabitacion().getNumero()));

        // Validar que la habitación esté disponible para reservar
        if (habitacion.getEstado() != EstadoHabitacion.LIBRE) {
            throw new IllegalArgumentException(
                "La habitación " + habitacion.getNumero() + " no está disponible para reservar. Estado actual: " + habitacion.getEstado());
        }

        // Buscar pasajero por DNI (viene en el responsable)
        if (reserva.getResponsable() == null || reserva.getResponsable().getNroDocumento() == null) {
            throw new IllegalArgumentException("El DNI del pasajero es requerido");
        }

        List<Pasajero> pasajeros = pasajeroService.buscarHuesped("dni", reserva.getResponsable().getNroDocumento());

        if (pasajeros.isEmpty()) {
            throw new IllegalArgumentException(
                "No se encontró un pasajero con DNI: " + reserva.getResponsable().getNroDocumento());
        }
        Pasajero pasajero = pasajeros.get(0);

        // Cambiar el estado de la habitación de LIBRE a RESERVADA
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        habitacionService.guardarHabitacion(habitacion);

        // Configurar la reserva
        reserva.setHabitacion(habitacion);
        reserva.setResponsable(pasajero);

        return reservaRepository.save(reserva);
    }


    // ==========================================================
    // CU11 - Baja lógica de PASAJERO
    // ==========================================================
    // @Override
    // @Transactional
    // public void darBajaPasajero(Long idPasajero) {

    //     Pasajero pasajero = pasajeroRepository.findById(idPasajero)
    //             .orElseThrow(() -> new RuntimeException("Pasajero no encontrado"));

    //     if (pasajero.getEstado() == EstadoPasajero.INACTIVO) {
    //         throw new RuntimeException("El pasajero ya se encuentra dado de baja");
    //     }

    //     pasajero.setEstado(EstadoPasajero.INACTIVO);
    //     pasajeroRepository.save(pasajero);
    // }


    // ==========================================================
    // Otros métodos
    // ==========================================================

    @Override
    @Transactional
    public Reserva realizarCheckIn(Reserva reservaRequest) {

        Reserva reserva = reservaRepository.findById(reservaRequest.getId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Habitacion habitacion = habitacionService.buscarPorNumero(
                reserva.getHabitacion().getNumero()
        ).orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        if (habitacion.getEstado() != EstadoHabitacion.RESERVADA) {
            throw new ConflictoReservaException(
            "No se puede hacer check-in de una habitación que no está reservada"
            );
        }
        
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionService.guardarHabitacion(habitacion);

        reserva.setHabitacion(habitacion);

        return reserva;
    }


    @Override
    @Transactional
    public Reserva modificarReserva(Long id, Reserva datosActualizados) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setFechaIngreso(datosActualizados.getFechaIngreso());
        reserva.setFechaEgreso(datosActualizados.getFechaEgreso());

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Factura facturar(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoHabitacion estadoHabitacion = reserva.getHabitacion().getEstado();

        if (estadoHabitacion == EstadoHabitacion.LIBRE ||
            estadoHabitacion == EstadoHabitacion.EN_MANTENIMIENTO) {

            throw new RuntimeException("No se puede facturar una reserva inactiva");
        }

        return contabilidadService.generarFactura(reserva);
    }


    @Override
    @Transactional
    public void cancelarReserva(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Habitacion habitacion = reserva.getHabitacion();

        // Validar que la habitación esté en un estado que permita cancelar
        if (habitacion.getEstado() == EstadoHabitacion.LIBRE) {
            throw new IllegalArgumentException(
                "La habitación " + habitacion.getNumero() + " ya está libre. La reserva puede haber sido cancelada previamente.");
        }

        // Cambiar el estado de la habitación a LIBRE
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        habitacionService.guardarHabitacion(habitacion);

        // Eliminar la reserva
        reservaRepository.delete(reserva);
    }

    @Override
    public Set<Reserva> buscarTodas() {
        List<Reserva> reservas = reservaRepository.findAllWithRelations();
        return reservas.stream().collect(Collectors.toSet());
    }

    @Override
    public List<Reserva> buscarPorNombreHuesped(String nombre) {
        return reservaRepository.buscarPorNombreHuesped(nombre);
    }

    @Override
    public List<Reserva> buscarPorDniHuesped(String dni) {
        return reservaRepository.buscarPorDniHuesped(dni);
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }
}
