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
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Integer idPrestamo;

    UUID idCliente;

    int cuotaPagada;

    LocalDate fechaPago;

    BigDecimal montoCapital;
    BigDecimal montoInteres;
    BigDecimal montoTotal;
    BigDecimal saldoAnterior;
    BigDecimal saldoActual;

    LocalDate fechaSistema;

    String observacion;

    String posteadoPor;
}
