package com.pizzati.pizzati_cooperativa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    int idPrestamo;

    int numeroCuota;

    LocalDate fechaPago;

    BigDecimal montoInteres;
    BigDecimal montoCapital;
    BigDecimal montoCuota;
    BigDecimal saldoActual;
}
