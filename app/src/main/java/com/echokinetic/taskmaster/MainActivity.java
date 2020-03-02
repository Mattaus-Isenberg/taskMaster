package com.echokinetic.taskmaster;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.DeleteTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.UpdateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.ResultListener;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.amplifyframework.storage.result.StorageDownloadFileResult;
import com.amplifyframework.storage.result.StorageListResult;
import com.amplifyframework.storage.result.StorageRemoveResult;
import com.amplifyframework.storage.result.StorageUploadFileResult;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.amplifyframework.storage.s3.utils.S3RequestUtils;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import android.app.Notification;
import android.app.PendingIntent;
//import android.app.RemoteInput;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.echokinetic.taskmaster.dummy.MyTaskRecyclerViewAdapter;
import com.echokinetic.taskmaster.dummy.TaskFragment.OnListFragmentInteractionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import javax.annotation.Nonnull;

import type.CreateTaskInput;
import type.DeleteTaskInput;
import type.UpdateTaskInput;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.graphics.BitmapFactory.decodeFile;


public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener
{
    private class FetchAWSResources extends AsyncTask <Task, String, URL>
    {
        @Override
        protected URL doInBackground(Task... tasks)
        {
            StorageDownloadFileOptions options = StorageDownloadFileOptions.defaultInstance(); // This is your download file options
            String identityId = AWSMobileClient.getInstance().getIdentityId();
            String serviceKey = S3RequestUtils.getServiceKey(
                    StorageAccessLevel.PUBLIC,
                    identityId,
                    tasks[0].getUnique_File_ID(),
                    options.getTargetIdentityId()
            );

            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            URL url = clientHatch.generatePresignedUrl(
                    "taskmaster18253efc36744d35bf7d990433aea683181109-dev",
                    serviceKey,
                    new Date(today.getTime() + (1000 * 60 * 60 * 24)));
            return url;
        }

        @Override
        protected void onPostExecute(URL url)
        {
            super.onPostExecute(url);
            Picasso.get().load(url.toString()).into(preview);
            preview.setVisibility(View.VISIBLE);
        }
    }


    private List<Task> taskList;
    List<Task> fullList;
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
    String ImageDecode;
    ImageView imageViewLoad;
    String unique_File_ID = " ";
    AmazonS3Client clientHatch;
    ImageView preview;
    private static PinpointManager pinpointManager;

    private AWSAppSyncClient mAWSAppSyncClient;
    private static final int PICK_IMG_FILE = 1;
    private static final int IMG_RESULT = 1;
    private static final int READ_STORAGE_PERMISSION = 100;
    private static final int WRITE_STORAGE_PERMISSION = 200;



    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    //Log.i("INIT", userStateDetails.getUserState());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
                    {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                               // Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            final String token = task.getResult().getToken();
                           // Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }

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
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails)
                    {

                        try {
                            Amplify.addPlugin(new AWSS3StoragePlugin());
                            Amplify.configure(getApplicationContext());
                            clientHatch = ((AWSS3StoragePlugin) Amplify.Storage.getPlugin("awsS3StoragePlugin")).getEscapeHatch();
                            Log.i("StorageQuickStart", clientHatch.getRegionName());
                        } catch (AmplifyException e)
                        {
                            e.printStackTrace();
                        }
                        Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                        switch (userStateDetails.getUserState()){
                            case SIGNED_IN:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toolbar.setSubtitle(AWSMobileClient.getInstance().getUsername()+ " Task's");
                                    }
                                });
                                break;
                            case SIGNED_OUT:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toolbar.setSubtitle("Logged Out");
                                    }
                                });
                                break;
                            default:
                                AWSMobileClient.getInstance().signOut();
                                break;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("INIT", "Initialization error.", e);
                    }
                }
        );

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

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


        getPinpointManager(getApplicationContext());

//        OnCreateTaskSubscription subscription = OnCreateTaskSubscription.builder().build();
//        awsAppSyncClient.subscribe(subscription).execute(new AppSyncSubscriptionCall.Callback<OnCreateTaskSubscription.Data>() {
//            private static final String TAG = "jtb.Subscription";
//            // AWS calls this method when a new Task is created
//            @Override
//            public void onResponse(@Nonnull com.apollographql.apollo.api.Response<OnCreateTaskSubscription.Data> response) {
//                //TODO: Verify constructor usage, esp. TaskState
//                Task task = new Task(response.data().onCreateTask().title(), response.data().onCreateTask().description(), TaskState.valueOf(response.data().onCreateTask().taskState().toString()));
//                MainActivity.this.taskItemAdapter.addItem(task);
//            }
//
//            @Override
//            public void onFailure(@Nonnull ApolloException e) {
//                Log.e(TAG, "Failure in subscription callback: " + e.getMessage());
//            }
//
//            // Called after successful subscription attempt
//            @Override
//            public void onCompleted() {
//                Log.i(TAG, "Subscription successful.");
//            }
//        });


        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 1);

        // Calls the Get All Tasks query - returns a list
        this.runQuery();


        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case SIGNED_OUT:
                        // user clicked signout button and signedout
                        Log.i("userState", "user signed out");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again.");
                        try {
                            AWSMobileClient.getInstance().showSignIn(MainActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Alternatively call .showSignIn()
                        break;
                    default:
                        Log.i("userState", "unsupported");
                }
            }
        });


        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener()
        {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case GUEST:
                        Log.i("userState", "user is in guest mode");
                        break;
                    case SIGNED_OUT:
                        Log.i("userState", "user is signed out");
                        break;
                    case SIGNED_IN:
                        Log.i("userState", "user is signed in");
                        Log.i("IDENTITY ID", AWSMobileClient.getInstance().getIdentityId());
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again");
                        break;
                    case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                        Log.i("userState", "user logged in via federation, but currently needs new tokens");
                        break;
                    default:
                        Log.e("userState", "unsupported");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        Switch star_Switch = (Switch)menu.findItem(R.id.priority_Item)
                .getActionView().findViewById(R.id.prioritySwitch);
        star_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    filterPriority();
                }
                else
                    {
                        taskList.clear();
                    //taskList.addAll(database.taskDao().getAll());
                        taskList.addAll(fullList);
                        adapter.notifyDataSetChanged();
                    }
            }
        });
        return true;
    }

    

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
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
            case R.id.authentication:
                authenticate();
                return true;
            case R.id.logout:
                AWSMobileClient.getInstance().signOut();
                toolbar.setSubtitle("Logged Out");
                return true;
            case R.id.priority_Item:
                filterPriority();
        }
        return false;
    }

    public void filterPriority()
    {
        List<Task> starList = new ArrayList<>();
        fullList = new ArrayList<>();
        fullList.addAll(taskList);
        for(Task task : taskList)
        {
            if(task.getState().getStatus_Code() == TaskState.HIGH_PRIORITY.getStatus_Code())
            {
                starList.add(task);
            }
        }

        taskList.clear();
        taskList.addAll(starList);

        //taskList.addAll(database.taskDao().getTasksByPriority(4));

        adapter.notifyDataSetChanged();
    }

    private void reDrawRecyclerFromDB()
    {
//        database = Room.databaseBuilder(getApplicationContext(), TaskDB.class, getString(R.string.app_name))
//                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();
//        taskList.addAll(database.taskDao().getAll());
//        adapter.notifyDataSetChanged();
        runQuery();
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
                    taskList.clear();
                    List<ListTasksQuery.Item> items = response.data().listTasks().items();
                    taskList.clear();
                    for(ListTasksQuery.Item item : items)
                    {
                        taskList.add(new Task(item));
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



    @Override
    protected void onStart()
    {
        super.onStart();
    }


    public void authenticate()
    {
        AWSMobileClient.getInstance().showSignIn(
                this,
                SignInUIOptions.builder()
                        .nextActivity(MainActivity.class)
                        .logo(R.mipmap.ic_launcher)
                        .backgroundColor(R.color.black)
                        .canCancel(true)
                        .build(),
                new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        //Log.d(TAG, "onResult: " + result.getUserState());
                        switch (result.getUserState()){
                            case SIGNED_IN:
                                Log.i("INIT", "logged in!");
                                break;
                            case SIGNED_OUT:
                                //Log.i(TAG, "onResult: User did not choose to sign-in");
                                break;
                            default:
                                AWSMobileClient.getInstance().signOut();
                                break;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        //Log.e(TAG, "onError: ", e);
                    }
                }
        );
    }

//--------------------------------------------------ON RESUME--------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        //toolbar.setSubtitle(AWSMobileClient.getInstance().getUsername()+ " Task's");
        taskList.clear();
        runQuery();

        //reDrawRecyclerFromDB();

        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // toolbar.setSubtitle(pref.getString("user", "")+ " Task's");
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
        invalidateOptionsMenu();
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
               .setNeutralButton("ATTACH", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {

                       openDir();
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
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.rgb(0, 0, 0));

                if(unique_File_ID.length() > 5)
                {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setText("FILE ATTACHED");
                }
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

        preview = (ImageView) view.findViewById(R.id.previewimg);

        if(item.getUnique_File_ID() == null)
        {
            if(preview != null)
            {
                preview.setVisibility(View.GONE);
            }
        }
        else if(item.getUnique_File_ID().equals(" "))
        {
            if(preview != null)
            {
                preview.setVisibility(View.GONE);
            }
        }
        else if(item.getUnique_File_ID().length() > 5)
        {
            Log.i("HAS ID", "HAS ID");


            Task[] array = {item};

            FetchAWSResources fetch = new FetchAWSResources();
            fetch.execute(array);
        }

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
                        taskList.clear();
                        runQuery();
                       // reDrawRecyclerFromDB();
                    }
                })
                .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //database.taskDao().deleteTask(item);
                        runDELETEMutation(item);
                        taskList.clear();
                        runQuery();
                       // reDrawRecyclerFromDB();
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
            databaseInject(title, body, TaskState.NEW, 0, unique_File_ID);
        }
        else if(chip.getText().toString().equals("Assigned"))
        {
            databaseInject(title, body, TaskState.ASSIGNED, 0, unique_File_ID);
        }
        else if(chip.getText().toString().equals("In Progress"))
        {
            databaseInject(title, body, TaskState.IN_PROGRESS, 0, unique_File_ID);
        }
        else if(chip.getText().toString().equals("Completed"))
        {
            databaseInject(title, body, TaskState.COMPLETE, 0, unique_File_ID);
        }
        else if(chip.getText().toString().equals("High Priority"))
        {
            databaseInject(title, body, TaskState.HIGH_PRIORITY, 0, unique_File_ID);
        }
        taskList.clear();
        runQuery();
        //reDrawRecyclerFromDB();
    }


    private void uploadFile(String file) {
        //File sampleFile = file;

        if (file != null) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);

            }
        }
        unique_File_ID = UUID.randomUUID().toString();
        Amplify.Storage.uploadFile(
                unique_File_ID,
                file,
                new ResultListener<StorageUploadFileResult>() {
                    @Override
                    public void onResult(StorageUploadFileResult result) {
                        Log.i("StorageQuickStart", "Successfully uploaded: " + result.getKey());
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("StorageQuickstart", "Upload error.", error);
                    }
                }
        );
    }


    private void downloadFile(String fileID) {

        Amplify.Storage.downloadFile(
                fileID,
                getApplicationContext().getFilesDir() + "/download.txt",
                new ResultListener<StorageDownloadFileResult>() {
                    @Override
                    public void onResult(StorageDownloadFileResult result) {
                        Log.i("StorageQuickStart", "Successfully downloaded: " + result.getFile().getName());
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("StorageQuickStart", error.getMessage());
                    }
                }
        );
    }



    private void listFiles(String fileID) {
        Amplify.Storage.list(
                "",
                new ResultListener<StorageListResult>() {
                    @Override
                    public void onResult(StorageListResult storageListResult) {
                        for(StorageListResult.Item item : storageListResult.getItems()) {
                            Log.i("StorageQuickStart", "Item: " + item.getKey());
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("StorageQuickStart", error.getMessage());
                    }
                }
        );
    }



    private void removeFile(String fileID) {
        Amplify.Storage.remove(
                fileID,
                new ResultListener<StorageRemoveResult>() {
                    @Override
                    public void onResult(StorageRemoveResult storageRemoveResult) {
                        Log.i("StorageQuickStart", "Successfully removed: " + storageRemoveResult.getKey());
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("StorageQuickStart", error.getMessage());
                    }
                }
        );
    }



    public  void databaseInject(String title, String body, TaskState state, int due, String uniqueID)
    {
        Task task = new Task(title, body, state, due, uniqueID);
        unique_File_ID = " ";
        //database.taskDao().addTask(task);
        runCREATEMutation(task);
        adapter.notifyDataSetChanged();
    }

    public void runCREATEMutation(Task task){
        Long id = task.getId();

        CreateTaskInput createTodoInput = CreateTaskInput.builder()
                .title(task.getTitle())
                .body(task.getBody())
                .state(type.TaskState.valueOf(task.getState().toString()))
                .dueDate(task.getDueDate())
                .unique_File_ID(task.getUnique_File_ID()).build();

        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTodoInput).build())
                .enqueue(createMutationCallback);
    }

    private GraphQLCall.Callback<CreateTaskMutation.Data> createMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response)
        {
            Log.i("Results", "Added Todo");
           // reDrawRecyclerFromDB();
            taskList.clear();
            runQuery();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };

    public void runUPDATEMutation(Task task){
        Long id = task.getId();

        UpdateTaskInput updateTaskInput = UpdateTaskInput.builder()
                .id(task.getDynamoDbId())
                .title(task.getTitle())
                .body(task.getBody())
                .state(type.TaskState.valueOf(task.getState().toString()))
                .dueDate(task.getDueDate())
                .unique_File_ID(task.getUnique_File_ID()).build();


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


        removeFile(task.getUnique_File_ID());
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

    @Override
    protected void onStop() {
        super.onStop();
        showNotification(this, "New Task", " ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Provide read access to files and sub-directories in the user-selected
        // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, PICK_IMG_FILE);
    }

//    public void onActivityResult(int requestCode, int resultCode,
//                                 Intent resultData)
//    {
//        super.onActivityResult(requestCode, resultCode, resultData);
//        if (requestCode == PICK_IMG_FILE
//                && resultCode == Activity.RESULT_OK) {
//            // The result data contains a URI for the document or directory that
//            // the user selected.
//            Uri uri = null;
//            if (resultData != null) {
//                uri = resultData.getData();
//                uploadFile();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                    && null != data) {


                Uri URI = data.getData();
                String[] FILE = { MediaStore.Images.Media.DATA };


                Cursor cursor = getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                ImageDecode = cursor.getString(columnIndex);
                Log.i("DECODE", ImageDecode);

                uploadFile(ImageDecode);
                //uploadWithTransferUtility(ImageDecode);

                addTaskDialog();
                cursor.close();

                //imageViewLoad.setImageBitmap(BitmapFactory
                      //  .decodeFile(ImageDecode));

            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                    .show();
        }

    }


    private void openDir()
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, PICK_IMG_FILE);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMG_FILE);
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_IMG_FILE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("PERMITTED", "AUTHORIZED");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case READ_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("PERMITTED", "AUTHORIZED");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}



