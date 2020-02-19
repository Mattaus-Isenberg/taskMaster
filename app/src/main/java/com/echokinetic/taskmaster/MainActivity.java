package com.echokinetic.taskmaster;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import android.app.Notification;
import android.app.PendingIntent;
//import android.app.RemoteInput;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment.OnListFragmentInteractionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener
{

   private List<Task> taskList;
   private static MyTaskRecyclerViewAdapter adapter;
   private RecyclerView recyclerView;
   private static TaskDB database;
   private Toolbar toolbar;


    public static String KEY_REPLY = "key_reply";
    public static final int NOTIFICATION_ID = 1;
    private int NOTIFY_ME_ID = 1337;
    public static String CHANNEL_ID = "My Channel";
    private  String CHANNEL_NAME = "Task Master";
    private String CHANNEL_DESCRIPTION = "Persistent ADD notification";

//insert at index o update adapter to inserted at 0 as well as layoutmanager scroll to position//

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //createNotificationChannel();
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



        //DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
            //    layoutManager.getOrientation());
        //recyclerView.addItemDecoration(mDividerItemDecoration);


        adapter = new MyTaskRecyclerViewAdapter(this.taskList, this, this::onListFragmentInteraction);
        recyclerView.setAdapter(adapter);


        ExtendedFloatingActionButton add_Button = findViewById(R.id.floating_Add);
        add_Button.setTranslationZ(15);
        add_Button.setOnClickListener( (e)-> {
            Intent intent = new Intent(this, addTask.class);
            intent.putExtra("COUNT", adapter.getItemCount());
            addTaskDialog();
            //showNotification(this, "ADD NEW TASK", " " );
        });

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


//--------------------------------------------------ON RESUME--------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();

        taskList.clear();

        this.reDrawRecyclerFromDB();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        toolbar.setSubtitle(pref.getString("user", "")+ " Task's");
    }


//=========================================================NOTIFICATIONS==============================================
//====================================================================================================================

//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = channel_Name;
//            String description = channel_Description;
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


//--------------------------------------------REMOTE INPUT------------------------------------------
    @Override
    protected void onNewIntent(Intent intent)
    {
         super.onNewIntent(intent);
    }


    public void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(mChannel);
            mChannel.setShowBadge(false);
            mChannel.enableVibration(false);
        }

        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.add_dialog);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
               // .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle("ADD NEW TASK");
                //.setContent(notificationLayoutExpanded);
                //.setCustomBigContentView(notificationLayoutExpanded);



        String replyLabel = "Task Title";

        //Initialise RemoteInput
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();



        int randomRequestCode = new Random().nextInt(54325);

        //PendingIntent that restarts the current activity instance.
        Intent resultIntent = new Intent(this, NotificationHandler.class);
        //Set a unique request code for this pending intent
        //PendingIntent resultPendingIntent = PendingIntent.getActivity(this, randomRequestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        PendingIntent resultPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        randomRequestCode,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification Action with RemoteInput instance added.
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                android.R.drawable.sym_action_chat, "NEW TASK", resultPendingIntent)
                .addRemoteInput(remoteInput)
               // .setAllowGeneratedReplies(true)
                .build();

        //Notification.Action instance added to Notification Builder.
        mBuilder.addAction(replyAction);

        Intent Launch = new Intent(this, MainActivity.class);
        Launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, Launch, 0);

        mBuilder.setContentIntent(pendingIntent);




//        Intent intentInternal = new Intent(this, MainActivity.class);
//        intentInternal.putExtra("notificationId", NOTIFICATION_ID);
//        intentInternal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intentInternal, PendingIntent.FLAG_CANCEL_CURRENT);
//        mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);

        mBuilder.setOngoing(true);
        mBuilder.build().flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        //mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


//=========================================================DIALOGS====================================================
//====================================================================================================================

    @Override
    public void onListFragmentInteraction(Task item)
    {
        taskDetailDialog(item);
    }


//-------------------------------------------------------ADD DIALOG----------------------------------------------------

    public void addTaskDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 0, 0));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 0, 0));
            }
        });
                dialog.show();
    }


//------------------------------------------------ABOUT DIALOG--------------------------------------------------


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


//---------------------------------------------TASK DETAIL DIALOG----------------------------------------------------


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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 0, 0));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(0, 0, 0));
            }
        });

        dialog.show();

    }



//=========================================================TASK INSERTION=============================================
//====================================================================================================================



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

    public static void databaseInject(String title, String body, TaskState state)
    {
        Task task = new Task(title, body, state);
        database.taskDao().addTask(task);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        showNotification(this, "New Task", " ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
