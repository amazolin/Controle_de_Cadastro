package com.example.controle_de_cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MenuAdministrador extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.criar_eventos) {
                    Intent intent = new Intent(MenuAdministrador.this, Cadastro_Evento.class);
                    startActivity(intent);
                    return true;

                } else if (id == R.id.ler_QR_code) {
                    IntentIntegrator integrator = new IntentIntegrator(MenuAdministrador.this);
                    integrator.setPrompt("Aponte a câmera para o QR Code");
                    integrator.setOrientationLocked(false);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                    return true;

                } else if (id == R.id.lista_participante) {
                    Intent intent = new Intent(MenuAdministrador.this, Lista_de_Presenca.class);
                    startActivity(intent);
                    return true;

                } else if (id == R.id.sair) {
                    Intent intent = new Intent(MenuAdministrador.this, MainActivity.class);
                    startActivity(intent);
                    finish();  // Fecha a activity atual para não ficar empilhada
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String conteudoQR = result.getContents();
                Toast.makeText(this, "QR Code: " + conteudoQR, Toast.LENGTH_LONG).show();

                String nome = extrairCampo(conteudoQR, "Nome:");
                String cpf = extrairCampo(conteudoQR, "CPF:");
                String email = extrairCampo(conteudoQR, "Email:");
                String evento = extrairCampo(conteudoQR, "Evento:");

                if (cpf != null && evento != null) {
                    salvarPresencaComEntradaESaida(cpf, evento);
                } else {
                    Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String extrairCampo(String texto, String chave) {
        int inicio = texto.indexOf(chave);
        if (inicio != -1) {
            int fim = texto.indexOf("\n", inicio);
            if (fim == -1) fim = texto.length();
            return texto.substring(inicio + chave.length(), fim).trim();
        }
        return null;
    }

    private void salvarPresencaComEntradaESaida(String cpf, String eventoId) {
        DatabaseReference alunoRef = FirebaseDatabase.getInstance().getReference("alunos").child(cpf);
        alunoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot alunoSnapshot) {
                if (!alunoSnapshot.exists()) {
                    Toast.makeText(MenuAdministrador.this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                String nomeAluno = alunoSnapshot.child("nome").getValue(String.class);
                String emailAluno = alunoSnapshot.child("email").getValue(String.class);

                DatabaseReference presencaRef = FirebaseDatabase.getInstance()
                        .getReference("presencas")
                        .child(eventoId)
                        .child(cpf);

                presencaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot presencaSnapshot) {
                        long timestampAtual = System.currentTimeMillis();
                        String horaFormatada = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(timestampAtual));

                        if (!presencaSnapshot.exists()) {
                            Map<String, Object> presenca = new HashMap<>();
                            presenca.put("nome", nomeAluno);
                            presenca.put("email", emailAluno);
                            presenca.put("horaEntrada", horaFormatada);

                            presencaRef.setValue(presenca)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(MenuAdministrador.this, "Hora de entrada registrada!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(MenuAdministrador.this, "Erro ao registrar entrada", Toast.LENGTH_SHORT).show());

                        } else if (!presencaSnapshot.hasChild("horaEntrada")) {
                            presencaRef.child("horaEntrada").setValue(horaFormatada)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(MenuAdministrador.this, "Hora de entrada registrada!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(MenuAdministrador.this, "Erro ao registrar entrada", Toast.LENGTH_SHORT).show());

                        } else if (!presencaSnapshot.hasChild("horaSaida")) {
                            presencaRef.child("horaSaida").setValue(horaFormatada)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(MenuAdministrador.this, "Hora de saída registrada!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(MenuAdministrador.this, "Erro ao registrar saída", Toast.LENGTH_SHORT).show());

                        } else {
                            Toast.makeText(MenuAdministrador.this, "Presença já registrada com entrada e saída.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MenuAdministrador.this, "Erro ao verificar presença", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuAdministrador.this, "Erro ao verificar aluno", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
