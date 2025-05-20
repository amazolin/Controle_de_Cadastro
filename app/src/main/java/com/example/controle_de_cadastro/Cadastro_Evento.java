package com.example.controle_de_cadastro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Cadastro_Evento extends AppCompatActivity {

    EditText txtEvento, txtDataInicio, txtHoraInicio, txtDataTermino, txtHoraTermino;
    Button btnCriarEvento, btnSairEvento;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_evento);

        txtEvento = findViewById(R.id.txtEvento);
        txtDataInicio = findViewById(R.id.txtDataInicio);
        txtHoraInicio = findViewById(R.id.txtHoraInicio);
        txtDataTermino = findViewById(R.id.txtDataTermino);
        txtHoraTermino = findViewById(R.id.txtHoraTermino);
        btnCriarEvento = findViewById(R.id.btnCriarEvento);
        btnSairEvento = findViewById(R.id.btnSairEvento);

        btnCriarEvento.setOnClickListener(v -> {
            try {
                String nome = txtEvento.getText().toString();
                String dataInicio = txtDataInicio.getText().toString();
                String horaInicio = txtHoraInicio.getText().toString();
                String dataTermino = txtDataTermino.getText().toString();
                String horaTermino = txtHoraTermino.getText().toString();

                Evento evento = new Evento(nome, dataInicio, dataTermino, horaInicio, horaTermino);

                EventoDAO dao = new EventoDAO();
                dao.salvar(evento);

                Toast.makeText(this, "Evento cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Erro ao cadastrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        btnSairEvento.setOnClickListener(v -> sairParaMainActivity());
    }

    private void sairParaMainActivity() {
        Intent intent = new Intent(Cadastro_Evento.this, MenuAdministrador.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
