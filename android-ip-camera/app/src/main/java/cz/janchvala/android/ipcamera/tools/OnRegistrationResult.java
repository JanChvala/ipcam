package cz.janchvala.android.ipcamera.tools;

/**
 * Callback interface used after GCM registration.
 * <p/>
 * Created by xchval01 on 21.04.2015.
 */
public interface OnRegistrationResult {
    /**
     * called when device was successfully registered to GCM.
     *
     * @param registrationId registration ID for GCM
     */
    void onRegistrationSuccess(String registrationId);

    /**
     * called when registration to GCM failed.
     */
    void onRegistrationFailed();
}
