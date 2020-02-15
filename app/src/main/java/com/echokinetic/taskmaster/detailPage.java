package com.echokinetic.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class detailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TaskDB database = Room.databaseBuilder(getApplicationContext(), TaskDB.class, getString(R.string.app_name)).allowMainThreadQueries().build();

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        //TextView state = findViewById(R.id.state);
        String titleTask = getIntent().getStringExtra("title");
        String bodyTask = getIntent().getStringExtra("description");
        String stateTask = getIntent().getStringExtra("state");
        long id = getIntent().getLongExtra("ID", 0);

        ChipGroup state_Chip = findViewById(R.id.state_Change);
        state_Chip.setSingleSelection(true);
        if(stateTask.equals("NEW"))
        {
            Chip chip = findViewById(R.id.state_New);
            chip.setChecked(true);
        }
        else if(stateTask.equals("ASSIGNED"))
        {
            Chip chip = findViewById(R.id.state_Assigned);
            chip.setChecked(true);
        }
        else if(stateTask.equals("IN_PROGRESS"))
        {
            Chip chip = findViewById(R.id.state_INProgress);
            chip.setChecked(true);
        }
        else if(stateTask.equals("COMPLETE"))
        {
            Chip chip = findViewById(R.id.state_Complete);
            chip.setChecked(true);
        }

        title.setText(titleTask);
        description.setText(bodyTask);
        //state.setText(stateTask);


        state_Chip.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null)

                    if(chip.getText().toString().equals("NEW"))
                    {
                        Task task = database.taskDao().getTaskById(id);
                        task.setState(TaskState.NEW);
                        database.taskDao().updateTask(task);

                    }
                    else if(chip.getText().toString().equals("Assigned"))
                    {
                        Task task = database.taskDao().getTaskById(id);
                        task.setState(TaskState.ASSIGNED);
                        database.taskDao().updateTask(task);
                    }
                    else if(chip.getText().toString().equals("In Progress"))
                    {
                        Task task = database.taskDao().getTaskById(id);
                        task.setState(TaskState.IN_PROGRESS);
                        database.taskDao().updateTask(task);
                    }
                    else if(chip.getText().toString().equals("Completed"))
                    {
                        Task task = database.taskDao().getTaskById(id);
                        task.setState(TaskState.COMPLETE);
                        database.taskDao().updateTask(task);
                    }

                    Toast.makeText(getApplicationContext(), "Chip is " + chip.getChipText(), Toast.LENGTH_SHORT).show();


            }
        });

//        Chip chip = findViewById(R.id.state_New);
//        chip.setOnCloseIconClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Close is Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
