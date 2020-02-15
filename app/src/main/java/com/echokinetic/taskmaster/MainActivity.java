package com.echokinetic.taskmaster;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment.OnListFragmentInteractionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener
{

   private List<Task> taskList;
   private MyTaskRecyclerViewAdapter adapter;
   private RecyclerView recyclerView;
   private TaskDB database;
   private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = Room.databaseBuilder(getApplicationContext(), TaskDB.class, getString(R.string.app_name)).allowMainThreadQueries().build();
        //Adapter

        taskList = new ArrayList<>();


        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);


        adapter = new MyTaskRecyclerViewAdapter(this.taskList, this, this::onListFragmentInteraction);
        recyclerView.setAdapter(adapter);


        FloatingActionButton add_Button = findViewById(R.id.floating_Add);
        add_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, addTask.class);
            intent.putExtra("COUNT", adapter.getItemCount());
            //startActivity(intent);
            addTaskDialog();
        });

//        ClipData.Item all_Tasks = (ClipData.Item) findViewById(R.id.all_tasks_item);
//        all_Tasks.setOnClickListener( (e)-> {
//            Intent intent = new Intent(this, allTasks.class);
//            startActivity(intent);
//        });

//        Button left_Button = findViewById(R.id.left_Button);
//        left_Button.setOnClickListener( (e)-> {
//            Intent intent = new Intent(this, detailPage.class);
//            intent.putExtra("taskTitle", left_Button.getText().toString());
//            startActivity(intent);
//        });

//        Button middle_Button = findViewById(R.id.middle_Button);
//        middle_Button.setOnClickListener( (e)-> {
//            Intent intent = new Intent(this, detailPage.class);
//            intent.putExtra("taskTitle", middle_Button.getText().toString());
//            startActivity(intent);
//        });

//        Button drop_DB = findViewById(R.id.right_Button);
//        drop_DB.setOnClickListener( (e)-> {
//            //Intent intent = new Intent(this, detailPage.class);
//            //intent.putExtra("taskTitle", right_Button.getText().toString());
//            //startActivity(intent);
//            database.taskDao().delete();
//            taskList.clear();
//            this.reDrawRecyclerFromDB();
//        });

//        ImageButton settings = findViewById(R.id.settings_Button);
//        settings.setOnClickListener( (e)-> {
//            Intent intent = new Intent(this, settings.class);
//           // intent.putExtra("taskTitle", right_Button.getText().toString());
//            startActivity(intent);
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }


    private void reDrawRecyclerFromDB()
    {
        database = Room.databaseBuilder(getApplicationContext(), TaskDB.class, getString(R.string.app_name))
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        taskList.addAll(database.taskDao().getAll());
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();

       // Bundle incoming_Bundle = getIntent().getBundleExtra("TASK_EXTRA");

        //if(incoming_Bundle != null)
       // {
         //   Task task = incoming_Bundle.getParcelable("TASK");
         //   taskList.add(task);
          //  adapter.notifyItemInserted(0);
       // }
    }

    @Override
    protected void onResume() {
        super.onResume();

        taskList.clear();

        this.reDrawRecyclerFromDB();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //TextView title = findViewById(R.id.mainTitle);
        toolbar.setSubtitle(pref.getString("user", "")+ " Task's");
        //.setText(pref.getString("user", "Test")+ " Task's");
    }

    @Override
    public void onListFragmentInteraction(Task item) {

    }

    public void addTaskDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.add_dialog, null);

        TextView count = (TextView) view.findViewById(R.id.dialog_Count);
        String count_String = "Total Tasks: " + adapter.getItemCount();
        count.setText(count_String);

        builder.setView(view)


                // action buttons
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        insertTask(view);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove the dialog from the screen
                    }
                })
                .show();

    }

    public void insertTask(View view)
    {
        EditText dialogTitle = (EditText) view.findViewById(R.id.dialog_Title);
        String title = dialogTitle.getText().toString();

        EditText bodyView = (EditText) view.findViewById(R.id.dialog_Body);
        String body = bodyView.getText().toString();

        ChipGroup dialogGroup = (ChipGroup) view.findViewById(R.id.dialog_state);
        Chip chip = dialogGroup.findViewById(dialogGroup.getCheckedChipId());


        if(chip.getText().toString().equals("New"))
        {
            databaseInject(title, body, TaskState.NEW);
        }
        else if(chip.getText().toString().equals("Assigned"))
        {
            databaseInject(title, body, TaskState.ASSIGNED);
        }
        else if(chip.getText().toString().equals("In Progress"))
        {
            databaseInject(title, body, TaskState.IN_PROGRESS);
        }
        else if(chip.getText().toString().equals("Completed"))
        {
            System.out.println("CALLED");
            databaseInject(title, body, TaskState.COMPLETE);
        }
        taskList.clear();
        reDrawRecyclerFromDB();
    }

    public void databaseInject(String title, String body, TaskState state)
    {
        Task task = new Task(title, body, state);
        database.taskDao().addTask(task);
        adapter.notifyDataSetChanged();
    }

}
