package com.echokinetic.taskmaster;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));


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
        add_Button.setTranslationZ(5);
        add_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, addTask.class);
            intent.putExtra("COUNT", adapter.getItemCount());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.all_tasks_item:
               startActivity(new Intent(this, allTasks.class));
               return true;
            case R.id.settings_item:
                startActivity(new Intent(this, settings.class));
                return true;
            case R.id.about_item:
                aboutDialog();
                return true;
        }
        return false;
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
    public void onListFragmentInteraction(Task item)
    {
        taskDetailDialog(item);
    }





    public void addTaskDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.add_dialog, null);

        TextView count = (TextView) view.findViewById(R.id.dialog_Count);
        String count_String = "Total Tasks: " + adapter.getItemCount();
        count.setText(count_String);

       AlertDialog dialog = builder.setView(view)


                // action buttons
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        insertTask(view);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove the dialog from the screen
                    }
                }).create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
                dialog.show();

    }

    public void aboutDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.about_dialog, null);

       AlertDialog dialog = builder.setView(view)

                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove the dialog from the screen
                    }
                }).create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();


    }










    public void taskDetailDialog(Task item){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.detail_dialog, null);

        EditText title = (EditText) view.findViewById(R.id.detail_Title);
        title.setText(item.getTitle());

        EditText body = (EditText) view.findViewById(R.id.detail_Body);
        body.setText(item.getBody());

        ChipGroup detailChipGroup = (ChipGroup) view.findViewById(R.id.detail_Group_state);
        detailChipGroup.setSingleSelection(true);

        detailChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    if (chip.getText().toString().equals("New")) {
                        System.out.println("TEST");
                        item.setState(TaskState.NEW);
                        database.taskDao().updateTask(item);
                    } else if (chip.getText().toString().equals("Assigned")) {
                        item.setState(TaskState.ASSIGNED);
                        database.taskDao().updateTask(item);
                    } else if (chip.getText().toString().equals("In Progress")) {
                        item.setState(TaskState.IN_PROGRESS);
                        database.taskDao().updateTask(item);
                    } else if (chip.getText().toString().equals("Completed")) {
                        item.setState(TaskState.COMPLETE);
                        database.taskDao().updateTask(item);
                    } else if (chip.getText().toString().equals("High Priority")) {
                        item.setState(TaskState.HIGH_PRIORITY);
                        database.taskDao().updateTask(item);
                    }
                    Toast.makeText(getApplicationContext(), "Current state is: " + chip.getChipText(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(item.getState().equals(TaskState.NEW))
        {
            Chip chip = (Chip)view.findViewById(R.id.detail_State_New);
            chip.setChecked(true);
        }
        else if(item.getState().equals(TaskState.ASSIGNED))
        {
            Chip chip = (Chip)view.findViewById(R.id.detail_State_Assigned);
            chip.setChecked(true);
        }
        else if(item.getState().equals(TaskState.IN_PROGRESS))
        {
            Chip chip = (Chip)view.findViewById(R.id.detail_State_INProgress);
            chip.setChecked(true);
        }
        else if(item.getState().equals(TaskState.COMPLETE))
        {
            Chip chip = (Chip)view.findViewById(R.id.detail_State_Complete);
            chip.setChecked(true);
        }
        else if(item.getState().equals(TaskState.HIGH_PRIORITY))
        {
            Chip chip = (Chip)view.findViewById(R.id.detail_High_Priority);
            chip.setChecked(true);
        }


       AlertDialog dialog =  builder.setView(view)
                // action buttons
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText title_Field = (EditText) view.findViewById(R.id.detail_Title);
                        EditText body_Field = (EditText) view.findViewById(R.id.detail_Body);
                        item.setTitle(title_Field.getText().toString());
                        item.setBody(body_Field.getText().toString());
                        database.taskDao().updateTask(item);
                        taskList.clear();
                        reDrawRecyclerFromDB();
                    }
                })
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        database.taskDao().deleteTask(item);
                        taskList.clear();
                        reDrawRecyclerFromDB();
                    }
                })
//                .setNegativeButton("UPDATE", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        EditText title_Field = (EditText) view.findViewById(R.id.detail_Title);
//                        EditText body_Field = (EditText) view.findViewById(R.id.detail_Body);
//                        item.setTitle(title_Field.getText().toString());
//                        item.setBody(body_Field.getText().toString());
//                        database.taskDao().updateTask(item);
//                        taskList.clear();
//                        reDrawRecyclerFromDB();
//                    }
//                })
               .create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();


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
            databaseInject(title, body, TaskState.COMPLETE);
        }
        else if(chip.getText().toString().equals("High Priority"))
        {
            databaseInject(title, body, TaskState.HIGH_PRIORITY);
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
