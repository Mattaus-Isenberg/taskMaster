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
        TextView description = findViewById(R.id.description);
        TextView state = findViewById(R.id.state);
        String titleTask = getIntent().getStringExtra("title");
        String bodyTask = getIntent().getStringExtra("description");
        String stateTask = getIntent().getStringExtra("state");

        title.setText(titleTask);
        description.setText(bodyTask);
        state.setText(stateTask);

    }
}
