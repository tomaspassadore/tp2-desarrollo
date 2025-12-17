package com.reservas.hotel.api_gestion_hotelera.service.impl;

// Importaciones para las Entidades
import com.reservas.hotel.api_gestion_hotelera.entities.Factura;
import com.reservas.hotel.api_gestion_hotelera.entities.ItemFactura;
import com.reservas.hotel.api_gestion_hotelera.entities.NotaDeCredito;
import com.reservas.hotel.api_gestion_hotelera.entities.Pago; 
import com.reservas.hotel.api_gestion_hotelera.entities.Reserva;
import com.reservas.hotel.api_gestion_hotelera.entities.enums.TipoFactura;
// Importaciones de Repositorios (Capa de Persistencia)
import com.reservas.hotel.api_gestion_hotelera.repository.FacturaRepository; 
import com.reservas.hotel.api_gestion_hotelera.repository.NotaDeCreditoRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.PagoRepository;
import com.reservas.hotel.api_gestion_hotelera.repository.ReservaRepository; 

// Importaciones del Servicio y Utilidades
import com.reservas.hotel.api_gestion_hotelera.service.ContabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; // Para manejar la lógica de negocio como una unidad atómica

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport; // Para manejar la conversión de Iterable de los repositorios

@Service
public class ContabilidadServiceImpl implements ContabilidadService {
    
    // Inyección de Repositorios (la Capa de Servicio agrupa su funcionalidad) [2, 4]
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private NotaDeCreditoRepository notaDeCreditoRepository;

    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    // ==========================================================
    // MÉTODOS DE FACTURA (CU07)
    // ==========================================================
    
    @Override
    @Transactional
    public Factura generarFactura(Reserva reserva) {

        Factura factura = new Factura();
        factura.setReservaAsociada(reserva);
        factura.setFechaDeEmision(new Date());
        factura.setTipo(TipoFactura.B);

        long noches = calcularNoches(
            reserva.getFechaIngreso(),
            reserva.getFechaEgreso()
        );


        // 2. Obtener precio por noche (BigDecimal)
        BigDecimal precioNoche = reserva.getHabitacion()
                .getTipoHabitacion()
                .getCostoPorNoche();

        // 3. Calcular total (BigDecimal)
        BigDecimal total = precioNoche.multiply(BigDecimal.valueOf(noches));

        // 4. Guardar total en Factura (Double)
        factura.setImporteTotal(total.doubleValue());
        // 5. Crear ítem de factura
        ItemFactura item = new ItemFactura();
        item.setDescripcion("Alojamiento habitación " + reserva.getHabitacion().getNumero());
        item.setCantidad((int) noches);
        item.setPrecioUnitario(precioNoche.doubleValue());
        item.setSubtotal(total.doubleValue());
        item.setFactura(factura);

        // 6. Asociar ítem a la factura
        factura.getItems().add(item);

        // 7. Guardar factura (cascade guarda el ítem)
        return facturaRepository.save(factura);
    }

    
    @Override
    @Transactional
    public Factura crearFactura(Long reservaId, Double importeTotal, Date fechaDeEmision, TipoFactura tipo) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));
        
        Factura factura = new Factura();
        factura.setReservaAsociada(reserva);
        factura.setImporteTotal(importeTotal);
        factura.setFechaDeEmision(fechaDeEmision);
        factura.setTipo(tipo != null ? tipo : TipoFactura.A);
        
        return facturaRepository.save(factura);
    }
    
    @Override // <--- Implementa ContabilidadService.buscarFacturaPorId(Long)
    public Optional<Factura> buscarFacturaPorId(Long id) {
        return facturaRepository.findById(id);
    }
    
    @Override // <--- Implementa ContabilidadService.buscarTodasFacturas()
    public Set<Factura> buscarTodasFacturas() {
        // Usa StreamSupport para convertir Iterable a Set
        return StreamSupport.stream(facturaRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }

    // ==========================================================
    // MÉTODOS DE NOTA DE CRÉDITO (CU19)
    // ==========================================================

    @Override
    public NotaDeCredito registrarNotaDeCredito(NotaDeCredito nota) {
        return notaDeCreditoRepository.save(nota);
    }
    
    @Override // <--- Implementa ContabilidadService.buscarNotaDeCreditoPorId(Long)
    public Optional<NotaDeCredito> buscarNotaDeCreditoPorId(Long id) {
        return notaDeCreditoRepository.findById(id);
    }

    @Override // <--- Implementa ContabilidadService.buscarTodasNotasDeCredito()
    public Set<NotaDeCredito> buscarTodasNotasDeCredito() {
        // Conversión segura de Iterable a Set
        return StreamSupport.stream(notaDeCreditoRepository.findAll().spliterator(), false)
               .collect(Collectors.toSet());
    }

    // ==========================================================
    // MÉTODOS DE PAGO
    // ==========================================================
    
    @Override
    @Transactional 
    public Pago registrarPago(Pago pago) {
        return pagoRepository.save(pago); 
    }
    
    @Override
    public Optional<Pago> buscarPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }
    
    private long calcularNoches(java.util.Date ingreso, java.util.Date egreso) {
        java.time.LocalDate in = ingreso.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        java.time.LocalDate out = egreso.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        return java.time.temporal.ChronoUnit.DAYS.between(in, out);
    }

}