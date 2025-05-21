package com.example.controle_de_cadastro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class menuusuario extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> listaEventosVisiveis;
    private List<String> listaIdsEventos;

    private String cpfAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuusuario);

        cpfAluno = getIntent().getStringExtra("cpfAluno");
        if (cpfAluno == null) {
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            cpfAluno = prefs.getString("cpf", null);
        }

        if (cpfAluno == null) {
            Toast.makeText(this, "Erro ao obter CPF do usuário", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(item -> {
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

            if (id == R.id.ticket) {
                if (listaEventosVisiveis.isEmpty()) {
                    Toast.makeText(menuusuario.this, "Nenhum evento disponível para gerar QR Code", Toast.LENGTH_SHORT).show();
                    return true;
                }

                String eventoSelecionado = listaEventosVisiveis.get(0);
                String nomeEvento = extrairNomeEvento(eventoSelecionado); // Aqui a correção foi aplicada

                DatabaseReference alunoRef = FirebaseDatabase.getInstance()
                        .getReference("alunos")
                        .child(cpfAluno);

                alunoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String nomeAluno = snapshot.child("nome").getValue(String.class);
                            String emailAluno = snapshot.child("email").getValue(String.class);
                            String cpf = snapshot.child("cpf").getValue(String.class);

                            if (nomeAluno != null && emailAluno != null && cpf != null) {
                                String conteudoQR = "Nome: " + nomeAluno +
                                        "\nCPF: " + cpf +
                                        "\nEmail: " + emailAluno +
                                        "\nEvento: " + nomeEvento +
                                        "\nEventoId: " + id;

                                Intent intent = new Intent(menuusuario.this, QR_Code.class);
                                intent.putExtra("conteudoQR", conteudoQR);
                                startActivity(intent);
                            } else {
                                Toast.makeText(menuusuario.this, "Dados incompletos do aluno", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(menuusuario.this, "Aluno não encontrado no banco de dados", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(menuusuario.this, "Erro ao buscar dados do aluno", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }

            return false;
        });

        ListView listView = findViewById(R.id.listView);
        listaEventosVisiveis = new ArrayList<>();
        listaIdsEventos = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                R.layout.activity_listview,
                R.id.listItemView,
                listaEventosVisiveis
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String eventoSelecionado = listaEventosVisiveis.get(position);
            String eventoId = listaIdsEventos.get(position);

            new androidx.appcompat.app.AlertDialog.Builder(menuusuario.this)
                    .setTitle("Participar do Evento")
                    .setMessage("Deseja participar deste evento?\n\n" + eventoSelecionado)
                    .setPositiveButton("Participar", (dialog, which) -> {

                        DatabaseReference presencasRef = FirebaseDatabase.getInstance().getReference("presencas");

                        presencasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean jaPresenteEmOutroEvento = false;
                                String eventoAtual = null;

                                for (DataSnapshot eventoSnapshot : snapshot.getChildren()) {
                                    if (eventoSnapshot.hasChild(cpfAluno)) {
                                        jaPresenteEmOutroEvento = true;
                                        eventoAtual = eventoSnapshot.getKey();
                                        break;
                                    }
                                }

                                if (jaPresenteEmOutroEvento) {
                                    Toast.makeText(menuusuario.this,
                                            "Aluno já está registrado no evento: " + eventoAtual,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    DatabaseReference alunoRef = FirebaseDatabase.getInstance()
                                            .getReference("alunos")
                                            .child(cpfAluno);

                                    alunoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String nomeAluno = snapshot.child("nome").getValue(String.class);
                                                String emailAluno = snapshot.child("email").getValue(String.class);
                                                String cpf = snapshot.child("cpf").getValue(String.class);

                                                if (nomeAluno != null && emailAluno != null && cpf != null) {
                                                    DatabaseReference presencaRef = presencasRef.child(eventoId).child(cpfAluno);

                                                    Map<String, Object> dadosAluno = new HashMap<>();
                                                    dadosAluno.put("nome", nomeAluno);
                                                    dadosAluno.put("email", emailAluno);
                                                    dadosAluno.put("horaEntrada", horaAtual());

                                                    presencaRef.setValue(dadosAluno)
                                                            .addOnSuccessListener(unused ->
                                                                    Toast.makeText(menuusuario.this, "Presença registrada com sucesso!", Toast.LENGTH_SHORT).show())
                                                            .addOnFailureListener(e ->
                                                                    Toast.makeText(menuusuario.this, "Erro ao registrar presença", Toast.LENGTH_SHORT).show());
                                                } else {
                                                    Toast.makeText(menuusuario.this, "Dados incompletos do aluno", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(menuusuario.this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(menuusuario.this, "Erro ao buscar dados do aluno", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(menuusuario.this, "Erro ao verificar presença do aluno", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        carregarEventosDoFirebase();
    }

    private String horaAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void carregarEventosDoFirebase() {
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference("eventos");

        eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventosVisiveis.clear();
                listaIdsEventos.clear();

                for (DataSnapshot dado : snapshot.getChildren()) {
                    Evento evento = dado.getValue(Evento.class);
                    if (evento != null) {
                        String info = "Nome Evento: " + evento.getNome() +
                                "\n\nData Início: " + formatarData(evento.getDataInicio()) + "   " + formatarHora(evento.getHoraInicio()) +
                                "\n\nData Término: " + formatarData(evento.getDataTermino()) + "   " + formatarHora(evento.getHoraTermino());

                        listaEventosVisiveis.add(info);
                        listaIdsEventos.add(evento.getId());
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
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
            return formatoSaida.format(formatoEntrada.parse(data));
        } catch (Exception e) {
            return data;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private String extrairNomeEvento(String eventoStr) {
        if (eventoStr.contains("Nome Evento:")) {
            int inicio = eventoStr.indexOf("Nome Evento:") + "Nome Evento:".length();
            int fim = eventoStr.indexOf("\n", inicio);
            if (fim == -1) fim = eventoStr.length();
            return eventoStr.substring(inicio, fim).trim();
        }
        return "Evento Desconhecido";
    }
}
