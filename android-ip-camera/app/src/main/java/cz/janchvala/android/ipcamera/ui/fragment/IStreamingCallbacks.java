package cz.janchvala.android.ipcamera.ui.fragment;

/**
 * Streaming callbacks are handled in ready to connect fragment.
 * <p/>
 * Created by jan on 13.03.2015.
 */
public interface IStreamingCallbacks {
    /**
     * called when streaming has started.
     */
    void onStreamingStarted();

    /**
     * called when streaming has ended.
     */
    void onStreamingStopped();
}
