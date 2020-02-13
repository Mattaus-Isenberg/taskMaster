package com.echokinetic.taskmaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment.OnListFragmentInteractionListener;
import com.echokinetic.taskmaster.dummy.TaskFragment;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Adapter
        Task taskOne = new Task("Code Challenge 27", "Complete code challenge, im+plement java code, create blog", "new");
        Task taskTwo = new Task("Gym", "Back and Triceps, do cardio, add 30 minutes of after rowing", "assigned");
        Task taskThree = new Task("Sleep", "Try to sleep enough, eat well, remain fit", "inProgress");
        Task taskFour = new Task("Lab", "Complete lab and implement recycler view", "Complete");
        List<Task> taskList = new LinkedList<>();

        taskList.add(taskOne);
        taskList.add(taskTwo);
        taskList.add(taskThree);
        taskList.add(taskFour);

        TaskFragment.OnListFragmentInteractionListener mListener;

        MyTaskRecyclerViewAdapter adapter = new MyTaskRecyclerViewAdapter(taskList, this, null);
        recyclerView.setAdapter(adapter);


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
