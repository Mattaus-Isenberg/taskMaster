package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class detailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView title = findViewById(R.id.title);
        String titleTask = getIntent().getStringExtra("taskTitle");
        title.setText(titleTask);
    }
}
