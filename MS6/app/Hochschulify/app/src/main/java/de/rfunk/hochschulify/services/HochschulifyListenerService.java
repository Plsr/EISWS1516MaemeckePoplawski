package de.rfunk.hochschulify.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.R;
import de.rfunk.hochschulify.activities.HomeActivity;
import de.rfunk.hochschulify.activities.SingleThreadActivity;
import de.rfunk.hochschulify.utils.Req;
import de.rfunk.hochschulify.utils.Utils;

/**
 * Created by Cheese on 14/01/16.
 */
public class HochschulifyListenerService extends GcmListenerService {
    private static final String TAG = HochschulifyListenerService.class.getSimpleName();

    private static final String SERVER_URL = Utils.SERVER_URL;
    private static final String USER_PATH = Utils.USER_PATH;
    private static final String ENTRY_PATH = Utils.ENTRY_PATH;

    JSONObject mUser;
    JSONObject mEntry;



    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, data.toString());
        String entryID = data.getString("entry");
        String userID = data.getString("fromuser");

        String userUrl = SERVER_URL + USER_PATH + "/" + userID;
        final String entryURL = SERVER_URL + ENTRY_PATH + "/" + entryID;

        Req userReq = new Req(this, userUrl);
        userReq.getWithAuth(new Req.Res() {
            @Override
            public void onSuccess(JSONObject res) {
                Log.d(TAG, res.toString());
                mUser = res;

                Req entryReq = new Req(HochschulifyListenerService.this, entryURL);
                entryReq.get(new Req.Res() {
                    @Override
                    public void onSuccess(JSONObject res) {
                        Log.d(TAG, res.toString());
                        mEntry = res;

                        sendNotification();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.e(TAG, "error", error);
                    }
                });
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "error", error);
            }
        });




        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Entry ID: " + entryID);
        Log.d(TAG, "User ID: " + userID);





        // [END_EXCLUDE]
    }

    private void sendNotification()  {
        Intent intent = new Intent(this, SingleThreadActivity.class);
        intent.putExtra("entry", mEntry.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String notificationContent = "Neue Antwort auf ";

        String notificationTitle = null;
        try {
            notificationTitle = mUser.getString("name");

            if(Utils.isEmptyString(mEntry.getString("title"))) {
                notificationContent +=  mEntry.getString("text");
            } else {
                notificationContent += mEntry.getString("title");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.users)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
