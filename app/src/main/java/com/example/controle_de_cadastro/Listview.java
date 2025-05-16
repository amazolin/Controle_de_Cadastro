package com.example.controle_de_cadastro; // ajuste para o seu pacote

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Listview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview); // Use o novo layout que contém a ListView

        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4"}; // Se os dados vêm de outra fonte, ajuste aqui

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.activity_listview, // Layout para CADA ITEM (com o texto branco)
                items
        ) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE); // Define a cor do texto para branco
                return textView;
            }
        };

    }
}