package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.controller.PrestamoControllerTH;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.repository.PrestamoRepository;
import com.pizzati.pizzati_cooperativa.util.EstadoPrestamo;
import com.pizzati.pizzati_cooperativa.util.ModoPrestamo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final ParticipanteService participanteService;

    private final PlanPagoService planPagoService;

    public PrestamoService(PrestamoRepository prestamoRepository, ParticipanteService participanteService, PlanPagoService planPagoService) {
        this.prestamoRepository = prestamoRepository;
        this.participanteService = participanteService;
        this.planPagoService = planPagoService;
    }

    public Prestamo guardarPrestamo(PrestamoControllerTH.SolicitantePrestamo solicitante, String usuario) {

        Participante usuarioDb = participanteService.findByUsuario(solicitante.getUsuarioSolicitante()).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        LocalDate fechaPrestamo = LocalDate.parse(solicitante.getFechaPrestamo());
        LocalDate fechaPrimeraCuota = LocalDate.parse(solicitante.getFechaPrestamo()).plusMonths(1L);

        Prestamo p = new Prestamo(
                null,
                usuarioDb.getId(),
                ModoPrestamo.valueOf(solicitante.getModoPrestamo()),
                solicitante.getDescripcion(),
                Integer.valueOf(solicitante.getNumeroPagos()),
                1,
                true,
                fechaPrestamo,
                fechaPrestamo,
                fechaPrimeraCuota,
                LocalDate.parse("1900-01-01"),
                LocalDate.now().plusMonths(Long.valueOf(solicitante.getNumeroPagos())),
                new BigDecimal(solicitante.getMontoPrestamo()),
                new BigDecimal(solicitante.getMontoCuota()),
                new BigDecimal(solicitante.getMontoPrestamo()),
                LocalDate.now(),
                usuario,
                usuario,
                EstadoPrestamo.ACTIVO
        );
        Prestamo save = prestamoRepository.save(p);
        BigDecimal reduce = prestamoRepository.findAllByIdParticipante(usuarioDb.getId()).stream().map(e -> e.getSaldoPrestamo()).reduce(BigDecimal.ZERO, BigDecimal::add);
        usuarioDb.setSaldoPrestamo(reduce);
        participanteService.actualizar(usuarioDb);

        List<PlanPago> planPagos = planPagoService.generarPlanPago(save);

        return save;

    }

    public List<Prestamo> findAll() {
        return prestamoRepository.findAll();
    }


    public Optional<Prestamo> findById(int idPrestamo) {
        return prestamoRepository.findById(idPrestamo);
    }

    public Prestamo actualizarPrestamo(Prestamo prestamo) {
        return prestamoRepository.save(prestamo);
    }

    public List<Prestamo> findAllByIdParticipante(UUID idParticipante) {
        return prestamoRepository.findAllByIdParticipante(idParticipante);
    }
}
/*









 */
