package com.example.controle_de_cadastro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menuusuario extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menuusuario);

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
}
