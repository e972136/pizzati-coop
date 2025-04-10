package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.repository.PlanPagosRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlanPagoService {

    private final PlanPagosRepository planPagosRepository;

    public PlanPagoService(PlanPagosRepository planPagosRepository) {
        this.planPagosRepository = planPagosRepository;
    }

    public List<PlanPago> generarPlanPago(Prestamo prestamo) {
        List<PlanPago> plan = new ArrayList<>();
        LocalDate pagoActual = prestamo.getFechaPrestamo().plusMonths(1l);
        BigDecimal saldoActual = prestamo.getMontoPrestamo();
         for(int i=0;i<prestamo.getNumeroPagos();i++){

             double montoInteresD = (1.20/365)*saldoActual.doubleValue()*(365.0/12.0);
             BigDecimal montoInteres = BigDecimal.valueOf(montoInteresD);

             BigDecimal montoCapital = prestamo.getMontoCuota().subtract(montoInteres);
             saldoActual = saldoActual.subtract(montoCapital);

             if(i==prestamo.getNumeroPagos()-1){
                 montoCapital = montoCapital.add(saldoActual);
                 saldoActual = BigDecimal.ZERO;
             }

             PlanPago x = new PlanPago(
                     null,
                     prestamo.getId(),
                     i+1,
                     pagoActual,
                     montoInteres,
                     montoCapital,
                     prestamo.getMontoCuota(),
                     saldoActual
             );
             plan.add(x);
             pagoActual = pagoActual.plusMonths(1L);
         }

        return  planPagosRepository.saveAll(plan);
    }

    public List<PlanPago> getPlanPago(Integer idPrestamo) {
        return planPagosRepository.findByIdPrestamo(idPrestamo);
    }
}
