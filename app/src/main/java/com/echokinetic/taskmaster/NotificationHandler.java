package com.echokinetic.taskmaster;

import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

public class NotificationHandler extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
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
}
