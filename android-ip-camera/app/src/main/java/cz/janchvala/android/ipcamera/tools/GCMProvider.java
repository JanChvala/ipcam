package cz.janchvala.android.ipcamera.tools;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import cz.janchvala.android.ipcamera.IpCameraApplication_;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;

/**
 * GCMProvider is helper for GoogleCloudMessaging registration.
 * <p/>
 * Created by xchval01 on 12.03.2015.
 */
@EBean
public class GCMProvider {

    private static final int MAX_TRY_COUNT = 3;

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    GoogleCloudMessaging gcm;
    AtomicInteger tryCounter = new AtomicInteger(0);

    public GCMProvider() {
    }

    /**
     * Registers device for GCM. This method is called on background thread.
     *
     * @param senderIds the sender ID
     * @param callbacks GCM callbacks
     */
    public void registerAsync(String[] senderIds, OnRegistrationResult callbacks) {
        registerAsyncInBackground(senderIds, callbacks);
    }

    /**
     * Same as registerAsyncInBackground but with 3000ms delay.
     *
     * @param senderIds sender IDs to register to
     * @param callbacks GCM callbacks
     */
    @Background(delay = 3000)
    protected void registerAsyncInBackgroundDelay(String[] senderIds, OnRegistrationResult callbacks) {
        registerAsyncInBackground(senderIds, callbacks);
    }

    /**
     * Register to given sender ids. On success save the registration ID into shared preferences.
     * Calling callbacks on success or failure.
     *
     * @param senderIds sender IDs to register to
     * @param callbacks GCM callbacks
     */
    @Background
    protected void registerAsyncInBackground(String[] senderIds, OnRegistrationResult callbacks) {
        try {
            String regId = getGcm().register(senderIds);

            // Persist the registration ID - no need to register again.
            ipCamPreferences.gcmRegistrationId().put(regId);

            // success callback
            if (callbacks != null) {
                callbacks.onRegistrationSuccess(regId);
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();

            // failure callback
            if (callbacks != null) {
                callbacks.onRegistrationFailed();
            }
        }

        // if the registration was not successful retry with delay
        if (tryCounter.incrementAndGet() <= MAX_TRY_COUNT) {
            registerAsyncInBackgroundDelay(senderIds, callbacks);
        } else {
            tryCounter.set(0);
        }
    }

    /**
     * helper method to create and get GoogleCloudMessaging instance.
     *
     * @return GoogleCloudMessaging instance
     */
    private GoogleCloudMessaging getGcm() {
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(IpCameraApplication_.getInstance().getApplicationContext());
        }
        return gcm;
    }
}
