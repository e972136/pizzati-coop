package com.pizzati.pizzati_cooperativa.service;

import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.repository.PlanPagosRepository;
import com.pizzati.pizzati_cooperativa.util.ModoPrestamo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        int numeroPagos = prestamo.getNumeroPagos();

        List<PlanPago> plan = new ArrayList<>();
        LocalDate fechaPagoActual = prestamo.getFechaPrestamo().plusMonths(1l);
        BigDecimal saldoActual = prestamo.getMontoPrestamo();
        BigDecimal montoCuota = prestamo.getMontoCuota();
        int iq = 0;
        for(int i=0;i<numeroPagos;i++){

             double montoInteresD = (1.20/365)*saldoActual.doubleValue()*(365.0/12.0);
             BigDecimal montoInteres = BigDecimal.valueOf(montoInteresD);


            PlanPago x;

            if(prestamo.getModoPrestamo().equals(ModoPrestamo.QUINCENAL)){
                iq++;

                BigDecimal interesA = BigDecimal.valueOf(montoInteres.doubleValue()/2.0).setScale(2,RoundingMode.CEILING);
                BigDecimal capitalA = montoCuota.subtract(interesA);


                saldoActual = saldoActual.subtract(capitalA);
                x = new PlanPago(
                        null,
                        prestamo.getId(),
                        iq,
                        fechaPagoActual,
                        interesA,
                        capitalA,
                        montoCuota,
                        saldoActual
                );
                plan.add(x);
                iq++;
                saldoActual = saldoActual.subtract(capitalA);

                if(i==numeroPagos-1){
                    capitalA = capitalA.add(saldoActual);
                    saldoActual = BigDecimal.ZERO;
                }

                x = new PlanPago(
                        null,
                        prestamo.getId(),
                        iq,
                        fechaPagoActual.plusDays(15),
                        interesA,
                        capitalA,
                        montoCuota,
                        saldoActual
                );
                plan.add(x);

            }else{

                BigDecimal montoCapital = prestamo.getMontoCuota().subtract(montoInteres);


                if(i==numeroPagos-1){
                    montoCapital = montoCapital.add(saldoActual);
                    saldoActual = BigDecimal.ZERO;
                }

                saldoActual = saldoActual.subtract(montoCapital);
                x = new PlanPago(
                        null,
                        prestamo.getId(),
                        i+1,
                        fechaPagoActual,
                        montoInteres,
                        montoCapital,
                        montoCuota,
                        saldoActual
                );
                plan.add(x);
            }

             fechaPagoActual = fechaPagoActual.plusMonths(1L);
         }

        return  planPagosRepository.saveAll(plan);
    }

    public List<PlanPago> getPlanPago(Integer idPrestamo) {
        return planPagosRepository.findByIdPrestamo(idPrestamo);
    }
}
