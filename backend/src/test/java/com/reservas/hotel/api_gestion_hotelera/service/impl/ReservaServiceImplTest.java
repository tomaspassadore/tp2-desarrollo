package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.*;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;
import com.reservas.hotel.api_gestion_hotelera.exception.ConflictoReservaException;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import com.reservas.hotel.api_gestion_hotelera.service.HabitacionService;
import com.reservas.hotel.api_gestion_hotelera.service.PasajeroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HabitacionService habitacionService;

    @Mock
    private PasajeroService pasajeroService;

    @Mock
    private ContabilidadService contabilidadService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Reserva reserva;
    private Habitacion habitacion;
    private Pasajero pasajero;
    private Factura factura;

    @BeforeEach
    void setUp() {
        // Configurar pasajero
        pasajero = new Pasajero();
        pasajero.setId(1L);
        pasajero.setNombre("Juan");
        pasajero.setApellido("Pérez");
        pasajero.setNroDocumento("12345678");
        pasajero.setEstado(EstadoPasajero.ACTIVO);

        // Configurar habitación
        habitacion = new Habitacion();
        habitacion.setNumero(101);
        habitacion.setEstado(EstadoHabitacion.LIBRE);

        // Configurar reserva
        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaIngreso(java.sql.Date.valueOf(LocalDate.now()));
        reserva.setFechaEgreso(java.sql.Date.valueOf(LocalDate.now().plusDays(3)));
        
        Habitacion habitacionRequest = new Habitacion();
        habitacionRequest.setNumero(101);
        reserva.setHabitacion(habitacionRequest);
        
        Pasajero responsableRequest = new Pasajero();
        responsableRequest.setNroDocumento("12345678");
        reserva.setResponsable(responsableRequest);

        // Configurar factura
        factura = new Factura();
        factura.setId(1L);
        factura.setImporteTotal(3000.0);
    }

    //  Tests para crearReserva 

    @Test
    void testCrearReserva_Exitoso() {
        
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(pasajeroService.buscarHuesped("dni", "12345678")).thenReturn(List.of(pasajero));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        
        Reserva resultado = reservaService.crearReserva(reserva);

        
        assertNotNull(resultado);
        assertEquals(habitacion, resultado.getHabitacion());
        assertEquals(pasajero, resultado.getResponsable());
        verify(habitacionService).guardarHabitacion(habitacion);
        verify(reservaRepository).save(any(Reserva.class));
        assertEquals(EstadoHabitacion.RESERVADA, habitacion.getEstado());
    }

    @Test
    void testCrearReserva_FaltaFechaIngreso() {
        
        reserva.setFechaIngreso(null);

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("Las fechas son requeridas", exception.getMessage());
    }

    @Test
    void testCrearReserva_FaltaFechaEgreso() {
        
        reserva.setFechaEgreso(null);

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("Las fechas son requeridas", exception.getMessage());
    }

    @Test
    void testCrearReserva_FaltaNumeroHabitacion() {
        
        reserva.setHabitacion(null);

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("El número de habitación es requerido", exception.getMessage());
    }

    @Test
    void testCrearReserva_HabitacionNoEncontrada() {
        
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.empty());

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("No se encontró una habitación con número"));
    }

    @Test
    void testCrearReserva_HabitacionNoDisponible() {
        
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("no está disponible para reservar"));
    }

    @Test
    void testCrearReserva_FaltaDniPasajero() {
        
        reserva.setResponsable(null);
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("El DNI del pasajero es requerido", exception.getMessage());
    }

    @Test
    void testCrearReserva_PasajeroNoEncontrado() {
        
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(pasajeroService.buscarHuesped("dni", "12345678")).thenReturn(Collections.emptyList());

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("No se encontró un pasajero con DNI"));
    }

    //  Tests para realizarCheckIn 

    @Test
    void testRealizarCheckIn_Exitoso() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(habitacionService.guardarHabitacion(any(Habitacion.class))).thenReturn(habitacion);

        
        Reserva resultado = reservaService.realizarCheckIn(reserva);

        
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.OCUPADA, habitacion.getEstado());
        verify(habitacionService).guardarHabitacion(habitacion);
    }

    @Test
    void testRealizarCheckIn_ReservaNoEncontrada() {
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testRealizarCheckIn_HabitacionNoEncontrada() {
        
        reserva.setHabitacion(habitacion);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertEquals("Habitación no encontrada", exception.getMessage());
    }

    @Test
    void testRealizarCheckIn_HabitacionNoReservada() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

         
        ConflictoReservaException exception = assertThrows(ConflictoReservaException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertTrue(exception.getMessage().contains("check-in"));
    }

    //  Tests para modificarReserva 

    @Test
    void testModificarReserva_Exitoso() {
        
        LocalDate nuevaFechaIngreso = LocalDate.now().plusDays(1);
        LocalDate nuevaFechaEgreso = LocalDate.now().plusDays(5);
        
        Reserva datosActualizados = new Reserva();
        datosActualizados.setFechaIngreso(java.sql.Date.valueOf(nuevaFechaIngreso));
        datosActualizados.setFechaEgreso(java.sql.Date.valueOf(nuevaFechaEgreso));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        
        Reserva resultado = reservaService.modificarReserva(1L, datosActualizados);

        
        assertNotNull(resultado);
        assertEquals(java.sql.Date.valueOf(nuevaFechaIngreso), resultado.getFechaIngreso());
        assertEquals(java.sql.Date.valueOf(nuevaFechaEgreso), resultado.getFechaEgreso());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testModificarReserva_ReservaNoEncontrada() {
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.modificarReserva(1L, new Reserva()));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    //  Tests para facturar 

    @Test
    void testFacturar_Exitoso() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(contabilidadService.generarFactura(reserva)).thenReturn(factura);

        
        Factura resultado = reservaService.facturar(1L);

        
        assertNotNull(resultado);
        assertEquals(3000.0, resultado.getImporteTotal());
        verify(contabilidadService).generarFactura(reserva);
    }

    @Test
    void testFacturar_ReservaNoEncontrada() {
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testFacturar_HabitacionLibre() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("No se puede facturar una reserva inactiva", exception.getMessage());
    }

    @Test
    void testFacturar_HabitacionEnMantenimiento() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.EN_MANTENIMIENTO);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("No se puede facturar una reserva inactiva", exception.getMessage());
    }

    //  Tests para cancelarReserva 

    @Test
    void testCancelarReserva_Exitoso() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaRepository).delete(reserva);

        
        reservaService.cancelarReserva(1L);

        
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());
        verify(habitacionService).guardarHabitacion(habitacion);
        verify(reservaRepository).delete(reserva);
    }

    @Test
    void testCancelarReserva_ReservaNoEncontrada() {
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

         
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.cancelarReserva(1L));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testCancelarReserva_HabitacionYaLibre() {
        
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

         
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.cancelarReserva(1L));
        assertTrue(exception.getMessage().contains("ya está libre"));
    }

    //  Tests para buscarTodas 

    @Test
    void testBuscarTodas() {
        
        Reserva reserva2 = new Reserva();
        List<Reserva> listaReservas = Arrays.asList(reserva, reserva2);
        when(reservaRepository.findAllWithRelations()).thenReturn(listaReservas);

        
        Set<Reserva> resultado = reservaService.buscarTodas();

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository).findAllWithRelations();
    }

    //  Tests para buscarPorNombreHuesped 

    @Test
    void testBuscarPorNombreHuesped() {
        
        List<Reserva> listaReservas = Arrays.asList(reserva);
        when(reservaRepository.buscarPorNombreHuesped("Juan")).thenReturn(listaReservas);

        
        List<Reserva> resultado = reservaService.buscarPorNombreHuesped("Juan");

        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository).buscarPorNombreHuesped("Juan");
    }

    //  Tests para buscarPorDniHuesped 

    @Test
    void testBuscarPorDniHuesped() {
        
        List<Reserva> listaReservas = Arrays.asList(reserva);
        when(reservaRepository.buscarPorDniHuesped("12345678")).thenReturn(listaReservas);

        
        List<Reserva> resultado = reservaService.buscarPorDniHuesped("12345678");

        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository).buscarPorDniHuesped("12345678");
    }

    //  Tests para buscarPorId 

    @Test
    void testBuscarPorId_Encontrado() {
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        
        Optional<Reserva> resultado = reservaService.buscarPorId(1L);

        
        assertTrue(resultado.isPresent());
        assertEquals(reserva, resultado.get());
        verify(reservaRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        
        Optional<Reserva> resultado = reservaService.buscarPorId(999L);

        
        assertFalse(resultado.isPresent());
        verify(reservaRepository).findById(999L);
    }
}
