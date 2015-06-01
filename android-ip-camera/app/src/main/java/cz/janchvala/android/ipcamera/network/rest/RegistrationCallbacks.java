package cz.janchvala.android.ipcamera.network.rest;

/**
 * Callback to get notified when registration has finished.
 */
public interface RegistrationCallbacks {
    void onRegistrationFinished(@ApplicationServerRestClient.RegistrationErrors int error, Throwable exception);
}
