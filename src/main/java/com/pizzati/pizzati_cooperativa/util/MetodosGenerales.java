package com.pizzati.pizzati_cooperativa.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MetodosGenerales {
    public static void mensajeErrorDetalle(RedirectAttributes redirectAttrs, String mensaje, TipoMensaje tipoMensaje) {
        redirectAttrs
                .addFlashAttribute("mensaje", mensaje)
                .addFlashAttribute("clase", tipoMensaje.getData());
    }

    public static String cambioFormatoAEstandar(String original){
        if(original.isEmpty()) {
            return original;
        }
        DecimalFormat formato = new DecimalFormat("#,###.##",new DecimalFormatSymbols(Locale.ENGLISH));
        String v=original;
        try {
            v = formato.format(Double.parseDouble(original));
        }catch(Exception e){
            return v;
        }
        if(!v.contains(".")){
            v = v+".00";
        }
        else
        {
            int pos=v.length()-v.indexOf(".");
            if(pos<=2){
                v = v + "0";
            }
        }
        return v;
    }
}
