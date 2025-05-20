package com.example.controle_de_cadastro;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QR_Code extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ImageView qrCodeImageView = findViewById(R.id.imgQRCode);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        // Recebe o conteúdo passado pela Intent
        String conteudo = getIntent().getStringExtra("conteudoQR");

        if (conteudo == null || conteudo.isEmpty()) {
            Toast.makeText(this, "Conteúdo do QR Code está vazio.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(conteudo, BarcodeFormat.QR_CODE, 600, 600);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }

        btnVoltar.setOnClickListener(v -> finish());
    }
}
