package cz.janchvala.android.ipcamera.preferences;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import cz.janchvala.android.ipcamera.R;

/**
 * Shared preferences for storing registration information.
 *
 * @author xchval01 on 22. 10. 2014.
 */
@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface IPcamPreferences {

    @DefaultString(keyRes = R.string.preferences_server_url, value = "http://ipcam.janchvala.cz") // "http://83.167.253.81:12784/subdomain/ipcam", "http://10.0.0.10:8080/subdomain/ipcam")
    String serverUrl();

    @DefaultString(keyRes = R.string.preferences_access_hash, value = "")
    String accessToken();

    @DefaultString(keyRes = R.string.preferences_stream_access_password, value = "")
    String accessPassword();

    @DefaultBoolean(keyRes = R.string.preferences_stream_enabled, value = false)
    boolean streamingEnabled();

    @DefaultInt(keyRes = R.string.preferences_try_count_gcm, value = 0)
    int registrationCountGcm();

    @DefaultInt(keyRes = R.string.preferences_try_count_google_play_services, value = 0)
    int registrationCountGooglePlay();

    @DefaultInt(keyRes = R.string.preferences_try_count_server_registration, value = 0)
    int registrationCountServer();



    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    @DefaultString(keyRes = R.string.preferences_gcm_sender_id, value = "641044930117")
    String gcmSenderId();

    @DefaultString(keyRes = R.string.preferences_gcm_registration_id, value = "")
    String gcmRegistrationId();



    @DefaultInt(keyRes = R.string.preferences_stream_min_width, value = 1280)
    int streamMinWidth();

    @DefaultInt(keyRes = R.string.preferences_stream_min_height, value = 720)
    int streamMinHeight();

    @DefaultInt(keyRes = R.string.preferences_stream_max_width, value = 1280)
    int streamMaxWidth();

    @DefaultInt(keyRes = R.string.preferences_stream_max_height, value = 720)
    int streamMaxHeight();

    @DefaultInt(keyRes = R.string.preferences_stream_bandwidth, value = 3000)
    int streamBandWidth();

    @DefaultInt(keyRes = R.string.preferences_stream_min_frame_rate, value = 30)
    int streamMinFrameRate();

    @DefaultInt(keyRes = R.string.preferences_stream_max_frame_rate, value = 60)
    int streamMaxFrameRate();
}
