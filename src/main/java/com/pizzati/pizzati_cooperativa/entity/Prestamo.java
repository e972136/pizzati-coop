package com.pizzati.pizzati_cooperativa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pizzati.pizzati_cooperativa.util.EstadoPrestamo;
import com.pizzati.pizzati_cooperativa.util.ModoPrestamo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    UUID idParticipante;

    @Enumerated(EnumType.STRING)
    ModoPrestamo modoPrestamo;

    String descripcion;

    int numeroPagos;

    int cuotaActual;

    boolean conInteres;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaPrestamo;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaUltimoPago;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaSiguientePago;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaPago;

    @JsonFormat(pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fechaEstimadaTerminacion;

    BigDecimal montoPrestamo;

    BigDecimal montoCuota;


    BigDecimal saldoPrestamo;

    LocalDate fechaSistema;

    String generadoPor;
    String aprobadoPor;

    @Enumerated(EnumType.STRING)
    EstadoPrestamo estadoPrestamo;

}
