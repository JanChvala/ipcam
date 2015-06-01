package cz.janchvala.android.ipcamera.helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import cz.janchvala.android.ipcamera.preferences.IPcamPreferences_;
import cz.janchvala.android.ipcamera.service.IpCamPreviewWindow_;
import wei.mark.standout.StandOutWindow;

/**
 * Created by jan on 15.05.2015.
 */
public class IntentHelpers {

    public static Intent createShareIntent(IPcamPreferences_ prefs) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Android IP camera: Watch my stream");
        i.putExtra(Intent.EXTRA_TEXT, getPlaybackUrl(prefs));
        return Intent.createChooser(i, "Sharing Android IP camera stream URL");
    }

    private static String getUrl(IPcamPreferences_ prefs, boolean play, Boolean quitOnEmpty) {
        Uri.Builder uri = Uri.parse(prefs.serverUrl().get()).buildUpon();
        uri.appendEncodedPath("assets/broadcast.html");
        uri.appendQueryParameter("room", prefs.accessToken().get());

        String password = prefs.accessPassword().get();
        if (play) {
            uri.appendQueryParameter("password", password);
        } else {
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
                uri.appendQueryParameter("password", password);
            }

            uri.appendQueryParameter("quitOnEmptyRoom", String.valueOf(quitOnEmpty));
        }

        return uri.build().toString();
    }

    public static String getStreamUrl(IPcamPreferences_ ipCamPreferences, boolean quitOnEmpty) {
        return getUrl(ipCamPreferences, false, quitOnEmpty);
    }

    public static String getPlaybackUrl(IPcamPreferences_ ipCamPreferences) {
        return getUrl(ipCamPreferences, true, null);
    }

    public static void startStreaming(Context context,IPcamPreferences_ ipCamPreferences, boolean quitOnEmpty) {
        if (webViewSupportsWebRTC()) {
            Intent showIpCamWidowsIntent = StandOutWindow.getShowIntent(context, IpCamPreviewWindow_.class, 0);
            showIpCamWidowsIntent.putExtra("quitOnEmpty", quitOnEmpty);
            context.startService(showIpCamWidowsIntent);
        } else {
            String url = IntentHelpers.getStreamUrl(ipCamPreferences, quitOnEmpty);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));

            if (context instanceof Service) {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(i);
        }
        /*StartupActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();*/
    }

    private static boolean webViewSupportsWebRTC() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
