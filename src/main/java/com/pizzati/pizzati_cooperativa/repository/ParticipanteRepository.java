package com.pizzati.pizzati_cooperativa.repository;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.entity.Pagos;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipanteRepository  extends JpaRepository<Participante, UUID> {
    Optional<Participante> findByUsuario(String usuario);
}
