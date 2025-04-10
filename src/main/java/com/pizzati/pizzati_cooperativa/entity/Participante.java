package com.pizzati.pizzati_cooperativa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;


    String nombreCompleto;

    String identidad;

    BigDecimal saldoAhorro;

    BigDecimal saldoPrestamo;

    BigDecimal aportacionPredeterminada;

    Integer idRol;

    String usuario;

    String clave;

    String creadoPor;
}
