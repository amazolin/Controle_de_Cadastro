package com.example.controle_de_cadastro;

public interface EventoCallback {
    void onEventoEncontrado(String eventoId);
    void onEventoNaoEncontrado();
    void onErro(Exception e);
}

