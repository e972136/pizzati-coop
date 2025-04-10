package com.pizzati.pizzati_cooperativa.repository;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.entity.Pagos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagosRepository  extends JpaRepository<Pagos,Integer> {
}
