package com.example.truefalseapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EndGamePage extends AppCompatActivity {

    TextView skor , txttrue, txtfalse;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_game_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        skor = findViewById(R.id.skottxt);
        txttrue = findViewById(R.id.txtTrue);
        txtfalse = findViewById(R.id.txtFalse);
        button = findViewById(R.id.button);

        String getSkor = getIntent().getStringExtra("score");
        String getTrueAnswer = getIntent().getStringExtra("answerTrue");
        String getFalseAnswer = getIntent().getStringExtra("answerFalse");

        skor.setText("Puan : "+getSkor);
        txttrue.setText("Doğru Cevap :" +getTrueAnswer );
        txtfalse.setText("Yanlış Cevap :" + getFalseAnswer);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EndGamePage.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}