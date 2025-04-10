package com.pizzati.pizzati_cooperativa.repository;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepositosRepository extends JpaRepository<Depositos,Integer> {
    List<Depositos> findAllByIdCliente(UUID id);
}
