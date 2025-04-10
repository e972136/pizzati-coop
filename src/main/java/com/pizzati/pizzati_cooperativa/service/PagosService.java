package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.controller.PrestamoControllerTH;
import com.pizzati.pizzati_cooperativa.entity.Pagos;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.repository.PagosRepository;
import com.pizzati.pizzati_cooperativa.util.EstadoPrestamo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagosService {

    private final PrestamoService prestamoService;
    private final PagosRepository pagosRepository;

    private final PlanPagoService planPagoService;

    private final ParticipanteService participanteService;

    public PagosService(PrestamoService prestamoService, PagosRepository pagosRepository, PlanPagoService planPagoService, ParticipanteService participanteService) {
        this.prestamoService = prestamoService;
        this.pagosRepository = pagosRepository;
        this.planPagoService = planPagoService;
        this.participanteService = participanteService;
    }

    public Pagos efectuarPagoCuota(PrestamoControllerTH.Cuota cuota, String usuario) {
        Prestamo prestamo = prestamoService.findById(cuota.getIdPrestamo()).get();
        BigDecimal montoCapital = new BigDecimal(cuota.getMontoCapital());
        BigDecimal saldoActual = prestamo.getSaldoPrestamo().subtract(montoCapital);
        Pagos pago = new Pagos(
                null,
                prestamo.getId(),
                prestamo.getIdParticipante(),
                prestamo.getCuotaActual(),
                LocalDate.parse(cuota.getFechaPago()),
                montoCapital,
                new BigDecimal(cuota.getMontoInteres()),
                new BigDecimal(cuota.getMontoTotal()),
                prestamo.getSaldoPrestamo(),
                saldoActual,
                LocalDate.now(),
                cuota.getObservacion(),
                usuario
        );

        int siguienteCuota = prestamo.getCuotaActual()+1;

        Optional<PlanPago> planPagoOptional = planPagoService.getPlanPago(cuota.getIdPrestamo())
                .stream()
                .filter(p->p.getNumeroCuota()==siguienteCuota).findFirst();

        if(planPagoOptional.isEmpty()){
            prestamo.setFechaPago(LocalDate.now());
            prestamo.setEstadoPrestamo(EstadoPrestamo.CANCELADO);
        }else{
            LocalDate fechaSiguientePago = planPagoOptional.get().getFechaPago();
            prestamo.setFechaSiguientePago(fechaSiguientePago);
        }

        prestamo.setSaldoPrestamo(saldoActual);
        prestamo.setCuotaActual(siguienteCuota);
        prestamo.setFechaUltimoPago(LocalDate.parse(cuota.getFechaPago()));

        prestamo = prestamoService.actualizarPrestamo(prestamo);

        /**
         * actualiar saldo en cliente
         */

        Participante participante = participanteService.findById(prestamo.getIdParticipante()).get();

        BigDecimal reduce = prestamoService.findAllByIdParticipante(participante.getId()).stream().map(e -> e.getSaldoPrestamo()).reduce(BigDecimal.ZERO, BigDecimal::add);
        participante.setSaldoPrestamo(reduce);
        participanteService.actualizar(participante);

        return pagosRepository.save(pago);

    }

    /**
     *         Integer idPrestamo;
     *         String fechaUltimoPago;
     *         String fechaPago;
     *         String montoCapital;
     *         String montoInteres;
     *         String montoTotal;
     *         String observacion;
     */
}
