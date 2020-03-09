package com.echokinetic.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.notification.NotificationDetails;
import com.echokinetic.taskmaster.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class NotificationHandler extends FirebaseMessagingService {
    public static final String TAG = NotificationHandler.class.getSimpleName();

    // Intent action used in local broadcast
    public static final String ACTION_PUSH_NOTIFICATION = "push-notification";
    // Intent keys
    public static final String INTENT_SNS_NOTIFICATION_FROM = "from";
    public static final String INTENT_SNS_NOTIFICATION_DATA = "data";

//    @Override
//    public void onNewToken(String token) {
//        super.onNewToken(token);
//
//        Log.d(TAG, "Registering push notifications token: " + token);
//        //MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient().registerDeviceToken(token);
//    }

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "Message: " + remoteMessage.getData());
//
//       // final NotificationClient notificationClient = MainActivity.getPinpointManager(getApplicationContext()).getNotificationClient();
//
//        final NotificationDetails notificationDetails = NotificationDetails.builder()
//                .from(remoteMessage.getFrom())
//                .mapData(remoteMessage.getData())
//                .intentAction(NotificationClient.FCM_INTENT_ACTION)
//                .build();
//
//     //   NotificationClient.CampaignPushResult pushResult = notificationClient.handleCampaignPush(notificationDetails);
//
//        if (!NotificationClient.CampaignPushResult.NOT_HANDLED.equals(pushResult)) {
//            /**
//             The push message was due to a Pinpoint campaign.
//             If the app was in the background, a local notification was added
//             in the notification center. If the app was in the foreground, an
//             event was recorded indicating the app was in the foreground,
//             for the demo, we will broadcast the notification to let the main
//             activity display it in a dialog.
//             */
//            if (NotificationClient.CampaignPushResult.APP_IN_FOREGROUND.equals(pushResult)) {
//                /* Create a message that will display the raw data of the campaign push in a dialog. */
//                final HashMap<String, String> dataMap = new HashMap<>(remoteMessage.getData());
//                broadcast(remoteMessage.getFrom(), dataMap);
//            }
//            return;
//        }
//    }

    private void broadcast(final String from, final HashMap<String, String> dataMap) {
        Intent intent = new Intent(ACTION_PUSH_NOTIFICATION);
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from);
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Helper method to extract push message from bundle.
     *
     * @param data bundle
     * @return message string from push notification
     */
    public static String getMessage(Bundle data) {
        return ((HashMap) data.get("data")).toString();
    }
}

//package com.echokinetic.taskmaster;
//
//import android.content.BroadcastReceiver;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.Person;
//import androidx.core.app.RemoteInput;
//
//import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
//import com.amazonaws.mobile.config.AWSConfiguration;
//import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
//import com.apollographql.apollo.GraphQLCall;
//import com.apollographql.apollo.api.Response;
//import com.apollographql.apollo.exception.ApolloException;
//
//import javax.annotation.Nonnull;
//
//import type.CreateTaskInput;
//
//import static type.ModelAttributeTypes.string;
//
//
//public class NotificationHandler extends BroadcastReceiver
//{
//
//    private AWSAppSyncClient mAWSAppSyncClient;
//
//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        mAWSAppSyncClient = AWSAppSyncClient.builder()
//                .context(context)
//                .awsConfiguration(new AWSConfiguration(context))
//                .build();
//        //getting the remote input bundle from intent
//        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
//
//        //if there is some input
//        if (remoteInput != null)
//        {
//
//            //getting the input value
//            CharSequence input = remoteInput.getCharSequence(MainActivity.KEY_REPLY);
//
//            String string_Input = input.toString();
//
//            runCREATEMutation(string_Input);
//
//            //MainActivity.databaseInject(input.toString(), " ", TaskState.NEW);
//
//
//
//
//
//
//
////            Person person = new Person.Builder()
////                    .setName("ADD NEW TASK")
////                    .build();
////
////            NotificationCompat.MessagingStyle chatMessageStyle = new NotificationCompat.MessagingStyle(person);
////
////            NotificationCompat.MessagingStyle.Message notificationMessage = new
////                    NotificationCompat.MessagingStyle.Message(
////                    " ",
////                    System.currentTimeMillis(),
////                    person);
////            chatMessageStyle.addMessage(notificationMessage);
//
//
//
//            //updating the notification with the input value
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
//                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
//                    .setContentTitle("Task titled: " + string_Input + ", added!");
//                    //.setStyle(chatMessageStyle);
//
//
//
//            NotificationManager notificationManager = (NotificationManager) context.
//                    getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
//        }
//
////        //if help button is clicked
////        if (intent.getIntExtra(MainActivity.KEY_INTENT_HELP, -1) == MainActivity.REQUEST_CODE_HELP) {
////            Toast.makeText(context, "You Clicked Help", Toast.LENGTH_LONG).show();
////        }
////
////        //if more button is clicked
////        if (intent.getIntExtra(MainActivity.KEY_INTENT_MORE, -1) == MainActivity.REQUEST_CODE_MORE) {
////            Toast.makeText(context, "You Clicked More", Toast.LENGTH_LONG).show();
////        }
//    }
//
//    public void runCREATEMutation(String string){
//        Task task = new Task(string, "", TaskState.NEW, 0);
//
//        CreateTaskInput createTodoInput = CreateTaskInput.builder()
//                .title(task.getTitle())
//                .body(task.getBody())
//                .state(type.TaskState.valueOf(task.getState().toString()))
//                .dueDate(task.getDueDate()).build();
//
//        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTodoInput).build())
//                .enqueue(createMutationCallback);
//    }
//
//    private GraphQLCall.Callback<CreateTaskMutation.Data> createMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
//        @Override
//        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response)
//        {
//
//
//        }
//
//        @Override
//        public void onFailure(@Nonnull ApolloException e) {
//            Log.e("Error", e.toString());
//        }
//    };
//}
