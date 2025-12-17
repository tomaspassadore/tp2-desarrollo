package com.reservas.hotel.api_gestion_hotelera.service;

import com.reservas.hotel.api_gestion_hotelera.entities.Conserje;
import java.util.Optional;
import java.util.Set;

public interface ConserjeService {
    
    // Contrato Básico (CRUD)
    Optional<Conserje> buscarPorId(Long id);
    Set<Conserje> buscarTodos();
    Conserje registrarConserje(Conserje conserje);
    
    // Lógica de Autenticación (Core de Spring Security - UserDetailsService)
    Optional<Conserje> buscarPorUsuario(String usuario); // Útil para el login [8]
    
    // Lógica de Negocio
    Conserje actualizarContrasenia(Long id, String nuevaContrasenia);
    // Podrían agregarse métodos para gestión de roles si la entidad Conserje tuviera roles definidos.
}
