package com.example.controle_de_cadastro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                buscarEventoDoAluno(cpfAluno, new EventoCallback() {
                    @Override
                    public void onEventoEncontrado(String eventoId) {
                        // Buscar os dados do aluno
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
                                        // Opcional: buscar nomeEvento para mostrar no QR (se quiser, pode criar método pra isso)
                                        // Buscar nome do evento usando o eventoId
                                        DatabaseReference eventoRef = FirebaseDatabase.getInstance()
                                                .getReference("eventos")
                                                .child(eventoId);

                                        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                                if (eventoSnapshot.exists()) {
                                                    String nomeEvento = eventoSnapshot.child("nome").getValue(String.class);

                                                    String conteudoQR = "Nome: " + nomeAluno +
                                                            "\nCPF: " + cpf +
                                                            "\nEmail: " + emailAluno +
                                                            "\nEvento: " + eventoId +
                                                            "\nNomeEvento: " + nomeEvento;

                                                    Intent intent = new Intent(menuusuario.this, QR_Code.class);
                                                    intent.putExtra("conteudoQR", conteudoQR);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(menuusuario.this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(menuusuario.this, "Erro ao buscar nome do evento", Toast.LENGTH_SHORT).show();
                                            }
                                        });

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
                    }

                    @Override
                    public void onEventoNaoEncontrado() {
                        Toast.makeText(menuusuario.this, "Aluno não está registrado em nenhum evento", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErro(Exception e) {
                        Toast.makeText(menuusuario.this, "Erro ao buscar evento do aluno", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }
            if (id == R.id.certificado) {
                buscarEventoDoAluno(cpfAluno, new EventoCallback() {
                    @Override
                    public void onEventoEncontrado(String eventoId) {
                        DatabaseReference presencaRef = FirebaseDatabase.getInstance()
                                .getReference("presencas")
                                .child(eventoId)
                                .child(cpfAluno);

                        presencaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String nome = snapshot.child("nome").getValue(String.class);
                                    String email = snapshot.child("email").getValue(String.class);
                                    String horaEntrada = snapshot.child("horaEntrada").getValue(String.class);
                                    String horaSaida = snapshot.child("horaSaida").getValue(String.class);

                                    // Buscar nome do evento
                                    DatabaseReference eventoRef = FirebaseDatabase.getInstance()
                                            .getReference("eventos")
                                            .child(eventoId);

                                    eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                            if (eventoSnapshot.exists()) {
                                                String nomeEvento = eventoSnapshot.child("nome").getValue(String.class);
                                                gerarCertificadoPDF(nome, email, nomeEvento, horaEntrada, horaSaida);
                                            } else {
                                                Toast.makeText(menuusuario.this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(menuusuario.this, "Erro ao buscar nome do evento", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(menuusuario.this, "Dados de presença não encontrados", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(menuusuario.this, "Erro ao acessar dados de presença", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onEventoNaoEncontrado() {
                        Toast.makeText(menuusuario.this, "Nenhum evento encontrado para o aluno", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErro(Exception e) {
                        Toast.makeText(menuusuario.this, "Erro ao buscar evento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void buscarEventoDoAluno(String cpfAluno, EventoCallback callback) {
        DatabaseReference presencasRef = FirebaseDatabase.getInstance().getReference("presencas");

        presencasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot eventoSnapshot : snapshot.getChildren()) {
                    if (eventoSnapshot.hasChild(cpfAluno)) {
                        String eventoId = eventoSnapshot.getKey();
                        callback.onEventoEncontrado(eventoId);
                        return;
                    }
                }
                callback.onEventoNaoEncontrado();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onErro(error.toException());
            }
        });
    }
    private void gerarCertificadoPDF(String nome, String email, String eventoId, String horaEntrada, String horaSaida) {
        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Configura título
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        titlePaint.setTextSize(14);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(10);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 400, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        int centerX = pageInfo.getPageWidth() / 2;
        int startX = 20;
        int y = 40;
        int lineSpacing = 18;

        // Título centralizado
        canvas.drawText("CERTIFICADO DE PARTICIPAÇÃO", centerX, y, titlePaint);
        y += 2 * lineSpacing;

        // Texto do certificado
        canvas.drawText("Certificamos que:", startX, y, paint);
        y += lineSpacing;

        canvas.drawText("Nome: " + nome, startX, y, paint);
        y += lineSpacing;

        canvas.drawText("Email: " + email, startX, y, paint);
        y += lineSpacing;

        canvas.drawText("Participou do evento: " + eventoId, startX, y, paint);
        y += lineSpacing;

        canvas.drawText("Entrada: " + horaEntrada, startX, y, paint);
        y += lineSpacing;

        canvas.drawText("Saída: " + horaSaida, startX, y, paint);
        y += lineSpacing;

        String dataHoraEmissao = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Certificado emitido em:", startX, y, paint);
        y += lineSpacing;

        canvas.drawText(dataHoraEmissao, startX, y, paint);
        y += 2 * lineSpacing;

        // Linha de assinatura
        canvas.drawLine(startX, y, pageInfo.getPageWidth() - startX, y, paint);
        y += lineSpacing;
        canvas.drawText("Assinatura/Organização", startX, y, paint);

        // LOGO NO RODAPÉ (centralizada na parte inferior)
        Bitmap rodapeLogo = BitmapFactory.decodeResource(getResources(), R.drawable.infinit); // Substitua por sua logo
        int logoWidth = 60;
        int logoHeight = 60;
        Bitmap scaledRodapeLogo = Bitmap.createScaledBitmap(rodapeLogo, logoWidth, logoHeight, false);

        int logoX = (pageInfo.getPageWidth() - logoWidth) / 2;
        int logoY = pageInfo.getPageHeight() - logoHeight - 10; // 10px da borda inferior

        canvas.drawBitmap(scaledRodapeLogo, logoX, logoY, paint);

        pdfDocument.finishPage(page);

        // Salvar o PDF
        try {
            File file = new File(getExternalFilesDir(null), "certificado_" + nome.replaceAll("\\s+", "_") + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(this, "Certificado gerado em: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    file), "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (IOException e) {
            Toast.makeText(this, "Erro ao gerar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}
