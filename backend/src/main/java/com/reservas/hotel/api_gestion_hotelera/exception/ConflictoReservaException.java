package com.reservas.hotel.api_gestion_hotelera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoReservaException extends RuntimeException {

    public ConflictoReservaException(String mensaje) {
        super(mensaje);
    }
}
