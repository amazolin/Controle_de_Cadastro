package com.example.controle_de_cadastro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista_de_Presenca extends AppCompatActivity {

    private ListView lista_presenca;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_de_presenca);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lista_presenca = findViewById(R.id.lista_presenca);
        listaDados = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaDados) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.YELLOW);
                return textView;
            }
        };
        lista_presenca.setAdapter(adapter);

        carregarPresencas();

        // Botão voltar, com listener
        Button voltarLista = findViewById(R.id.voltarLista);
        voltarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Lista_de_Presenca.this, MenuAdministrador.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void carregarPresencas() {
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("eventos");
        DatabaseReference presencasRef = FirebaseDatabase.getInstance().getReference("presencas");

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot eventosSnapshot) {
                for (DataSnapshot eventoSnap : eventosSnapshot.getChildren()) {
                    String eventoId = eventoSnap.getKey();
                    String nomeEvento = eventoSnap.child("nome").getValue(String.class);

                    presencasRef.child(eventoId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot presencasSnapshot) {
                            if (presencasSnapshot.exists()) {
                                listaDados.add("Evento: " + nomeEvento);
                                for (DataSnapshot alunoSnap : presencasSnapshot.getChildren()) {
                                    String nomeAluno = alunoSnap.child("nome").getValue(String.class);
                                    listaDados.add(" - " + nomeAluno);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(Lista_de_Presenca.this, "Erro ao carregar presenças", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Lista_de_Presenca.this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
