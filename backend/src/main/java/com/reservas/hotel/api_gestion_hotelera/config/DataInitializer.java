package com.reservas.hotel.api_gestion_hotelera.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.reservas.hotel.api_gestion_hotelera.entities.Habitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.TipoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.EstadoHabitacion;
import com.reservas.hotel.api_gestion_hotelera.repository.HabitacionRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.TipoHabitacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;

    @Override
    public void run(String... args) {
        // Solo inicializar si no hay datos
        if (tipoHabitacionRepository.count() > 0) {
            log.info("Los datos ya están inicializados");
            return;
        }

        log.info("Inicializando datos de habitaciones...");

        // Crear tipos de habitación
        TipoHabitacion individualEstandar = crearTipoHabitacion(
            "Individual Estándar", 
            new BigDecimal("50800"), 
            10
        );

        TipoHabitacion dobleEstandar = crearTipoHabitacion(
            "Doble Estándar", 
            new BigDecimal("70230"), 
            18
        );

        TipoHabitacion dobleSuperior = crearTipoHabitacion(
            "Doble Superior", 
            new BigDecimal("90560"), 
            8
        );

        TipoHabitacion superiorFamily = crearTipoHabitacion(
            "Superior Family Plan", 
            new BigDecimal("110500"), 
            10
        );

        TipoHabitacion suiteDoble = crearTipoHabitacion(
            "Suite Doble", 
            new BigDecimal("128600"), 
            2
        );

        // Crear habitaciones para cada tipo
        crearHabitaciones(individualEstandar, 101, 10);
        crearHabitaciones(dobleEstandar, 201, 18);
        crearHabitaciones(dobleSuperior, 301, 8);
        crearHabitaciones(superiorFamily, 401, 10);
        crearHabitaciones(suiteDoble, 501, 2);

        log.info("Inicialización completada. Total de habitaciones: " + habitacionRepository.count());
    }

    private TipoHabitacion crearTipoHabitacion(String nombre, BigDecimal costo, Integer cantidad) {
        TipoHabitacion tipo = new TipoHabitacion(nombre, costo, cantidad);
        return tipoHabitacionRepository.save(tipo);
    }

    private void crearHabitaciones(TipoHabitacion tipo, int numeroInicial, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            int numeroHabitacion = numeroInicial + i;
            
            Habitacion habitacion = new Habitacion(
                numeroHabitacion,
                EstadoHabitacion.LIBRE,
                tipo
            );
            
            habitacionRepository.save(habitacion);
        }
        log.info("Creadas " + cantidad + " habitaciones de tipo: " + tipo.getNombre());
    }
}