package com.pizzati.pizzati_cooperativa.repository;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrestamoRepository  extends JpaRepository<Prestamo,Integer> {
    List<Prestamo> findAllByIdParticipante(UUID id);
}
