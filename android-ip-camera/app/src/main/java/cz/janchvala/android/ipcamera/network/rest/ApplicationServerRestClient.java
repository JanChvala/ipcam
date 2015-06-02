package cz.janchvala.android.ipcamera.network.rest;

import android.support.annotation.IntDef;
import android.util.Base64;
import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cz.janchvala.android.ipcamera.network.bean.DeviceRegistration;
import cz.janchvala.android.ipcamera.network.bean.Token;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * GcmRestClient is used for communication with server backend.
 * <p/>
 * Created by xchval01 on 12.02.2015.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ApplicationServerRestClient {

    public static final int ERROR_NONE = 0;
    public static final int ERROR_NOT_SUCCESSFUL = 1;

    /**
     * Definition of error codes.
     */
    @IntDef({ERROR_NONE, ERROR_NOT_SUCCESSFUL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RegistrationErrors {
    }

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    public Token registerApplication(DeviceRegistration deviceRegistration) {
        IApplicationServerRestAPI api = createRestApi(IApplicationServerRestAPI.class);

        // Fetch and print a list of the contributors to this library.
        return api.registerDevice(deviceRegistration);
    }

    @Background
    public void sendRegistrationIdToBackendAsync(String regId, RegistrationCallbacks callbacks) {
        Token device;
        try {
            device = registerApplication(new DeviceRegistration(regId, regId));
            ipCamPreferences.accessToken().put(device.getToken());
            callbacks.onRegistrationFinished(ERROR_NONE, null);
        } catch (Exception e) {
            ipCamPreferences.accessToken().put("");
            callbacks.onRegistrationFinished(ERROR_NOT_SUCCESSFUL, e);
        }
    }

    private <T> T createRestApi(Class<T> apiClazz) {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        // Create a very simple REST adapter which points the GitHub API endpoint.
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ipCamPreferences.serverUrl().get())
                .setClient(new OkClient(client))
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.i("HTTP", message);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL).build();

        // Create an instance of our GitHub API interface.
        return restAdapter.create(apiClazz);
    }

}
