package com.pizzati.pizzati_cooperativa.repository;

import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPagosRepository extends JpaRepository<PlanPago,Integer> {
    List<PlanPago> findByIdPrestamo(Integer idPrestamo);
}
