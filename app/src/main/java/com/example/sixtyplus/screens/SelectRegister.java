package com.example.sixtyplus.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sixtyplus.R;

public class SelectRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnGoBackLogin2 = findViewById(R.id.goBackLoginSelect);
        btnGoBackLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectRegister.this, Landing.class);
                startActivity(intent);
            }
        });

        ImageButton imgStudent = findViewById(R.id.studentSelected);
        imgStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectRegister.this, RegisterStudents.class);
                startActivity(intent);
            }
        });

        Button btnStudentSelected = findViewById(R.id.studentSelectBtn);
        btnStudentSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectRegister.this, RegisterStudents.class);
                startActivity(intent);
            }
        });

        ImageButton imgInCharge = findViewById(R.id.inChargeSelected);
        imgInCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectRegister.this, RegisterInCharge.class);
                startActivity(intent);
            }
        });

        Button btnInChargeSelected = findViewById(R.id.inChargeSelectedBtn);
        btnInChargeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectRegister.this, RegisterInCharge.class);
                startActivity(intent);
            }
        });
    }
}