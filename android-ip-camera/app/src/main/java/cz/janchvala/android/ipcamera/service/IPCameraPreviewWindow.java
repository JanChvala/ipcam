package cz.janchvala.android.ipcamera.service;

import android.annotation.SuppressLint;
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
import org.androidannotations.annotations.sharedpreferences.Pref;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.helpers.IntentHelper;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

/**
 * This service is created only for Android Lollipop and above where WebView supports WebRTC technology.
 * <p/>
 * Created by jan on 14.05.2015.
 */
@EService
public class IPCameraPreviewWindow extends StandOutWindow {

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    WebView wv = null;

    boolean quitOnEmpty;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // check Intent extrsa parameter
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
        // inflate layout for ipcamera preview window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ipcam_preview_window, frame, true);

        // find the WebView
        wv = (WebView) frame.findViewById(R.id.wv_ipcamera_preview_window);
        setupWebView(wv);
    }

    /**
     * Set up WebView JavaScript, ChromeClient and load stream page.
     */
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupWebView(WebView wv) {
        WebSettings webSettings = wv.getSettings();

        // enabling JavaScript
        webSettings.setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new IPCameraWebInterface(), "Android");

        // this is hack fot RTCMultiConnection library to work on mobile devices.
        webSettings.setUserAgentString("this-is-webrtc-user-agent-demo");

        // chrome client for WebRTC permission request and message logging
        wv.setWebChromeClient(new WebChromeClient() {

            /**
             * WebRTC permission request has to be allowed for the server registered in preferences.
             *
             * @param request permission request
             */
            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void onPermissionRequest(final PermissionRequest request) {
                // handling WebRTC permission request
                Log.d("WebRTC", "onPermissionRequest");
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

            /**
             * showing debug information from web console to LogCat.
             *
             * @param cm - message
             * @return true - we handle it
             */
            @Override
            public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        wv.loadUrl(IntentHelper.getStreamUrl(ipCamPreferences, quitOnEmpty));
    }


    /**
     * Setting the layout for 400dp × 300dp. Right bottom alignment.
     *
     * @param id     service window id
     * @param window Window instance
     * @return layout parameters
     */
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return new StandOutLayoutParams(id, Math.round(400 * density), Math.round(300 * density), StandOutLayoutParams.RIGHT, StandOutLayoutParams.BOTTOM);
    }

    /**
     * Setting the flags to be able to move the window by dragging the view.
     *
     * @param id service window id
     * @return windo flags
     */
    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
    }

    /**
     * Title message for notification.
     *
     * @param id service window id
     * @return notification title
     */
    @Override
    public String getPersistentNotificationMessage(int id) {
        return "Click to stop camera streaming.";
    }

    /**
     * Show the IPCameraPreviewWindow_ when clicking on notification.
     *
     * @param id service window id
     * @return Intent of the notification
     */
    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return StandOutWindow.getShowIntent(this, IPCameraPreviewWindow_.class, id);
    }


    /**
     * Close everything if this Service is being destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        onClose(0, null);
    }

    /**
     * creating notification to be able to stop the streaming or share it.
     *
     * @param id service service window id
     * @return created notification
     */
    @Override
    public Notification getPersistentNotification(int id) {
        // basic notification stuff: http://developer.android.com/guide/topics/ui/notifiers/notifications.html
        int icon = getAppIcon();
        long when = System.currentTimeMillis();
        Context c = getApplicationContext();

        // titles and texts
        String contentTitle = getPersistentNotificationTitle(id);
        String contentText = getPersistentNotificationMessage(id);
        String tickerText = String.format("%s: %s", contentTitle, contentText);

        // action intents
        Intent notificationIntent = getPersistentNotificationIntent(id);
        Intent closeIntent = StandOutWindow.getCloseIntent(c, IPCameraPreviewWindow_.class, id);
        Intent shareIntent = IntentHelper.createShareIntent(ipCamPreferences);

        // pending Intents for action Intents
        PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent closeClickIntent = PendingIntent.getService(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent shareClickIntent = PendingIntent.getActivity(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // creating the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setSmallIcon(icon);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);

        // setting notification buttons
        builder.setContentIntent(contentIntent);
        builder.addAction(R.drawable.ic_share_white_24dp, "Share", shareClickIntent);
        builder.addAction(R.drawable.close, "Stop", closeClickIntent);

        return builder.build();
    }


    /**
     * Clean the WebRTC and WebView in case that this service is closed and destroyed.
     *
     * @param id     service window id
     * @param window Window instance
     * @return true
     */
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onClose(final int id, Window window) {
        Log.d("### destroying", "the streaming window is closing");
        if (wv != null) {
            wv.evaluateJavascript("if(window.connection){window.connection.close();}", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.d("### CLOSING", value);
                    wv.loadUrl("about:blank");
                    wv = null;
                    close(id);
                }
            });
        }
        return true;
    }


    /**
     * This class contains method representing.
     */
    public class IPCameraWebInterface {
        /**
         * Instantiate the interface and set the context.
         */
        public IPCameraWebInterface() {
        }

        /**
         * Show a toast from the web page.
         */
        @JavascriptInterface
        public void closeStreaming() {
            Intent i = StandOutWindow.getCloseAllIntent(IPCameraPreviewWindow.this, IPCameraPreviewWindow_.class);
            IPCameraPreviewWindow.this.startService(i);
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public String getPassword() {
            return ipCamPreferences.accessPassword().get();
        }
    }
}
