package cz.janchvala.android.ipcamera.network.rest;

import cz.janchvala.android.ipcamera.network.bean.DeviceRegistration;
import cz.janchvala.android.ipcamera.network.bean.Token;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Server backend API interface.
 * <p/>
 * Created by xchval01 on 20. 2. 2015.
 */
public interface IGcmRestAPI {

    /**
     * Method is called when device gains registrationID for GCM. This ID has to be saved on server
     * so it can be later used to request device stream.
     *
     * @return Token for device access
     */
    @POST("/api/devices/")
    Token registerDevice(@Body DeviceRegistration deviceRegistration);
}
