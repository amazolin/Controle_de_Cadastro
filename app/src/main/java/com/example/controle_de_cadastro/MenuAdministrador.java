package com.example.controle_de_cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
                    // lógica para abrir lista
                    return true;

                } else if (id == R.id.sair) {
                    // lógica para sair
                    return true;
                }

                return false;
            }
        });
    }

    // Trata o resultado da leitura do QR Code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Toast.makeText(this, "QR Code: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Aqui você pode iniciar outra Activity ou salvar o conteúdo
            } else {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
