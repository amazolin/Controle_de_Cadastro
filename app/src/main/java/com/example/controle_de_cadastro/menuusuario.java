package com.example.controle_de_cadastro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class menuusuario extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> listaEventos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menuusuario);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.sair) {
                new androidx.appcompat.app.AlertDialog.Builder(menuusuario.this)
                        .setTitle("Sair")
                        .setMessage("Deseja realmente sair?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            Intent intent = new Intent(menuusuario.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            }

            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.listView);
        listaEventos = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                R.layout.activity_listview,
                R.id.listItemView,
                listaEventos
        );
        listView.setAdapter(adapter);

        carregarEventosDoFirebase();
    }

    private void carregarEventosDoFirebase() {
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("eventos");

        eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();
                for (DataSnapshot dado : snapshot.getChildren()) {
                    Evento evento = dado.getValue(Evento.class);
                    if (evento != null) {
                        String info = "Nome Evento: " + evento.getNome() +
                                "\n\nData Início: " + formatarData(evento.getDataInicio()) + "   " + formatarHora(evento.getHoraInicio()) +
                                "\n\nData Término: " + formatarData(evento.getDataTermino()) + "   " + formatarHora(evento.getHoraTermino());
                        listaEventos.add(info);

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(menuusuario.this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatarHora(String hora) {
        if (hora != null && hora.length() == 4) {
            return hora.substring(0, 2) + ":" + hora.substring(2);
        }
        return hora;
    }


    private String formatarData(String data) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("ddMMyyyy"); // quando os dados vierem assim
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
            return formatoSaida.format(formatoEntrada.parse(data));
        } catch (Exception e) {
            return data; // caso já venha no formato certo ou erro de parsing
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Item selecionado: " + item.getTitle(), Toast.LENGTH_SHORT).show();

        int id = item.getItemId();

        if (id == R.id.sair) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar saída")
                    .setMessage("Deseja realmente sair?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        Intent intent = new Intent(menuusuario.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
