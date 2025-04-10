package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.controller.DepositoControllerTH;
import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.repository.DepositosRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DepositosService {

    private final DepositosRepository depositosRepository;
    private final ParticipanteService participanteService;

    public DepositosService(DepositosRepository depositosRepository, ParticipanteService participanteService) {
        this.depositosRepository = depositosRepository;
        this.participanteService = participanteService;
    }

    public Depositos postearDeposito(DepositoControllerTH.DepositoIngreso deposito, String usuario) {
        Participante participanteDB = participanteService.findById(deposito.getId()).get();

        BigDecimal montoDeposito = new BigDecimal(deposito.getMontoDeposito());

        Depositos guardar = new Depositos(
            null,
                participanteDB.getId(),
                deposito.getDescripcion(),
                montoDeposito,
                participanteDB.getSaldoAhorro(),
                participanteDB.getSaldoAhorro().add(montoDeposito),
                LocalDate.parse(deposito.getFechaDeposito()),
                LocalDate.now(),
                usuario
        );

        guardar = depositosRepository.save(guardar);

        BigDecimal reduce = depositosRepository.findAllByIdCliente(participanteDB.getId())
                .stream()
                .map(e -> e.getMontoDeposito())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        participanteDB.setSaldoAhorro(reduce);

        participanteService.actualizar(participanteDB);

        return guardar;

    }

    public List<Depositos> traerDepositos(UUID id) {
        return depositosRepository.findAllByIdCliente(id);
    }
}
