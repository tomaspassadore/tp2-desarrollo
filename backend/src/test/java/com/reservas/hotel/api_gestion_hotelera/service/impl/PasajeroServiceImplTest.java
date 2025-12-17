package com.reservas.hotel.api_gestion_hotelera.service.impl;

import com.reservas.hotel.api_gestion_hotelera.entities.Direccion;
import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoPasajero;
import com.reservas.hotel.api_gestion_hotelera.repository.DireccionRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PasajeroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasajeroServiceImplTest {

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private PasajeroServiceImpl pasajeroService;

    private Pasajero pasajero;
    private Direccion direccion;

    @BeforeEach
    void setUp() {
        // Configurar dirección
        direccion = new Direccion();
        direccion.setId(1L);
        direccion.setCalle("Av. Siempre Viva");
        direccion.setNumero("742");
        direccion.setLocalidad("Springfield");
        direccion.setProvincia("Buenos Aires");
        direccion.setPais("Argentina");

        // Configurar pasajero
        pasajero = new Pasajero();
        pasajero.setId(1L);
        pasajero.setNombre("Juan");
        pasajero.setApellido("Pérez");
        pasajero.setNroDocumento("12345678");
        pasajero.setTelefono("1234567890");
        pasajero.setEmail("juan.perez@example.com");
        pasajero.setCuit("20-12345678-9");
        pasajero.setFechaDeNacimiento(java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)));
        pasajero.setNacionalidad("Argentina");
        pasajero.setOcupacion("Ingeniero");
        pasajero.setEstado(EstadoPasajero.ACTIVO);
        pasajero.setDireccion(direccion);
    }

    // ========== Tests para buscarPorId ==========

    @Test
    void testBuscarPorId_Encontrado() {
        // Arrange
        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(pasajero));

        // Act
        Optional<Pasajero> resultado = pasajeroService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(pasajero, resultado.get());
        assertEquals("Juan", resultado.get().getNombre());
        verify(pasajeroRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        // Arrange
        when(pasajeroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Pasajero> resultado = pasajeroService.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(pasajeroRepository).findById(999L);
    }

    // ========== Tests para buscarTodos ==========

    @Test
    void testBuscarTodos() {
        // Arrange
        Pasajero pasajero2 = new Pasajero();
        pasajero2.setId(2L);
        pasajero2.setNombre("María");
        
        List<Pasajero> listaPasajeros = Arrays.asList(pasajero, pasajero2);
        when(pasajeroRepository.findAll()).thenReturn(listaPasajeros);

        // Act
        Set<Pasajero> resultado = pasajeroService.buscarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pasajeroRepository).findAll();
    }

    @Test
    void testBuscarTodos_ListaVacia() {
        // Arrange
        when(pasajeroRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Set<Pasajero> resultado = pasajeroService.buscarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pasajeroRepository).findAll();
    }

    // ========== Tests para darDeBajaPasajero ==========

    @Test
    void testDarDeBajaPasajero() {
        // Arrange
        doNothing().when(pasajeroRepository).deleteById(1L);

        // Act
        pasajeroService.darDeBajaPasajero(1L);

        // Assert
        verify(pasajeroRepository).deleteById(1L);
    }

    // ========== Tests para registrarPasajero ==========

    @Test
    void testRegistrarPasajero_ConDireccion() {
        // Arrange
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(pasajero);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals(EstadoPasajero.ACTIVO, resultado.getEstado());
        assertNotNull(resultado.getDireccion());
        verify(direccionRepository).save(any(Direccion.class));
        verify(pasajeroRepository).save(any(Pasajero.class));
    }

    @Test
    void testRegistrarPasajero_SinDireccion() {
        // Arrange
        pasajero.setDireccion(null);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(pasajero);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals(EstadoPasajero.ACTIVO, resultado.getEstado());
        assertNull(resultado.getDireccion());
        verify(direccionRepository, never()).save(any(Direccion.class));
        verify(pasajeroRepository).save(any(Pasajero.class));
    }

    @Test
    void testRegistrarPasajero_EstadoNoEstablecido() {
        // Arrange
        pasajero.setEstado(null);
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(pasajero);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPasajero.ACTIVO, pasajero.getEstado()); // Verificar que se estableció ACTIVO
        verify(pasajeroRepository).save(any(Pasajero.class));
    }

    @Test
    void testRegistrarPasajero_EstadoInactivo() {
        // Arrange
        pasajero.setEstado(EstadoPasajero.INACTIVO);
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.registrarPasajero(pasajero);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPasajero.INACTIVO, resultado.getEstado()); // Mantiene el estado establecido
        verify(pasajeroRepository).save(any(Pasajero.class));
    }

    // ========== Tests para actualizarPasajero ==========

    @Test
    void testActualizarPasajero_Exitoso() {
        // Arrange
        Pasajero pasajeroActualizado = new Pasajero();
        pasajeroActualizado.setNombre("Carlos");
        pasajeroActualizado.setApellido("García");
        pasajeroActualizado.setNroDocumento("87654321");
        pasajeroActualizado.setTelefono("0987654321");
        pasajeroActualizado.setEmail("carlos.garcia@example.com");
        pasajeroActualizado.setCuit("20-87654321-9");
        pasajeroActualizado.setFechaDeNacimiento(java.sql.Date.valueOf(LocalDate.of(1985, 5, 15)));
        pasajeroActualizado.setNacionalidad("Argentina");
        pasajeroActualizado.setOcupacion("Médico");
        pasajeroActualizado.setEstado(EstadoPasajero.ACTIVO);

        Direccion nuevaDireccion = new Direccion();
        nuevaDireccion.setId(2L);
        nuevaDireccion.setCalle("Calle Nueva");
        nuevaDireccion.setNumero("123");
        pasajeroActualizado.setDireccion(nuevaDireccion);

        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(pasajero));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(nuevaDireccion);
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.actualizarPasajero(1L, pasajeroActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos", pasajero.getNombre());
        assertEquals("García", pasajero.getApellido());
        assertEquals("87654321", pasajero.getNroDocumento());
        assertEquals("0987654321", pasajero.getTelefono());
        assertEquals("carlos.garcia@example.com", pasajero.getEmail());
        assertEquals("20-87654321-9", pasajero.getCuit());
        assertEquals(java.sql.Date.valueOf(LocalDate.of(1985, 5, 15)), pasajero.getFechaDeNacimiento());
        assertEquals("Argentina", pasajero.getNacionalidad());
        assertEquals("Médico", pasajero.getOcupacion());
        assertEquals(EstadoPasajero.ACTIVO, pasajero.getEstado());
        verify(direccionRepository).save(any(Direccion.class));
        verify(pasajeroRepository).save(pasajero);
    }

    @Test
    void testActualizarPasajero_SinDireccion() {
        // Arrange
        Pasajero pasajeroActualizado = new Pasajero();
        pasajeroActualizado.setNombre("Carlos");
        pasajeroActualizado.setApellido("García");
        pasajeroActualizado.setNroDocumento("87654321");
        pasajeroActualizado.setTelefono("0987654321");
        pasajeroActualizado.setEmail("carlos.garcia@example.com");
        pasajeroActualizado.setCuit("20-87654321-9");
        pasajeroActualizado.setFechaDeNacimiento(java.sql.Date.valueOf(LocalDate.of(1985, 5, 15)));
        pasajeroActualizado.setNacionalidad("Argentina");
        pasajeroActualizado.setOcupacion("Médico");
        pasajeroActualizado.setDireccion(null);

        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(pasajero));
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.actualizarPasajero(1L, pasajeroActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos", pasajero.getNombre());
        verify(direccionRepository, never()).save(any(Direccion.class));
        verify(pasajeroRepository).save(pasajero);
    }

    @Test
    void testActualizarPasajero_NoEncontrado() {
        // Arrange
        when(pasajeroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> pasajeroService.actualizarPasajero(999L, pasajero));
        assertEquals("Pasajero no encontrado", exception.getMessage());
        verify(pasajeroRepository, never()).save(any(Pasajero.class));
    }

    @Test
    void testActualizarPasajero_SinEstado() {
        // Arrange
        Pasajero pasajeroActualizado = new Pasajero();
        pasajeroActualizado.setNombre("Carlos");
        pasajeroActualizado.setApellido("García");
        pasajeroActualizado.setNroDocumento("87654321");
        pasajeroActualizado.setTelefono("0987654321");
        pasajeroActualizado.setEmail("carlos.garcia@example.com");
        pasajeroActualizado.setCuit("20-87654321-9");
        pasajeroActualizado.setFechaDeNacimiento(java.sql.Date.valueOf(LocalDate.of(1985, 5, 15)));
        pasajeroActualizado.setNacionalidad("Argentina");
        pasajeroActualizado.setOcupacion("Médico");
        pasajeroActualizado.setEstado(null); // Estado null
        pasajeroActualizado.setDireccion(null);

        when(pasajeroRepository.findById(1L)).thenReturn(Optional.of(pasajero));
        when(pasajeroRepository.save(any(Pasajero.class))).thenReturn(pasajero);

        // Act
        Pasajero resultado = pasajeroService.actualizarPasajero(1L, pasajeroActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPasajero.ACTIVO, pasajero.getEstado()); // No debe cambiar
        verify(pasajeroRepository).save(pasajero);
    }

    // ========== Tests para buscarHuesped ==========

    @Test
    void testBuscarHuesped_PorDni() {
        // Arrange
        List<Pasajero> listaPasajeros = Arrays.asList(pasajero);
        when(pasajeroRepository.buscarPorDni("12345678")).thenReturn(listaPasajeros);

        // Act
        List<Pasajero> resultado = pasajeroService.buscarHuesped("dni", "12345678");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pasajero, resultado.get(0));
        verify(pasajeroRepository).buscarPorDni("12345678");
    }

    @Test
    void testBuscarHuesped_PorNombre() {
        // Arrange
        List<Pasajero> listaPasajeros = Arrays.asList(pasajero);
        when(pasajeroRepository.buscarPorNombre("Juan")).thenReturn(listaPasajeros);

        // Act
        List<Pasajero> resultado = pasajeroService.buscarHuesped("nombre", "Juan");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pasajero, resultado.get(0));
        verify(pasajeroRepository).buscarPorNombre("Juan");
    }

    @Test
    void testBuscarHuesped_PorApellido() {
        // Arrange
        List<Pasajero> listaPasajeros = Arrays.asList(pasajero);
        when(pasajeroRepository.buscarPorApellido("Pérez")).thenReturn(listaPasajeros);

        // Act
        List<Pasajero> resultado = pasajeroService.buscarHuesped("apellido", "Pérez");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pasajero, resultado.get(0));
        verify(pasajeroRepository).buscarPorApellido("Pérez");
    }

    @Test
    void testBuscarHuesped_CriterioInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> pasajeroService.buscarHuesped("email", "test@test.com"));
        assertTrue(exception.getMessage().contains("Criterio de búsqueda no válido"));
    }

    @Test
    void testBuscarHuesped_CriterioMayusculas() {
        // Arrange
        List<Pasajero> listaPasajeros = Arrays.asList(pasajero);
        when(pasajeroRepository.buscarPorDni("12345678")).thenReturn(listaPasajeros);

        // Act
        List<Pasajero> resultado = pasajeroService.buscarHuesped("DNI", "12345678");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pasajeroRepository).buscarPorDni("12345678");
    }

    @Test
    void testBuscarHuesped_ListaVacia() {
        // Arrange
        when(pasajeroRepository.buscarPorDni("99999999")).thenReturn(Collections.emptyList());

        // Act
        List<Pasajero> resultado = pasajeroService.buscarHuesped("dni", "99999999");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pasajeroRepository).buscarPorDni("99999999");
    }
}
