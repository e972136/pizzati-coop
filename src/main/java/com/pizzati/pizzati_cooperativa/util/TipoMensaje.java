package com.pizzati.pizzati_cooperativa.util;

public enum TipoMensaje {
    WARNING("warning"),
    SUCCESS("success"),
    DANGER("danger"),
    INFO("info");
    private final String data;
    TipoMensaje(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
