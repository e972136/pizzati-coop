package com.pizzati.pizzati_cooperativa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rol {
    /**
     * administrador-puede hacer lo que quiera
     * consultor-solo puede ver
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String descripcion;
}
