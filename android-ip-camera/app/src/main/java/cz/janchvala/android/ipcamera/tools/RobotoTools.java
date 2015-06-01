package cz.janchvala.android.ipcamera.tools;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cz.janchvala.android.ipcamera.IpCameraApplication_;

/**
 * helper class used to provide Roboto Type face specific paths in asset folder
 * <p/>
 * Created by xchval01 on 7.11.2014.
 */
public class RobotoTools {

    /**
     * enum of roboto fonts
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ROBOTO_MEDIUM, ROBOTO_REGULAR})
    public @interface RobotoPath {
    }

    public static final String ROBOTO_MEDIUM = "fonts/Roboto-Medium.ttf";
    public static final String ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";

    /**
     * getter for specific RobotoPath font.
     *
     * @param path one of roboto fonts
     * @return Typeface
     */
    public static Typeface getRobotoTypeFace(@NonNull @RobotoPath String path) {
        return TypeFaceTools_.getInstance_(IpCameraApplication_.getInstance()).getTypeFace(path);
    }
}
