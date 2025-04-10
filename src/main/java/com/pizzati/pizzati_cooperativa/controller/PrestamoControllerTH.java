package com.pizzati.pizzati_cooperativa.controller;

import com.pizzati.pizzati_cooperativa.entity.Pagos;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.entity.PlanPago;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.service.PagosService;
import com.pizzati.pizzati_cooperativa.service.ParticipanteService;
import com.pizzati.pizzati_cooperativa.service.PlanPagoService;
import com.pizzati.pizzati_cooperativa.service.PrestamoService;
import com.pizzati.pizzati_cooperativa.util.ComboItem;
import com.pizzati.pizzati_cooperativa.util.TipoMensaje;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pizzati.pizzati_cooperativa.util.MetodosGenerales.cambioFormatoAEstandar;
import static com.pizzati.pizzati_cooperativa.util.MetodosGenerales.mensajeErrorDetalle;

@Slf4j
@Controller
@RequestMapping("/prestamo")
public class PrestamoControllerTH {

    private final ParticipanteService participanteService;
    private final PrestamoService prestamoService;

    private final PagosService pagosService;
    private final PlanPagoService planPagoService;

    public PrestamoControllerTH(ParticipanteService participanteService, PrestamoService prestamoService, PagosService pagosService, PlanPagoService planPagoService) {
        this.participanteService = participanteService;
        this.prestamoService = prestamoService;
        this.pagosService = pagosService;
        this.planPagoService = planPagoService;
    }

    @GetMapping("/crear")
    public ModelAndView crearPrestamo(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/prestamo-crear");


        List<ComboItem> participantes = participanteService.findAll()
                .stream()
                .filter(e -> !e.getUsuario().equals(usuario))
                .map(e -> new ComboItem(e.getUsuario(), e.getNombreCompleto()))
                .toList();


        mav.addObject("participantes", participantes);
        mav.addObject("solicitante", new SolicitantePrestamo());

        return mav;
    }


    @PostMapping("/generar")
    public ModelAndView guardarParticipante(
            @ModelAttribute("solicitante") SolicitantePrestamo solicitante,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("redirect:/participante/listado");

        Prestamo prestamo = prestamoService.guardarPrestamo(solicitante, usuario);


        String url = MessageFormat.format("/prestamo/plan-pago/{0}", prestamo.getId() + "");
        ;
        mav.addObject("url", url);

        mensajeErrorDetalle(redirectAttrs, "guardado", TipoMensaje.SUCCESS);

        return mav;
    }

    @GetMapping("/lista")
    public ModelAndView listarPrestamo(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/prestamo-listado");

        Map<UUID, String> participantes = participanteService.findAll().stream().collect(Collectors.toMap(Participante::getId, Participante::getNombreCompleto));

        List<ListaPrestamo> prestamos = prestamoService.findAll().stream().map(e->ListaPrestamo.fromEntity(e,participantes)).toList();

        mav.addObject("prestamos", prestamos);

        return mav;
    }

    private record ListaPrestamo(
            int id,
            String participante,
            String descripcion,
            String fechaPrestamo,
            String fechaUltimoPago,
            String fechaSiguientePago,
            String montoPrestamo,
            String saldoPrestamo
    ) {
        public static ListaPrestamo fromEntity(Prestamo p,Map<UUID, String> participantes){



            return new ListaPrestamo(
                    p.getId(),
                    participantes.getOrDefault(p.getIdParticipante(),"xxx"),
                    p.getDescripcion(),
                    p.getFechaPrestamo()+"",
                    p.getFechaUltimoPago()+"",
                    p.getFechaSiguientePago()+"",
                    cambioFormatoAEstandar(p.getMontoPrestamo()+""),
                    cambioFormatoAEstandar(p.getSaldoPrestamo()+"")
            );
        }
    }


    @GetMapping("/pago-parcial/{idPrestamo}")
    public ModelAndView pagarPrestamo(
            @PathVariable int idPrestamo,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/prestamo-pagar");

        Prestamo prestamo = prestamoService.findById(idPrestamo).get();

        System.out.println(prestamo);
        System.out.println(prestamo.getSaldoPrestamo().equals(BigDecimal.ZERO));

        if (prestamo.getSaldoPrestamo().doubleValue() == 0.0) {
            mensajeErrorDetalle(redirectAttrs, "Sin valores pendientes", TipoMensaje.DANGER);
            return new ModelAndView("redirect:/prestamo/lista");
        }

        mav.addObject("prestamo", prestamo);
        mav.addObject("cuota", new Cuota(prestamo.getId(), prestamo.getFechaUltimoPago().toString()));

        return mav;
    }


    @GetMapping("/pago-cuota/{idPrestamo}")
    public ModelAndView pagarCuotaPrestamo(
            @PathVariable int idPrestamo,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/prestamo-pagar-cuota");

        Prestamo prestamo = prestamoService.findById(idPrestamo).get();

        List<PlanPago> planPagos = planPagoService.getPlanPago(prestamo.getId());

        Optional<PlanPago> planPagoOp = planPagos.stream()
                .filter(e -> e.getNumeroCuota() == prestamo.getCuotaActual())
                .findFirst();


        if (planPagoOp.isEmpty() || prestamo.getSaldoPrestamo().doubleValue() == 0.0) {
            mensajeErrorDetalle(redirectAttrs, "Sin valores pendientes", TipoMensaje.DANGER);
            return new ModelAndView("redirect:/prestamo/lista");
        }

        PlanPago planPago = planPagoOp.get();
        mav.addObject("prestamo", prestamo);
        mav.addObject("cuota", new Cuota(
                prestamo.getId(),
                prestamo.getFechaUltimoPago().toString(),
                planPago.getMontoCapital().toString(),
                planPago.getMontoInteres().toString(),
                planPago.getMontoCuota().toString()
        ));

        return mav;
    }

    @GetMapping("/plan-pago/{idPrestamo}")
    public ModelAndView planPago(
            @PathVariable int idPrestamo
    ) {
        List<PlanPago> planPago = planPagoService.getPlanPago(idPrestamo);
        Prestamo prestamo = prestamoService.findById(idPrestamo).get();
        Participante participante = participanteService.findById(prestamo.getIdParticipante()).get();
        PrestamoPlanPago prestamoInfo = new PrestamoPlanPago(
                participante.getNombreCompleto(),
                prestamo.getDescripcion(),
                prestamo.getId() + "",
                prestamo.getEstadoPrestamo() + "",
                prestamo.getFechaPrestamo().toString(),
                prestamo.getNumeroPagos() + "",
                prestamo.getFechaEstimadaTerminacion().toString(),
                cambioFormatoAEstandar(prestamo.getMontoPrestamo().toString()),
                cambioFormatoAEstandar(prestamo.getMontoCuota().toString())
        );
        ModelAndView mav = new ModelAndView("./page/plan-pago");
        mav.addObject("planPago", planPago);
        mav.addObject("prestamo", prestamoInfo);
        return mav;
    }

    @GetMapping("/ver/{idPrestamo}")
    public ModelAndView verPrestamo(
            @PathVariable int idPrestamo
    ){
        ModelAndView mav = new ModelAndView("./page/prestamo-ver");

        Prestamo prestamo = prestamoService.findById(idPrestamo).get();

        Participante participante = participanteService.findById(prestamo.getIdParticipante()).get();

        InfoPrestamo infoPrestamo = new InfoPrestamo(
                prestamo.getId()+"",
                participante.getNombreCompleto(),
                prestamo.getDescripcion(),
                prestamo.getNumeroPagos()+"",
                prestamo.getFechaPrestamo()+"",
                cambioFormatoAEstandar(prestamo.getMontoPrestamo()+""),
                cambioFormatoAEstandar(prestamo.getMontoCuota()+"")
        );

        mav.addObject("infoPrestamo", infoPrestamo);

        return mav;
    }


    public record InfoPrestamo(
            String id,
        String usuarioNombre,
        String descripcion,
        String numeroPagos,
        String fechaPrestamo,
        String montoPrestamo,
        String montoCuota
    ) {
    }





    @PostMapping("/pago-cuota")
    public ModelAndView pagarCuotaPrestamo(
            @ModelAttribute("cuota") Cuota cuota,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ) {
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if (rol == null) {
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");


        Pagos pagos = pagosService.efectuarPagoCuota(cuota, usuario);


        ModelAndView mav = new ModelAndView("redirect:/participante/listado");


        mensajeErrorDetalle(redirectAttrs, "guardado", TipoMensaje.SUCCESS);

        return mav;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cuota {
        Integer idPrestamo;
        String fechaUltimoPago;
        String fechaPago;
        String montoCapital;
        String montoInteres;
        String montoTotal;
        String observacion;

        public Cuota(Integer idPrestamo, String fechaUltimoPago) {
            this.idPrestamo = idPrestamo;
            this.fechaUltimoPago = fechaUltimoPago;
        }

        public Cuota(Integer idPrestamo, String fechaUltimoPago, String montoCapital, String montoInteres, String montoTotal) {
            this.idPrestamo = idPrestamo;
            this.fechaUltimoPago = fechaUltimoPago;
            this.montoCapital = montoCapital;
            this.montoInteres = montoInteres;
            this.montoTotal = montoTotal;
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SolicitantePrestamo {
        String usuarioSolicitante;
        String descripcion;
        String numeroPagos;
        String fechaPrestamo;
        String montoPrestamo;
        String montoCuota;
    }

    public record PrestamoPlanPago(
            String idParticipante,
            String descripcion,
            String id,
            String estadoPrestamo,
            String fechaPrestamo,
            String numeroPagos,
            String fechaEstimadaTerminacion,
            String montoPrestamo,
            String montoCuota
    ) {

    }



}
