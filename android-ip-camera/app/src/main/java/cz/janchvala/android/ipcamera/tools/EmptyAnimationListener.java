package cz.janchvala.android.ipcamera.tools;

import android.animation.Animator;

/**
 * This class is only used when we do not need all functions from AnimatorListener interface.
 * <p/>
 * Created by xchval01 on 13.03.2015.
 */
public abstract class EmptyAnimationListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}
