package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_Button = findViewById(R.id.newTask);
        add_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, addTask.class);
            startActivity(intent);
        });

        Button all_Button = findViewById(R.id.allTasks);
        all_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, allTasks.class);
            startActivity(intent);
        });
    }


}
