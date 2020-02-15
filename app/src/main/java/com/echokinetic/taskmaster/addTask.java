package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class addTask extends AppCompatActivity {
    private TaskDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Bundle incoming_Bundle = getIntent().getBundleExtra("LIST_EXTRA");
        if(incoming_Bundle != null)
        {
            ArrayList<Task> taskList = incoming_Bundle.getParcelable("LIST");
        }

        this.database = Room.databaseBuilder(getApplicationContext(), TaskDB.class, getString(R.string.app_name)).allowMainThreadQueries().build();


        Button add_Task = findViewById(R.id.addNewTask);
        add_Task.setOnClickListener( (e)-> {

            EditText title = findViewById(R.id.add_Title);
            EditText body = findViewById(R.id.add_Body);

            Task task = new Task(title.getText().toString(), body.getText().toString());
            Intent intent = new Intent(this, MainActivity.class);
            //Bundle bundle = new Bundle();

            //bundle.putParcelable("TASK", task);
            //intent.putExtra("TASK_EXTRA", bundle);

            insertTask();

            startActivity(intent);
        });
    }

    public void insertTask()
    {

        EditText titleView = findViewById(R.id.add_Title);
        String title = titleView.getText().toString();

        EditText bodyView = findViewById(R.id.add_Body);
        String body = bodyView.getText().toString();


        this.databaseInject(title, body);
    }

    public void databaseInject(String title, String body)
    {
        Task task = new Task(title, body);
        database.taskDao().addTask(task);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView count_View = findViewById(R.id.item_Count);

        Intent intent = getIntent();
        int count = intent.getIntExtra("COUNT", 0);
        String count_String = "Total Tasks: " + Integer.toString(count);
        count_View.setText(count_String);

    }
}
