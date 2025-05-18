package com.example.controle_de_cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnCadastrar;
    EditText txtUsername, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String senha = txtPassword.getText().toString();

                if (username.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlunoDAO alunoDAO = new AlunoDAO();

                alunoDAO.buscarPorUsernameESenha(username, senha, new AlunoCallback() {
                    @Override
                    public void onAlunoEncontrado(Aluno aluno) {
                        Log.d("ALUNO_ENCONTRADO", "Aluno encontrado: " + aluno.getNome());
                        String tipoUsuario = aluno.getTipoUsuario();
                        if (tipoUsuario.equals("usuario")) {
                            try {
                                Intent it = new Intent(getApplicationContext(), menuusuario.class);
                                startActivity(it);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Erro ao abrir a tela: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (tipoUsuario.equals("administrador")) {
                            try {
                                Intent it = new Intent(getApplicationContext(), MenuAdministrador.class);
                                startActivity(it);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Erro ao abrir a tela: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onAlunoNaoEncontrado() {
                        Toast.makeText(MainActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErro(Exception e) {
                        Toast.makeText(MainActivity.this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), Cadastro.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
