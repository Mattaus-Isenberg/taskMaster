package com.echokinetic.taskmaster;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment.OnListFragmentInteractionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener
{

   private List<Task> taskList;
   private MyTaskRecyclerViewAdapter adapter;
   private RecyclerView recyclerView;
   private TaskDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
            startActivity(intent);
        });

        Button all_Button = findViewById(R.id.allTasks);
        all_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, allTasks.class);
            startActivity(intent);
        });

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

        Button drop_DB = findViewById(R.id.right_Button);
        drop_DB.setOnClickListener( (e)-> {
            //Intent intent = new Intent(this, detailPage.class);
            //intent.putExtra("taskTitle", right_Button.getText().toString());
            //startActivity(intent);
            database.taskDao().delete();
            taskList.clear();
            this.reDrawRecyclerFromDB();
        });

        ImageButton settings = findViewById(R.id.settings_Button);
        settings.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, settings.class);
           // intent.putExtra("taskTitle", right_Button.getText().toString());
            startActivity(intent);
        });

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
        TextView title = findViewById(R.id.mainTitle);
        title.setText(pref.getString("user", "Test")+ " Task's");
    }

    @Override
    public void onListFragmentInteraction(Task item) {

    }

}
