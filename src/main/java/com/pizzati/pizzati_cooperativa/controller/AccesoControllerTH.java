package com.pizzati.pizzati_cooperativa.controller;

import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.service.ParticipanteService;
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

import java.util.Optional;

import static com.pizzati.pizzati_cooperativa.util.MetodosGenerales.mensajeErrorDetalle;

@Slf4j
@Controller
@RequestMapping("/ingreso")
public class AccesoControllerTH {

    private final ParticipanteService participanteService;

    public AccesoControllerTH(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @GetMapping()
    public ModelAndView inicio(
    )
    {
        ModelAndView mav = new ModelAndView("./page/menu-principal");

        mav.addObject("credencial", new Credencial());
        return mav;
    }

    @PostMapping("/validar")
    public ModelAndView ingresarSistema(
            @ModelAttribute Credencial credencial,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){
        Optional<Participante> byUsuario = participanteService.findByUsuario(credencial.getUsuario());
        if(byUsuario.isEmpty()){
            mensajeErrorDetalle(redirectAttrs, "Problema de autenticacion", TipoMensaje.DANGER);
            return new ModelAndView("redirect:/ingreso");
        }
        Participante participante = byUsuario.get();

        if(!participante.getClave().equals(credencial.getClave())){
            mensajeErrorDetalle(redirectAttrs, "Problema de autenticacion", TipoMensaje.DANGER);
            return new ModelAndView("redirect:/ingreso");
        }

        request.getSession().setAttribute("usuario", credencial.getUsuario());
        request.getSession().setAttribute("rol", participante.getIdRol());

        return new ModelAndView("redirect:/participante/listado");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Credencial {
        String usuario;
        String clave;
    }
}
