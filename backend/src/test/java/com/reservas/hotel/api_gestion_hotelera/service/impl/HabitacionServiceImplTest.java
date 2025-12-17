package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.TipoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.repository.HabitacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceImplTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionServiceImpl habitacionService;

    private Habitacion habitacion;
    private TipoHabitacion tipoHabitacion;

    @BeforeEach
    void setUp() {
        // Configurar tipo de habitación
        tipoHabitacion = new TipoHabitacion();
        tipoHabitacion.setNombre("Simple");
        tipoHabitacion.setCostoPorNoche(new BigDecimal("1000.00"));
        tipoHabitacion.setCantidadDisponible(2);

        // Configurar habitación
        habitacion = new Habitacion();
        habitacion.setNumero(101);
        habitacion.setEstado(EstadoHabitacion.LIBRE);
        habitacion.setTipoHabitacion(tipoHabitacion);
    }

    // ========== Tests para buscarPorNumero ==========

    @Test
    void testBuscarPorNumero_Encontrada() {
        // Arrange
        when(habitacionRepository.findByNumero(101)).thenReturn(Optional.of(habitacion));

        // Act
        Optional<Habitacion> resultado = habitacionService.buscarPorNumero(101);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(habitacion, resultado.get());
        assertEquals(101, resultado.get().getNumero());
        verify(habitacionRepository).findByNumero(101);
    }

    @Test
    void testBuscarPorNumero_NoEncontrada() {
        // Arrange
        when(habitacionRepository.findByNumero(999)).thenReturn(Optional.empty());

        // Act
        Optional<Habitacion> resultado = habitacionService.buscarPorNumero(999);

        // Assert
        assertFalse(resultado.isPresent());
        verify(habitacionRepository).findByNumero(999);
    }

    @Test
    void testBuscarPorNumero_NumeroNulo() {
        // Arrange
        when(habitacionRepository.findByNumero(null)).thenReturn(Optional.empty());

        // Act
        Optional<Habitacion> resultado = habitacionService.buscarPorNumero(null);

        // Assert
        assertFalse(resultado.isPresent());
        verify(habitacionRepository).findByNumero(null);
    }

    // ========== Tests para buscarDisponibles ==========

    @Test
    void testBuscarDisponibles() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.LIBRE);

        Habitacion habitacion3 = new Habitacion();
        habitacion3.setNumero(103);
        habitacion3.setEstado(EstadoHabitacion.LIBRE);

        List<Habitacion> habitacionesLibres = Arrays.asList(habitacion, habitacion2, habitacion3);
        when(habitacionRepository.findByEstado(EstadoHabitacion.LIBRE)).thenReturn(habitacionesLibres);

        // Act
        List<Habitacion> resultado = habitacionService.buscarDisponibles();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getEstado() == EstadoHabitacion.LIBRE));
        verify(habitacionRepository).findByEstado(EstadoHabitacion.LIBRE);
    }

    @Test
    void testBuscarDisponibles_ListaVacia() {
        // Arrange
        when(habitacionRepository.findByEstado(EstadoHabitacion.LIBRE)).thenReturn(Collections.emptyList());

        // Act
        List<Habitacion> resultado = habitacionService.buscarDisponibles();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(habitacionRepository).findByEstado(EstadoHabitacion.LIBRE);
    }

    // ========== Tests para buscarTodas ==========

    @Test
    void testBuscarTodas() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.OCUPADA);

        Habitacion habitacion3 = new Habitacion();
        habitacion3.setNumero(103);
        habitacion3.setEstado(EstadoHabitacion.EN_MANTENIMIENTO);

        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion, habitacion2, habitacion3);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.buscarTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(habitacionRepository).findAll();
    }

    @Test
    void testBuscarTodas_ListaVacia() {
        // Arrange
        when(habitacionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Set<Habitacion> resultado = habitacionService.buscarTodas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(habitacionRepository).findAll();
    }

    // ========== Tests para guardarHabitacion ==========

    @Test
    void testGuardarHabitacion() {
        // Arrange
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.guardarHabitacion(habitacion);

        // Assert
        assertNotNull(resultado);
        assertEquals(habitacion, resultado);
        assertEquals(101, resultado.getNumero());
        assertEquals(EstadoHabitacion.LIBRE, resultado.getEstado());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testGuardarHabitacion_HabitacionNueva() {
        // Arrange
        Habitacion habitacionNueva = new Habitacion();
        habitacionNueva.setNumero(201);
        habitacionNueva.setEstado(EstadoHabitacion.LIBRE);
        
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacionNueva);

        // Act
        Habitacion resultado = habitacionService.guardarHabitacion(habitacionNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(201, resultado.getNumero());
        verify(habitacionRepository).save(habitacionNueva);
    }

    // ========== Tests para mostrarPorEstado ==========

    @Test
    void testMostrarPorEstado_Libre() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.LIBRE);

        Habitacion habitacion3 = new Habitacion();
        habitacion3.setNumero(103);
        habitacion3.setEstado(EstadoHabitacion.OCUPADA);

        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion, habitacion2, habitacion3);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.LIBRE);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getEstado() == EstadoHabitacion.LIBRE));
        verify(habitacionRepository).findAll();
    }

    @Test
    void testMostrarPorEstado_Ocupada() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.OCUPADA);

        Habitacion habitacion3 = new Habitacion();
        habitacion3.setNumero(103);
        habitacion3.setEstado(EstadoHabitacion.OCUPADA);

        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion, habitacion2, habitacion3);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.OCUPADA);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getEstado() == EstadoHabitacion.OCUPADA));
        verify(habitacionRepository).findAll();
    }

    @Test
    void testMostrarPorEstado_Reservada() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.RESERVADA);

        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion, habitacion2);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.RESERVADA);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getEstado() == EstadoHabitacion.RESERVADA));
        verify(habitacionRepository).findAll();
    }

    @Test
    void testMostrarPorEstado_EnMantenimiento() {
        // Arrange
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setNumero(102);
        habitacion2.setEstado(EstadoHabitacion.EN_MANTENIMIENTO);

        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion, habitacion2);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.EN_MANTENIMIENTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().allMatch(h -> h.getEstado() == EstadoHabitacion.EN_MANTENIMIENTO));
        verify(habitacionRepository).findAll();
    }

    @Test
    void testMostrarPorEstado_NingunResultado() {
        // Arrange
        List<Habitacion> todasHabitaciones = Arrays.asList(habitacion);
        when(habitacionRepository.findAll()).thenReturn(todasHabitaciones);

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.OCUPADA);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(habitacionRepository).findAll();
    }

    @Test
    void testMostrarPorEstado_ListaVacia() {
        // Arrange
        when(habitacionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Set<Habitacion> resultado = habitacionService.mostrarPorEstado(EstadoHabitacion.LIBRE);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(habitacionRepository).findAll();
    }

    // ========== Tests para actualizarEstado ==========

    @Test
    void testActualizarEstado_LibreAReservada() {
        // Arrange
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.RESERVADA);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.RESERVADA, habitacion.getEstado());
        verify(habitacionRepository).findById(101L);
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testActualizarEstado_ReservadaAOcupada() {
        // Arrange
        habitacion.setEstado(EstadoHabitacion.RESERVADA);
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.OCUPADA);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.OCUPADA, habitacion.getEstado());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testActualizarEstado_OcupadaALibre() {
        // Arrange
        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.LIBRE);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testActualizarEstado_LibreAEnMantenimiento() {
        // Arrange
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.EN_MANTENIMIENTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.EN_MANTENIMIENTO, habitacion.getEstado());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testActualizarEstado_EnMantenimientoALibre() {
        // Arrange
        habitacion.setEstado(EstadoHabitacion.EN_MANTENIMIENTO);
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.LIBRE);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void testActualizarEstado_HabitacionNoEncontrada() {
        // Arrange
        when(habitacionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> habitacionService.actualizarEstado(999L, EstadoHabitacion.OCUPADA));
        assertEquals("Habitación no encontrada", exception.getMessage());
        verify(habitacionRepository, never()).save(any(Habitacion.class));
    }

    @Test
    void testActualizarEstado_MismoEstado() {
        // Arrange
        when(habitacionRepository.findById(101L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);

        // Act
        Habitacion resultado = habitacionService.actualizarEstado(101L, EstadoHabitacion.LIBRE);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());
        verify(habitacionRepository).save(habitacion);
    }
}
