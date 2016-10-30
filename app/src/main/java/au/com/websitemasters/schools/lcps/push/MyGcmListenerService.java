/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.websitemasters.schools.lcps.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import au.com.websitemasters.schools.lcps.R;
import au.com.websitemasters.schools.lcps.SchoolsApplication;
import au.com.websitemasters.schools.lcps.activities.AlertsMenuActivity;
import au.com.websitemasters.schools.lcps.activities.NewsMenuActivity;
import au.com.websitemasters.schools.lcps.activities.NewslettersMenuActivity;
import au.com.websitemasters.schools.lcps.utils.SQLiteHelper;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "rklogs";

    public SQLiteDatabase db;
    public SQLiteHelper dbHelper;

    public static final String BROADCAST_ACTION = "com.mukesh.service";

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
        String message = data.getString("message");


        Log.d("rklogs", "data.toString()_" + data.toString());

        Log.d("rklogs", "From: " + from);
        Log.d("rklogs", "Message: " + message);

        if (from.startsWith("/topics/")) {

            Log.d("rklogs", "message received from some topic.");
        } else {

            Log.d("rklogs", "normal downstream message.");
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        try {


            if ((data.getString("catid").equals("8"))) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("News") == true) {
                    sendNotification(message, NewsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("20")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Pre-Kindy") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("21")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Kindy") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("22")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Pre-Primary") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("23")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year1") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("24")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year2") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("25")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year3") == true) {
                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("26")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year4") == true) {

                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("27")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year5") == true) {

                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (data.getString("catid").equals("28")) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Year6") == true) {

                    sendNotification(message, AlertsMenuActivity.class);
                }
            }

            if (((data.getString("catid").equals("15")) || (data.getString("catid").equals("16")) ||
                    (data.getString("catid").equals("17")) || (data.getString("catid").equals("18")))) {
                if (((SchoolsApplication) getApplicationContext()).getSetting("Newsletters") == true) {
                    sendNotification(message, NewslettersMenuActivity.class);
                }
            }

        } catch (NullPointerException e) {
            Log.d("rklogs", "ER! Nullpointer in MyGCMListener");
        }

        // [END_EXCLUDE]
    }
    // [END receive_message]

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */

    public void sendNotification(String message, Class clas) {

        //load not readed. +1. save em.
        int notReaded = ((SchoolsApplication) getApplicationContext()).loadBadgesCount();
        notReaded++;
        ((SchoolsApplication) getApplicationContext()).saveBadgesCount(notReaded);

        //show it on badge.
        ShortcutBadger.applyCount(getApplicationContext(), notReaded);

        Intent intent = new Intent(this, clas);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_gray)
                .setContentTitle("Leschenault Catholic Primary School")
                .setContentText(message)
                .setAutoCancel(true)
                .setNumber(notReaded)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        //push realtime refresh of lists (ANN)
        Intent intentBroadcast = new Intent(BROADCAST_ACTION);
        sendBroadcast(intentBroadcast);
    }

}
