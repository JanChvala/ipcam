package cz.janchvala.android.ipcamera.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cz.janchvala.android.ipcamera.helpers.IntentHelper;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import cz.janchvala.android.ipcamera.receiver.GcmBroadcastReceiver;

/**
 * Intent service for GCM message handling. This service is very simple and treats all messages as the
 * one which starts the streaming.
 * <p/>
 * Created by xchval01 on 12.03.2015.
 */
@EService
public class GcmIntentService extends IntentService {

    /**
     * LOG tag.
     */
    private static final String TAG = "GcmService";

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // when the streaming is not enabled we simply ignore the GCM messages
        if (!ipCamPreferences.streamingEnabled().get()) {
            return;
        }

        Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {
            // Initialize GCM
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

            // getting the message type
            String messageType = gcm.getMessageType(intent);

            Log.i(TAG, "MessageType: " + messageType);
            Log.i(TAG, "Received extras: " + extras.toString());

            // If this is regular message than start stream
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // automatically quit when only using WebView on Lollipop
                boolean quitOnEmptyRoom = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
                IntentHelper.startStreaming(this, ipCamPreferences, quitOnEmptyRoom);
            }
        }

        // Releasing the wake lock
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
