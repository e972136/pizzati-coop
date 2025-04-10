package com.pizzati.pizzati_cooperativa.controller;

import com.pizzati.pizzati_cooperativa.entity.Depositos;
import com.pizzati.pizzati_cooperativa.entity.Participante;
import com.pizzati.pizzati_cooperativa.entity.Prestamo;
import com.pizzati.pizzati_cooperativa.service.DepositosService;
import com.pizzati.pizzati_cooperativa.service.ParticipanteService;
import com.pizzati.pizzati_cooperativa.service.PrestamoService;
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


import java.util.*;

import static com.pizzati.pizzati_cooperativa.util.MetodosGenerales.mensajeErrorDetalle;
import static java.util.Objects.isNull;

@Slf4j
@Controller
@RequestMapping("/participante")
public class ParticipantesControllerTH {

    private final ParticipanteService participanteService;
    private final DepositosService depositosService;

    private final PrestamoService prestamoService;

    public ParticipantesControllerTH(ParticipanteService participanteService, DepositosService depositosService, PrestamoService prestamoService) {
        this.participanteService = participanteService;
        this.depositosService = depositosService;
        this.prestamoService = prestamoService;
    }

    @GetMapping("/listado")
    public ModelAndView getAllFacturasPorCia(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs,
            @RequestParam(required = false)  String url
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/participante-listado");

        List<Participante> participantes= new ArrayList<>();
        if(rol==1){
            participantes = participanteService.findAll();
            participantes.sort(Comparator.comparing(Participante::getNombreCompleto));
        }else{
            participantes = participanteService.findAll().stream().filter(e->e.getUsuario().equals(usuario)).toList();
        }

        mav.addObject("participantes",participantes);
        if(url !=null){
            mav.addObject("url", url);
        }

        return mav;
    }

    @GetMapping("/crear")
    public ModelAndView crearParticipante(
            @ModelAttribute("participante") CrearParticipante participante,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/participante-crear");

        if(!isNull(participante)){
            mav.addObject("participante",new CrearParticipante(participante) );
        }else{
            mav.addObject("participante",new CrearParticipante());
        }


        return mav;
    }

    @PostMapping("/guardar")
    public ModelAndView guardarParticipante(
            @ModelAttribute("participante") CrearParticipante participante,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("redirect:/participante/listado");

        if(!participante.getUsuario().matches("[a-zA-Z]+")){
            log.error("Error en nombre de usuario:"+participante.getUsuario());

            mav = new ModelAndView("redirect:/participante/crear");
            mav.addObject("nombre",participante.nombre);
            mav.addObject("identidad",participante.identidad);
            mav.addObject("usuario",participante.usuario);
            mav.addObject("aportacionPredeterminada",participante.aportacionPredeterminada);
            mav.addObject("clave",participante.clave);
            mav.addObject("usuarioAdministrador",participante.usuarioAdministrador);

            mensajeErrorDetalle(redirectAttrs,"Error en nombre de usuario",TipoMensaje.WARNING);
            return mav;
        }

        Optional<Participante> participanteBD = participanteService.findByUsuario(participante.getUsuario());

        if(participanteBD.isPresent()){
            log.error("nombre usuario ya existe:"+participanteBD);
            mav = new ModelAndView("redirect:/participante/crear");
            mav.addObject("nombre",participante.nombre);
            mav.addObject("identidad",participante.identidad);
            mav.addObject("usuario",participante.usuario);
            mav.addObject("aportacionPredeterminada",participante.aportacionPredeterminada);
            mav.addObject("clave",participante.clave);
            mav.addObject("usuarioAdministrador",participante.usuarioAdministrador);

            mensajeErrorDetalle(redirectAttrs,"nombre usuario ya existe",TipoMensaje.WARNING);
            return mav;
        }

        Participante creado = participanteService.guardarParticipante(participante,usuario);
        log.info(creado.toString());

        mensajeErrorDetalle(redirectAttrs,"guardado",TipoMensaje.SUCCESS);

        return mav;
    }

    //participante-editar
    @GetMapping("/editar/{id}")
    public ModelAndView editarParticipante(
            @PathVariable UUID id,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/participante-editar");

        Optional<Participante> participanteDB = participanteService.findById(id);

        mav.addObject("participante",participanteDB.get());

        return mav;
    }

    @GetMapping("/ver/{id}")
    public ModelAndView verParticipante(
            @PathVariable UUID id,
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){
        Integer rol = (Integer) request.getSession().getAttribute("rol");
        if(rol == null){
            return new ModelAndView("redirect:/ingreso");
        }
        String usuario = (String) request.getSession().getAttribute("usuario");

        ModelAndView mav = new ModelAndView("./page/participante-ver");

        Optional<Participante> participanteDB = participanteService.findById(id);
        List<Depositos> depositos = depositosService.traerDepositos(id);
        List<Prestamo> prestamos = prestamoService.findAllByIdParticipante(id);


        mav.addObject("participante",participanteDB.get());
        mav.addObject("depositos",depositos);
        mav.addObject("prestamos",prestamos);

        return mav;
    }


//
    @PostMapping("/actualizar")
    public ModelAndView actualizarParticipante(
            HttpServletRequest request,
            RedirectAttributes redirectAttrs
    ){

        ModelAndView mav = new ModelAndView("./page/participante-listado");


        List<Participante> participantes = participanteService.findAll();
        mav.addObject("participantes",participantes);

        return mav;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrearParticipante{
        String nombre;
        String identidad;
        String usuario;
        String clave;
        String aportacionPredeterminada="0";

        boolean usuarioAdministrador;

        public CrearParticipante(CrearParticipante participante) {
            this.nombre = participante.nombre;
            this.identidad = participante.identidad;
            this.usuario = participante.usuario;
            this.clave = participante.clave;
            this.aportacionPredeterminada = participante.aportacionPredeterminada;
        }
    }
}
