package cz.janchvala.android.ipcamera.tools;

import android.graphics.Typeface;
import android.support.v4.util.LruCache;

import org.androidannotations.annotations.EBean;

import cz.janchvala.android.ipcamera.IpCameraApplication_;

/**
 * Class provides way of getting type face from assets and cache them.
 * <p/>
 * Created by xchval01 on 7.11.2014.
 */
@EBean(scope = EBean.Scope.Singleton)
public class TypeFaceTools {

    /**
     * LruCache for loaded typefaces.
     */
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<>(6);

    /**
     * Getter for Typeface.
     *
     * @param typefaceName typefaceName of typeface
     * @return Typeface
     */
    public Typeface getTypeFace(String typefaceName) {
        Typeface typeface = sTypefaceCache.get(typefaceName);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(IpCameraApplication_.getInstance().getAssets(), typefaceName);

            // Cache the loaded Typeface
            sTypefaceCache.put(typefaceName, typeface);
        }

        return typeface;
    }
}
