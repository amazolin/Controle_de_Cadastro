package com.example.controle_de_cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Cadastro extends AppCompatActivity {

    Button btnCadastrar;
    TextView txtNome, txtEmail, txtCpf, txtPassword, txtConfirmPassword;
    RadioGroup radioTipo;
    RadioButton rdbtUsuario, rdbtAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializando os campos
        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
        txtCpf = findViewById(R.id.txtCpf);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        rdbtUsuario = findViewById(R.id.rdbtUsuario);
        rdbtAdmin = findViewById(R.id.rdbtAdmin);
        radioTipo = findViewById(R.id.radioTipo);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = txtNome.getText().toString();
                String email = txtEmail.getText().toString();
                String cpf = txtCpf.getText().toString();
                String senha = txtPassword.getText().toString();
                String confirmarSenha = txtConfirmPassword.getText().toString();

                // Verificar qual radio está selecionado
                String tipoUsuario = "";
                if (rdbtUsuario.isChecked()) {
                    tipoUsuario = "usuario";
                } else if (rdbtAdmin.isChecked()) {
                    tipoUsuario = "administrador";
                }

                if (!senha.equals(confirmarSenha)) {
                    Toast.makeText(Cadastro.this, "Senhas não coincidem", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(Cadastro.this, "Cadastro feito com sucesso!", Toast.LENGTH_SHORT).show();
                }

                // Salvar senha como texto simples (sem hash)
                Aluno aluno = new Aluno(nome, email, cpf, senha, tipoUsuario);
                AlunoDAO alunoDAO = new AlunoDAO();
                alunoDAO.salvarAluno(aluno);

                Intent in = new Intent(Cadastro.this, MainActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
