package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class addTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button add_Task = findViewById(R.id.addNewTask);
        add_Task.setOnClickListener( (e)-> {
            TextView item = findViewById(R.id.label);
            item.setText("Submitted!");
        });
    }
}
