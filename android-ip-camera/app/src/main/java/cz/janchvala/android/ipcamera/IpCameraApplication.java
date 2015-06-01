package cz.janchvala.android.ipcamera;

import android.app.Application;

import com.facebook.stetho.Stetho;

import org.androidannotations.annotations.EApplication;

/**
 * Base application class holding the context. Initializes Stetho instance.
 * <p/>
 * Created by xchval01 on 7.11.2014.
 */
@EApplication
public class IpCameraApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // initialize Stetho if this is debug release
        if (BuildConfig.DEBUG) {
            Stetho.InitializerBuilder builder = Stetho.newInitializerBuilder(this);
            builder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
            Stetho.initialize(builder.build());
        }
    }
}
