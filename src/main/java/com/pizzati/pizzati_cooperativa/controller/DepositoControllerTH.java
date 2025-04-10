package com.pizzati.pizzati_cooperativa.controller;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.service.DepositosService;
import com.pizzati.pizzati_cooperativa.service.ParticipanteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/deposito")
public class DepositoControllerTH {
    //

    private final ParticipanteService participanteService;
    private final DepositosService depositosService;

    public DepositoControllerTH(ParticipanteService participanteService, DepositosService depositosService) {
        this.participanteService = participanteService;
        this.depositosService = depositosService;
    }

    @GetMapping("/ingreso/{id}")
    public ModelAndView getAllFacturasPorCia(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs,
            @PathVariable UUID id
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");


      ModelAndView mav = new ModelAndView("./page/deposito-postear");

      String descripcion;

        String []meses = {"","Enero","Febero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        System.out.println(LocalDate.now().getMonthValue());
        if(LocalDate.now().getDayOfMonth()<15){
            descripcion = "Quincena 1 " + meses[LocalDate.now().getMonthValue()];
        } else {
            descripcion = "Quincena 2 " + meses[LocalDate.now().getMonthValue()];
        }

        DepositoIngreso deposito =  participanteService.findById(id)
                .map(
                e->new DepositoIngreso(
                        id,
                        e.getNombreCompleto(),
                        descripcion,
                        e.getAportacionPredeterminada()+"",
                        LocalDate.now()+""
                )).get();

        mav.addObject("deposito",deposito);
        return mav;
    }

    @PostMapping("/posteo")
    public ModelAndView posteoDeposito(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs,
            @ModelAttribute("participante") DepositoIngreso deposito
            ){

        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");


        ModelAndView mav = new ModelAndView("redirect:/participante/listado");

        Depositos depositos = depositosService.postearDeposito(deposito,usuario);


        return mav;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DepositoIngreso{
        UUID id;
        String nombre;
        String descripcion;
        String montoDeposito;
        String fechaDeposito;
    }

}
