package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        Button left_Button = findViewById(R.id.left_Button);
        left_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, detailPage.class);
            intent.putExtra("taskTitle", left_Button.getText().toString());
            startActivity(intent);
        });

        Button middle_Button = findViewById(R.id.middle_Button);
        middle_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, detailPage.class);
            intent.putExtra("taskTitle", middle_Button.getText().toString());
            startActivity(intent);
        });

        Button right_Button = findViewById(R.id.right_Button);
        right_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, detailPage.class);
            intent.putExtra("taskTitle", right_Button.getText().toString());
            startActivity(intent);
        });

        ImageButton settings = findViewById(R.id.settingsButton);
        settings.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, settings.class);
           // intent.putExtra("taskTitle", right_Button.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        TextView title = findViewById(R.id.mainTitle);
        title.setText(pref.getString("user", "Test")+ " Task's");
    }
}
