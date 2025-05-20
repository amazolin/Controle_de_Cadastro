package com.example.controle_de_cadastro;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventoDAO {
    private DatabaseReference referencia;

    public EventoDAO() {
        referencia = FirebaseDatabase.getInstance().getReference("eventos");
    }

    public void salvar(Evento evento) {
        String chave = referencia.push().getKey();
        evento.setId(chave);
        referencia.child(chave).setValue(evento);
    }
}
