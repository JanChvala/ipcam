package cz.janchvala.android.ipcamera.network.bean;

/**
 * The DeviceRegistration is object for device registration which is send to server. It contains GCM
 * registration ID so that the server can contact the device.
 * <p/>
 * Created by xchval01 on 13.03.2015.
 */
public class DeviceRegistration {

    private String name;
    private String gcmRegistrationId;

    public DeviceRegistration() {
    }

    public DeviceRegistration(String name, String gcmRegistrationId) {
        this.name = name;
        this.gcmRegistrationId = gcmRegistrationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGcmRegistrationId() {
        return gcmRegistrationId;
    }

    public void setGcmRegistrationId(String gcmRegistrationId) {
        this.gcmRegistrationId = gcmRegistrationId;
    }
}
