package com.example.controle_de_cadastro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Cadastro_Evento extends AppCompatActivity {

    EditText txtEvento, txtDataInicio, txtHoraInicio, txtDataTermino, txtHoraTermino;
    Button btnCriarEvento;

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

    }
}
