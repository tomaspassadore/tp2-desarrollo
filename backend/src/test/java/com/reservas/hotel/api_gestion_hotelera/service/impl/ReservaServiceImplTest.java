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

    // ========== Tests para crearReserva ==========

    @Test
    void testCrearReserva_Exitoso() {
        // Arrange
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(pasajeroService.buscarHuesped("dni", "12345678")).thenReturn(List.of(pasajero));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.crearReserva(reserva);

        // Assert
        assertNotNull(resultado);
        assertEquals(habitacion, resultado.getHabitacion());
        assertEquals(pasajero, resultado.getResponsable());
        verify(habitacionService).guardarHabitacion(habitacion);
        verify(reservaRepository).save(any(Reserva.class));
        assertEquals(EstadoHabitacion.RESERVADA, habitacion.getEstado());
    }

    @Test
    void testCrearReserva_FaltaFechaIngreso() {
        // Arrange
        reserva.setFechaIngreso(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("Las fechas son requeridas", exception.getMessage());
    }

    @Test
    void testCrearReserva_FaltaFechaEgreso() {
        // Arrange
        reserva.setFechaEgreso(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("Las fechas son requeridas", exception.getMessage());
    }

    @Test
    void testCrearReserva_FaltaNumeroHabitacion() {
        // Arrange
        reserva.setHabitacion(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("El número de habitación es requerido", exception.getMessage());
    }

    @Test
    void testCrearReserva_HabitacionNoEncontrada() {
        // Arrange
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("No se encontró una habitación con número"));
    }

    @Test
    void testCrearReserva_HabitacionNoDisponible() {
        // Arrange
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("no está disponible para reservar"));
    }

    @Test
    void testCrearReserva_FaltaDniPasajero() {
        // Arrange
        reserva.setResponsable(null);
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertEquals("El DNI del pasajero es requerido", exception.getMessage());
    }

    @Test
    void testCrearReserva_PasajeroNoEncontrado() {
        // Arrange
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(pasajeroService.buscarHuesped("dni", "12345678")).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.crearReserva(reserva));
        assertTrue(exception.getMessage().contains("No se encontró un pasajero con DNI"));
    }

    // ========== Tests para realizarCheckIn ==========

    @Test
    void testRealizarCheckIn_Exitoso() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));
        when(habitacionService.guardarHabitacion(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Reserva resultado = reservaService.realizarCheckIn(reserva);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.OCUPADA, habitacion.getEstado());
        verify(habitacionService).guardarHabitacion(habitacion);
    }

    @Test
    void testRealizarCheckIn_ReservaNoEncontrada() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testRealizarCheckIn_HabitacionNoEncontrada() {
        // Arrange
        reserva.setHabitacion(habitacion);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertEquals("Habitación no encontrada", exception.getMessage());
    }

    @Test
    void testRealizarCheckIn_HabitacionNoReservada() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(habitacionService.buscarPorNumero(101)).thenReturn(Optional.of(habitacion));

        // Act & Assert
        ConflictoReservaException exception = assertThrows(ConflictoReservaException.class, 
            () -> reservaService.realizarCheckIn(reserva));
        assertTrue(exception.getMessage().contains("check-in"));
    }

    // ========== Tests para modificarReserva ==========

    @Test
    void testModificarReserva_Exitoso() {
        // Arrange
        LocalDate nuevaFechaIngreso = LocalDate.now().plusDays(1);
        LocalDate nuevaFechaEgreso = LocalDate.now().plusDays(5);
        
        Reserva datosActualizados = new Reserva();
        datosActualizados.setFechaIngreso(java.sql.Date.valueOf(nuevaFechaIngreso));
        datosActualizados.setFechaEgreso(java.sql.Date.valueOf(nuevaFechaEgreso));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva resultado = reservaService.modificarReserva(1L, datosActualizados);

        // Assert
        assertNotNull(resultado);
        assertEquals(java.sql.Date.valueOf(nuevaFechaIngreso), resultado.getFechaIngreso());
        assertEquals(java.sql.Date.valueOf(nuevaFechaEgreso), resultado.getFechaEgreso());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testModificarReserva_ReservaNoEncontrada() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.modificarReserva(1L, new Reserva()));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    // ========== Tests para facturar ==========

    @Test
    void testFacturar_Exitoso() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(contabilidadService.generarFactura(reserva)).thenReturn(factura);

        // Act
        Factura resultado = reservaService.facturar(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(3000.0, resultado.getImporteTotal());
        verify(contabilidadService).generarFactura(reserva);
    }

    @Test
    void testFacturar_ReservaNoEncontrada() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testFacturar_HabitacionLibre() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("No se puede facturar una reserva inactiva", exception.getMessage());
    }

    @Test
    void testFacturar_HabitacionEnMantenimiento() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.EN_MANTENIMIENTO);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.facturar(1L));
        assertEquals("No se puede facturar una reserva inactiva", exception.getMessage());
    }

    // ========== Tests para cancelarReserva ==========

    @Test
    void testCancelarReserva_Exitoso() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaRepository).delete(reserva);

        // Act
        reservaService.cancelarReserva(1L);

        // Assert
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());
        verify(habitacionService).guardarHabitacion(habitacion);
        verify(reservaRepository).delete(reserva);
    }

    @Test
    void testCancelarReserva_ReservaNoEncontrada() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservaService.cancelarReserva(1L));
        assertEquals("Reserva no encontrada", exception.getMessage());
    }

    @Test
    void testCancelarReserva_HabitacionYaLibre() {
        // Arrange
        reserva.setHabitacion(habitacion);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> reservaService.cancelarReserva(1L));
        assertTrue(exception.getMessage().contains("ya está libre"));
    }

    // ========== Tests para buscarTodas ==========

    @Test
    void testBuscarTodas() {
        // Arrange
        Reserva reserva2 = new Reserva();
        List<Reserva> listaReservas = Arrays.asList(reserva, reserva2);
        when(reservaRepository.findAllWithRelations()).thenReturn(listaReservas);

        // Act
        Set<Reserva> resultado = reservaService.buscarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository).findAllWithRelations();
    }

    // ========== Tests para buscarPorNombreHuesped ==========

    @Test
    void testBuscarPorNombreHuesped() {
        // Arrange
        List<Reserva> listaReservas = Arrays.asList(reserva);
        when(reservaRepository.buscarPorNombreHuesped("Juan")).thenReturn(listaReservas);

        // Act
        List<Reserva> resultado = reservaService.buscarPorNombreHuesped("Juan");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository).buscarPorNombreHuesped("Juan");
    }

    // ========== Tests para buscarPorDniHuesped ==========

    @Test
    void testBuscarPorDniHuesped() {
        // Arrange
        List<Reserva> listaReservas = Arrays.asList(reserva);
        when(reservaRepository.buscarPorDniHuesped("12345678")).thenReturn(listaReservas);

        // Act
        List<Reserva> resultado = reservaService.buscarPorDniHuesped("12345678");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository).buscarPorDniHuesped("12345678");
    }

    // ========== Tests para buscarPorId ==========

    @Test
    void testBuscarPorId_Encontrado() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act
        Optional<Reserva> resultado = reservaService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(reserva, resultado.get());
        verify(reservaRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        // Arrange
        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Reserva> resultado = reservaService.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(reservaRepository).findById(999L);
    }
}
