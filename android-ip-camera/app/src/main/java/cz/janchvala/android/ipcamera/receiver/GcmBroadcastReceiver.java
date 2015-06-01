package cz.janchvala.android.ipcamera.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import cz.janchvala.android.ipcamera.service.GcmIntentService_;


/**
 * GcmBroadcastReceiver is invoked whenever GCM message is received. This will just call a service
 * which handles actual message handling.
 * <p/>
 * Created by xchval01 on 12.03.2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent intent) {
        // specify that GcmIntentService will handle the intent
        ComponentName cName = new ComponentName(c.getPackageName(), GcmIntentService_.class.getName());
        intent.setComponent(cName);

        // Start the service and keep device awake
        startWakefulService(c, intent);

        // set the result of this receive as success
        setResultCode(Activity.RESULT_OK);
    }
}
