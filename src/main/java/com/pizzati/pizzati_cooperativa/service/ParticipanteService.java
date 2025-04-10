package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.controller.ParticipantesControllerTH;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    public List<Participante> findAll() {
        return participanteRepository.findAll();
    }

    public Optional<Participante> findByUsuario(String usuario) {
        return participanteRepository.findByUsuario(usuario);
    }

    public Participante guardarParticipante(ParticipantesControllerTH.CrearParticipante participante, String creadoPor) {
        int rol = 2;
        if(participante.isUsuarioAdministrador()){
            rol = 1;
        }
        Participante nuevo = new Participante(
          null,
                participante.getNombre(),
                participante.getIdentidad(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal(participante.getAportacionPredeterminada()),
                rol,
                participante.getUsuario(),
                participante.getClave(),
                creadoPor
        );
        return participanteRepository.save(nuevo);
    }

    public Optional<Participante> findById(UUID id) {
        return participanteRepository.findById(id);
    }

    public Participante actualizar(Participante usuarioDb) {
        return participanteRepository.save(usuarioDb);
    }
}
