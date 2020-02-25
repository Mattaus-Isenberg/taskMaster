package com.echokinetic.taskmaster;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.DeleteTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.UpdateTaskMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import type.DeleteTaskInput;
import type.UpdateTaskInput;

public class allTasks extends AppCompatActivity implements TaskFragment.OnListFragmentInteractionListener {

    MyTaskRecyclerViewAdapter adapter;
    List<Task> tasks_List;
    private AWSAppSyncClient mAWSAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));


        tasks_List = new ArrayList<>();

        //query DDB
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        //MyTaskRecyclerViewAdapter.OnListFragmentInteractionListener listener = new MyTaskRecyclerViewAdapter.OnListFragmentInteractionListener;
        RecyclerView recycler = findViewById(R.id.allrecycler);
        adapter = new MyTaskRecyclerViewAdapter(tasks_List, getApplicationContext(), this::onListFragmentInteraction);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        this.runQuery();
    }

    @Override
    public void onListFragmentInteraction(Task item)
    {
        taskDetailDialog(item);
    }

    public void taskDetailDialog(Task item){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

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
//---------------------------------------------correct
                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    if (chip.getText().toString().equals("New")) {
                        item.setState(TaskState.NEW);
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                    } else if (chip.getText().toString().equals("Assigned")) {
                        item.setState(TaskState.ASSIGNED);
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                    } else if (chip.getText().toString().equals("In Progress")) {
                        item.setState(TaskState.IN_PROGRESS);
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                    } else if (chip.getText().toString().equals("Completed")) {
                        item.setState(TaskState.COMPLETE);
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                    } else if (chip.getText().toString().equals("High Priority")) {
                        item.setState(TaskState.HIGH_PRIORITY);
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                        adapter.notifyDataSetChanged();
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
                        //database.taskDao().updateTask(item);
                        runUPDATEMutation(item);
                        tasks_List.clear();
                        runQuery();
                        // reDrawRecyclerFromDB();
                    }
                })
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //database.taskDao().deleteTask(item);
                        runDELETEMutation(item);
                        tasks_List.clear();
                        runQuery();
                        // reDrawRecyclerFromDB();
                    }
                })
                .create();

        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 0, 0));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 0, 0));
            }
        });

        dialog.show();

    }

    public void runQuery()
    {
        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(tasksCallback);
    }

    private GraphQLCall.Callback<ListTasksQuery.Data> tasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response)
        {
            //Log.i("Results", response.data().listTasks().items().toString());
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tasks_List.clear();
                    List<ListTasksQuery.Item> items = response.data().listTasks().items();
                    tasks_List.clear();
                    for(ListTasksQuery.Item item : items)
                    {
                        tasks_List.add(new Task(item));
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e)
        {
            Log.e("ERROR", e.toString());
            // Toast.makeText(getApplicationContext(), "DynamoDB Query ERROR", Toast.LENGTH_SHORT).show();
        }
    };





    public void runUPDATEMutation(Task task){
        Long id = task.getId();

        UpdateTaskInput updateTaskInput = UpdateTaskInput.builder()
                .id(task.getDynamoDbId())
                .title(task.getTitle())
                .body(task.getBody())
                .state(type.TaskState.valueOf(task.getState().toString()))
                .dueDate(task.getDueDate()).build();


        mAWSAppSyncClient.mutate(UpdateTaskMutation.builder().input(updateTaskInput).build())
                .enqueue(updateMutationCallback);
    }

    private GraphQLCall.Callback<UpdateTaskMutation.Data> updateMutationCallback = new GraphQLCall.Callback<UpdateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<UpdateTaskMutation.Data> response)
        {
            Log.i("Results", "Added Todo");
            // reDrawRecyclerFromDB();
            runQuery();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };

    public void runDELETEMutation(Task task){
        Long id = task.getId();

        DeleteTaskInput deleteTaskInput = DeleteTaskInput.builder().id(task.getDynamoDbId()).build();


        mAWSAppSyncClient.mutate(DeleteTaskMutation.builder().input(deleteTaskInput).build())
                .enqueue(deleteMutationCallback);
    }

    private GraphQLCall.Callback<DeleteTaskMutation.Data> deleteMutationCallback = new GraphQLCall.Callback<DeleteTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<DeleteTaskMutation.Data> response)
        {
            Log.i("Results", "Added Todo");
            // reDrawRecyclerFromDB();
            runQuery();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };
}
