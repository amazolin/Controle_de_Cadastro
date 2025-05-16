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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class menuusuario extends AppCompatActivity {

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


        // Corrigir possível NPE ao garantir que o ID existe no layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ListView e conteúdo de exemplo
        ListView listView = findViewById(R.id.listView);

        String[] eventos = {
                "Nome Evento: Fatec LOG\n\nHora Inscrição: 16/11/2023 15:31\n\nHora Entrada:\n\nHora Saída:"
        };

        // Adapter com layout personalizado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.activity_listview,     // layout com borda e cor
                R.id.listItemView,      // ID do TextView no layout
                eventos
        );

        listView.setAdapter(adapter);

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
