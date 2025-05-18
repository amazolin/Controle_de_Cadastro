package com.example.controle_de_cadastro;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlunoDAO {

    private final DatabaseReference databaseReference;

    public AlunoDAO() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("alunos");
    }

    // Salva aluno com a senha em texto plano
    public void salvarAluno(Aluno aluno) {
        String alunoId = aluno.getCpf();  // Usa CPF como chave única

        databaseReference.child(alunoId).setValue(aluno)
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Aluno salvo com sucesso"))
                .addOnFailureListener(e -> Log.e("FIREBASE", "Erro ao salvar aluno", e));
    }

    // Busca aluno com nome e senha em texto plano
    public void buscarPorUsernameESenha(String username, String senha, AlunoCallback callback) {
        databaseReference.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Aluno aluno = dataSnapshot.getValue(Aluno.class);
                if (aluno != null &&
                        aluno.getNome().trim().equalsIgnoreCase(username.trim()) &&
                        aluno.getPassword().equals(senha)) {

                    callback.onAlunoEncontrado(aluno);
                    return;
                }
            }
            callback.onAlunoNaoEncontrado();
        }).addOnFailureListener(e -> {
            Log.e("FIREBASE_ERROR", "Erro ao acessar o Firebase", e);
            callback.onErro(e);
        });
    }
}
