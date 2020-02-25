package com.echokinetic.taskmaster;

import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

import static type.ModelAttributeTypes.string;


public class NotificationHandler extends BroadcastReceiver
{

    private AWSAppSyncClient mAWSAppSyncClient;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(context)
                .awsConfiguration(new AWSConfiguration(context))
                .build();
        //getting the remote input bundle from intent
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        //if there is some input
        if (remoteInput != null)
        {

            //getting the input value
            CharSequence input = remoteInput.getCharSequence(MainActivity.KEY_REPLY);

            String string_Input = input.toString();

            //MainActivity.databaseInject(input.toString(), " ", TaskState.NEW);







//            Person person = new Person.Builder()
//                    .setName("ADD NEW TASK")
//                    .build();
//
//            NotificationCompat.MessagingStyle chatMessageStyle = new NotificationCompat.MessagingStyle(person);
//
//            NotificationCompat.MessagingStyle.Message notificationMessage = new
//                    NotificationCompat.MessagingStyle.Message(
//                    " ",
//                    System.currentTimeMillis(),
//                    person);
//            chatMessageStyle.addMessage(notificationMessage);



            //updating the notification with the input value
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setContentTitle("Task titled: " + string_Input + ", added!");
                    //.setStyle(chatMessageStyle);



            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
        }

//        //if help button is clicked
//        if (intent.getIntExtra(MainActivity.KEY_INTENT_HELP, -1) == MainActivity.REQUEST_CODE_HELP) {
//            Toast.makeText(context, "You Clicked Help", Toast.LENGTH_LONG).show();
//        }
//
//        //if more button is clicked
//        if (intent.getIntExtra(MainActivity.KEY_INTENT_MORE, -1) == MainActivity.REQUEST_CODE_MORE) {
//            Toast.makeText(context, "You Clicked More", Toast.LENGTH_LONG).show();
//        }
    }

    public void runCREATEMutation(String string){
        Task task = new Task(string, "", TaskState.NEW, 0);

        CreateTaskInput createTodoInput = CreateTaskInput.builder()
                .title(task.getTitle())
                .body(task.getBody())
                .state(type.TaskState.valueOf(task.getState().toString()))
                .dueDate(task.getDueDate()).build();

        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTodoInput).build())
                .enqueue(createMutationCallback);
    }

    private GraphQLCall.Callback<CreateTaskMutation.Data> createMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response)
        {
            Log.i("Results", "Added Todo");
            //MainActivity.reDraw();
            // reDrawRecyclerFromDB();

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };
}
