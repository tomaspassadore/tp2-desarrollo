package com.reservas.hotel.api_gestion_hotelera.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.hotel.api_gestion_hotelera.entities.Pasajero;
import com.reservas.hotel.api_gestion_hotelera.service.PasajeroService;

@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    /**
     * DTO para recibir los datos de búsqueda
     */
    public static class BusquedaRequest {
        private String criterio;
        private String valor;

        public BusquedaRequest() {
        }

        public BusquedaRequest(String criterio, String valor) {
            this.criterio = criterio;
            this.valor = valor;
        }

        public String getCriterio() {
            return criterio;
        }

        public void setCriterio(String criterio) {
            this.criterio = criterio;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }

    /**
     * Endpoint POST para buscar huéspedes por criterio (dni, nombre o apellido)
     * @param request Objeto con criterio y valor de búsqueda
     * @return Lista de pasajeros encontrados
     */
    @PostMapping("/buscar")
    public ResponseEntity<List<Pasajero>> buscarHuesped(@RequestBody BusquedaRequest request) {
        try {
            // Validar que los datos estén presentes
            if (request.getCriterio() == null || request.getValor() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Validar que el criterio sea válido
            String criterio = request.getCriterio().toLowerCase();
            if (!criterio.equals("dni") && !criterio.equals("nombre") && !criterio.equals("apellido")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Realizar la búsqueda
            List<Pasajero> pasajeros = pasajeroService.buscarHuesped(request.getCriterio(), request.getValor());
            
            return new ResponseEntity<>(pasajeros, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
    /**
     * Endpoint POST para dar de alta un huésped (crear pasajero con su dirección)
     * @param pasajero Objeto Pasajero con todos sus atributos incluyendo la dirección
     * @return Pasajero creado con código HTTP 201 CREATED
     */
    @PostMapping("/dar-alta")
    public ResponseEntity<Pasajero> darAltaPasajero(@RequestBody Pasajero pasajero) {
        try {
            // El servicio se encarga de guardar tanto la dirección como el pasajero
            Pasajero pasajeroGuardado = pasajeroService.registrarPasajero(pasajero);
            return new ResponseEntity<>(pasajeroGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pasajero> actualizarPasajero(@PathVariable Long id, @RequestBody Pasajero pasajero) {
        try {
            Pasajero pasajeroActualizado = pasajeroService.actualizarPasajero(id, pasajero);
            return new ResponseEntity<>(pasajeroActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint DELETE para dar de baja (eliminar) un huésped por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBajaPasajero(@PathVariable Long id) {
        try {
            pasajeroService.darDeBajaPasajero(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

