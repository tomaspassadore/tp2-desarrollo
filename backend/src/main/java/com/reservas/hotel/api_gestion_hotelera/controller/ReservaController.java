package com.reservas.hotel.api_gestion_hotelera.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // <-- USADO
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;      // <-- USADO
import org.springframework.web.bind.annotation.PostMapping;  // <-- USADO
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;                          // <-- USADO
import org.springframework.web.bind.annotation.RequestParam;                     // <-- USADO
import org.springframework.web.bind.annotation.RestController;

import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.service.ReservaService;

// @RestController es una versión especializada de @Controller que incluye @ResponseBody [2, 3]
@RestController
@RequestMapping("/api/reservas") // Define la URL base del recurso
public class ReservaController {
    
    @Autowired 
    private ReservaService reservaService;
    
    //Endpoint POST para crear una reserva usando el DNI del pasajero
    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody Reserva reserva) {
        try {
            Reserva reservaCreada = reservaService.crearReserva(reserva);
            return new ResponseEntity<>(reservaCreada, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la reserva: " + e.getMessage());
        }
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<Reserva> realizarCheckIn(@RequestBody Reserva reserva) {
        // Endpoint secundario de POST
        Reserva reservaActualizada = reservaService.realizarCheckIn(reserva); 
        return new ResponseEntity<>(reservaActualizada, HttpStatus.OK);
    }
    
    // ==========================================================
    // 2. GET: CONSULTAR RECURSOS (CU05: Mostrar disponibilidad)
    // ==========================================================

    // Endpoint 1 de GET: Obtener todas las reservas (Colección)
    @GetMapping 
    public ResponseEntity<Set<Reserva>> obtenerTodasReservas() {
        Set<Reserva> reservas = reservaService.buscarTodas(); // Llama al servicio
        return new ResponseEntity<>(reservas, HttpStatus.OK); 
    }

    // Endpoint 2 de GET: Buscar reservas por nombre de huésped/responsable
    @GetMapping("/buscar")
    public ResponseEntity<List<Reserva>> buscarReservasPorNombre(@RequestParam("nombre") String nombre) {
        List<Reserva> reservas = reservaService.buscarPorNombreHuesped(nombre);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Endpoint 3 de GET: Buscar reservas por DNI del huésped/responsable
    @GetMapping("/buscar-por-dni")
    public ResponseEntity<List<Reserva>> buscarReservasPorDni(@RequestParam("dni") String dni) {
        List<Reserva> reservas = reservaService.buscarPorDniHuesped(dni);
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Endpoint 4 de GET: Buscar una reserva por ID (Recurso único)
    // @PathVariable mapea el ID de la URL (ej. /api/reservas/123) al parámetro del método [7]
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.buscarPorId(id);
        
        // Manejo de la respuesta: 200 OK si existe, 404 NOT FOUND si no [7]
        return reserva.map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // ==========================================================
    // 3. DELETE: ELIMINAR RECURSOS (CU06: Cancelar reserva) [5]
    // ==========================================================

    // Endpoint 1 de DELETE: Cancelar una reserva (CU06)
   @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==========================================================
    // 4. PUT: MODIFICAR RECURSOS (CU07: Facturar) [5]
    // ==========================================================
    
    // 1. PUT: Facturar una reserva (CU07)
// EL TIPO DE RETORNO CAMBIA A Factura
@PutMapping("/facturar/{id}")
public ResponseEntity<Factura> facturarReserva(@PathVariable Long id) {
    
    // EL TIPO DE LA VARIABLE DE CAPTURA CAMBIA A Factura
    Factura facturaGenerada = reservaService.facturar(id); 

    // Retornamos el objeto Factura con HTTP 200 OK
    return new ResponseEntity<>(facturaGenerada, HttpStatus.OK);
}
    
    // Endpoint 2 de PUT: Modificar reserva (general)
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> modificarReserva(@PathVariable Long id, @RequestBody Reserva datosActualizados) {
        // Este método actualiza completamente la entidad
        Reserva reservaModificada = reservaService.modificarReserva(id, datosActualizados);
        return new ResponseEntity<>(reservaModificada, HttpStatus.OK);
    }

}
