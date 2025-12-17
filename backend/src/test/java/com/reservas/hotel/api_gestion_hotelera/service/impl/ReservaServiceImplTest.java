package com.reservas.hotel.api_gestion_hotelera.service.impl;

// Importaciones necesarias de JUnit, Mockito y utilidades
import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*; 
import static org.junit.jupiter.api.Assertions.*; 
import java.util.Optional; 
import java.util.Set; 

// Importa tus clases de la lógica de negocio (entidades, repositorios y otros servicios)
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository;
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService; 
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;

// Anotación clave: Extiende el comportamiento de JUnit para usar Mockito
@ExtendWith(MockitoExtension.class) 
public class ReservaServiceImplTest {

    // 1. CLASE A PROBAR (LA UNIDAD): 
    // @InjectMocks inyecta los objetos simulados (@Mock) en la clase real.
    @InjectMocks
    private ReservaServiceImpl reservaService; 

    // 2. DEPENDENCIAS SIMULADAS (MOCKS): 
    // @Mock crea sustitutos simulados para las dependencias (Repositorio, Contabilidad, etc.).
    // Esto asegura que NO tocaremos la base de datos ni llamaremos a la lógica del ContabilidadService.
    @Mock
    private ReservaRepository reservaRepository; 
    
    @Mock
    private ContabilidadService contabilidadService; 
    
    // Aquí pondremos los métodos de prueba (@Test) en el siguiente paso.
    @Test 
    public void buscarPorId_CuandoExiste_DebeRetornarReserva() {
    
        // --- 1. PREPARACIÓN (ARRANGE) ---
        Long idExistente = 10L;
    
        // Creamos un objeto de prueba (Reserva)
        Reserva reservaSimulada = new Reserva(); 
        // OJO: Asumo que tu clase Reserva tiene un método setId(Long id)
        reservaSimulada.setId(idExistente); 
    
        // Instruir al Mock (el repositorio):
        // Cuando el servicio llame a reservaRepository.findById(10L), Mockito debe devolver 
        // un contenedor Optional lleno con nuestra reserva simulada.
        when(reservaRepository.findById(idExistente)).thenReturn(Optional.of(reservaSimulada)); 

        // --- 2. EJECUCIÓN (ACT) ---
        // Llamamos a la función real que queremos probar
        Optional<Reserva> resultado = reservaService.buscarPorId(idExistente);

        // --- 3. VERIFICACIÓN (ASSERT) ---
        // 3.1. JUnit Assertions: Comprobamos el resultado.
        assertTrue(resultado.isPresent()); // Debe existir (no debe ser Optional.empty())
        assertEquals(idExistente, resultado.get().getId()); 
    
        // 3.2. Mockito Verification: Comprobamos que el repositorio FUE LLAMADO.
        verify(reservaRepository, times(1)).findById(idExistente); // Se debe haber llamado exactamente una vez [4].
    }
}


