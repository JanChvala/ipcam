package cz.janchvala.android.ipcamera.helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import cz.janchvala.android.ipcamera.service.IPCameraPreviewWindow_;
import wei.mark.standout.StandOutWindow;

/**
 * IntentHelper contains methods for creating specific Intents for streaming and playback.
 * <p/>
 * Created by jan on 15.05.2015.
 */
public class IntentHelper {

    /**
     * the path of streaming page on the server side
     */
    private static final String STREAMING_PATH = "assets/ipcamera.html";

    /**
     * Method creates Intent for sharing the stream. The playback URL is build from preferences.
     *
     * @param prefs IPCameraPreferences instance
     * @return Intent for sharing playback URL
     */
    public static Intent createShareIntent(IPCameraPreferences_ prefs) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Android IP camera: Watch my stream");
        i.putExtra(Intent.EXTRA_TEXT, getPlaybackUrl(prefs));
        return Intent.createChooser(i, "Sharing Android IP camera stream URL");
    }

    /**
     * Private method for building streaming or playback URL.
     *
     * @param prefs       IPCameraPreferences
     * @param play        if true that playback URL is build streaming otherwise
     * @param quitOnEmpty parameter which changes the behavior for explicit streaming. If true than the stream is not closed after all participants have left.
     * @return URL of requested page
     */
    private static String getUrl(IPCameraPreferences_ prefs, boolean play, Boolean quitOnEmpty) {
        Uri.Builder uri = Uri.parse(prefs.serverUrl().get()).buildUpon();
        uri.appendEncodedPath(STREAMING_PATH);
        uri.appendQueryParameter("room", prefs.accessToken().get());

        String password = prefs.accessPassword().get();
        if (play) {
            // password is set up only for playback
            uri.appendQueryParameter("password", password);
        } else {
            // stream settings for streaming page
            uri.appendQueryParameter("stream", String.valueOf(true));
            uri.appendQueryParameter("minWidth", String.valueOf(prefs.streamMinWidth().get()));
            uri.appendQueryParameter("minHeight", String.valueOf(prefs.streamMinHeight().get()));
            uri.appendQueryParameter("maxWidth", String.valueOf(prefs.streamMaxWidth().get()));
            uri.appendQueryParameter("maxHeight", String.valueOf(prefs.streamMaxHeight().get()));
            uri.appendQueryParameter("bandwidth", String.valueOf(prefs.streamBandWidth().get()));
            uri.appendQueryParameter("minFrameRate", String.valueOf(prefs.streamMinFrameRate().get()));
            uri.appendQueryParameter("maxFrameRate", String.valueOf(prefs.streamMinFrameRate().get()));

            // requests on pre-lollipop devices are redirected to web browser which requires accepting the
            // local media each time we load the page. This is not desirable so we leave the page loaded
            // even when all participants have left.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // when loading external web browser we need to set password
                uri.appendQueryParameter("password", password);
            }

            uri.appendQueryParameter("quitOnEmptyRoom", String.valueOf(quitOnEmpty));
        }

        return uri.build().toString();
    }

    /**
     * method to get streaming URL.
     *
     * @param ipCamPreferences IPCameraPreferences
     * @param quitOnEmpty      if true streaming is ended after all participants have left
     * @return streaming URL
     */
    public static String getStreamUrl(IPCameraPreferences_ ipCamPreferences, boolean quitOnEmpty) {
        return getUrl(ipCamPreferences, false, quitOnEmpty);
    }

    /**
     * method to get playback URL.
     *
     * @param ipCamPreferences IPCameraPreferences
     * @return playback URL
     */
    public static String getPlaybackUrl(IPCameraPreferences_ ipCamPreferences) {
        return getUrl(ipCamPreferences, true, null);
    }

    /**
     * Method starts Activity which starts the streaming. This will either start IPCameraPreviewWindow Service
     * or the default web browser via Intent. The external web browser starts when the WebView does not natively
     * support WebRTC technology.
     *
     * @param context          context to start the activity from
     * @param ipCamPreferences IPCameraPreferences
     * @param quitOnEmpty      if true streaming is ended after all participants have left
     */
    public static void startStreaming(Context context, IPCameraPreferences_ ipCamPreferences, boolean quitOnEmpty) {
        if (webViewSupportsWebRTC()) {
            Intent showIpCamWidowsIntent = StandOutWindow.getShowIntent(context, IPCameraPreviewWindow_.class, 0);
            showIpCamWidowsIntent.putExtra("quitOnEmpty", quitOnEmpty);
            context.startService(showIpCamWidowsIntent);
        } else {
            String url = IntentHelper.getStreamUrl(ipCamPreferences, quitOnEmpty);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));

            if (context instanceof Service) {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(i);
        }
    }

    /**
     * method decides whether the WebRTC is natively supported in WebView component or not.
     *
     * @return true if WebRTC is supported.
     */
    private static boolean webViewSupportsWebRTC() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
