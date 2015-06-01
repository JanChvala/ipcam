package cz.janchvala.android.ipcamera.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.helpers.IntentHelpers;
import cz.janchvala.android.ipcamera.preferences.IPcamPreferences_;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

/**
 * This service is created only for Android Lollipop and above where WebView supports WebRTC technology.
 *
 * Created by jan on 14.05.2015.
 */
@EService
public class IpCamPreviewWindow extends StandOutWindow {

    @Pref
    IPcamPreferences_ ipCamPreferences;

    WebView wv = null;

    boolean quitOnEmpty;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        quitOnEmpty = intent.getBooleanExtra("quitOnEmpty", true);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public String getAppName() {
        return getString(R.string.ip_cam_streaming);
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return getAppName();
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_videocam_black_48dp;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        // create a new layout from body.xml
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ipcam_preview_window, frame, true);

        wv = (WebView) frame.findViewById(R.id.wv_ipcamera_preview_window);
        setupWebView(wv);
    }

    /**
     * setup WebView JavaScript, ChromeClient and load stream page
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupWebView(WebView wv) {
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new IpCamWebInterface(this), "Android");
        webSettings.setUserAgentString("this-is-webrtc-user-agent-demo");

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                // handling WebRTC permission request
                Log.d("WebRTC", "onPermissionRequest");
                dispatchPermissionRequest(request);
            }

            @Override
            public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        wv.loadUrl(IntentHelpers.getStreamUrl(ipCamPreferences, quitOnEmpty));
    }


    @UiThread
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void dispatchPermissionRequest(PermissionRequest request) {
        String origin = request.getOrigin().toString();
        Log.d("WebRTC request origin", origin);
        Log.d("server origin", ipCamPreferences.serverUrl().get());

        if (origin.startsWith(ipCamPreferences.serverUrl().get())) {
            Log.d("server origin", "accepted");
            request.grant(request.getResources());
        } else {
            Log.d("server origin", "denied");
            request.deny();
        }
    }


    // the window will be centered
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return new StandOutLayoutParams(id, Math.round(400 * density), Math.round(300 * density), StandOutLayoutParams.RIGHT, StandOutLayoutParams.BOTTOM);
    }

    // move the window by dragging the view
    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "Click to stop camera streaming.";
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return StandOutWindow.getShowIntent(this, IpCamPreviewWindow_.class, id);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        onClose(0, null);
    }

    @Override
    public Notification getPersistentNotification(int id) {
        // basic notification stuff: http://developer.android.com/guide/topics/ui/notifiers/notifications.html
        int icon = getAppIcon();
        long when = System.currentTimeMillis();
        Context c = getApplicationContext();

        String contentTitle = getPersistentNotificationTitle(id);
        String contentText = getPersistentNotificationMessage(id);
        String tickerText = String.format("%s: %s", contentTitle, contentText);

        Intent notificationIntent = getPersistentNotificationIntent(id);
        Intent closeIntent = StandOutWindow.getCloseIntent(c, IpCamPreviewWindow_.class, id);
        Intent shareIntent = IntentHelpers.createShareIntent(ipCamPreferences);

        PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent closeClickIntent = PendingIntent.getService(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent shareClickIntent = PendingIntent.getActivity(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setSmallIcon(icon);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);

        builder.setContentIntent(contentIntent);
        builder.addAction(R.drawable.ic_share_white_24dp, "Share", shareClickIntent);
        builder.addAction(R.drawable.close, "Stop", closeClickIntent);

        return builder.build();
    }


    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onClose(final int id, Window window) {
        Log.d("######### destroying", "fasd fhsa fhl skaf");
        if (wv != null) {
            wv.evaluateJavascript("if(window.connection){window.connection.close();}", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.d("###### CLOSING", value);
                    wv.loadUrl("about:blank");
                    wv = null;
                    close(id);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public class IpCamWebInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        IpCamWebInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void closeStreaming() {
            mContext.startService(StandOutWindow.getCloseAllIntent(mContext, IpCamPreviewWindow_.class));
        }
        /** Show a toast from the web page */
        @JavascriptInterface
        public String getPassword() {
            return ipCamPreferences.accessPassword().get();
        }
    }
}
