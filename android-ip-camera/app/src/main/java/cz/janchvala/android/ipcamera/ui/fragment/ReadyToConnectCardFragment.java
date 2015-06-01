package cz.janchvala.android.ipcamera.ui.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.widget.FloatingActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.concurrent.atomic.AtomicBoolean;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import cz.janchvala.android.ipcamera.tools.EmptyAnimationListener;
import cz.janchvala.android.ipcamera.tools.RobotoTools;
import cz.janchvala.android.ipcamera.tools.TypeFaceTools;
import cz.janchvala.android.ipcamera.tools.ViewTools;

/**
 * This fragment is shown when successfully registered for GCM and application server.
 * <p/>
 * Created by jan on 13.03.2015.
 */
@EFragment(R.layout.fragment_ready_to_connect_card)
public class ReadyToConnectCardFragment extends Fragment implements View.OnClickListener, IStreamingCallbacks {

    /**
     * Share and stop streaming callbacks.
     */
    public interface Callbacks {
        void onShareClick();

        void onStopStreamingClick();
    }

    @ViewById(R.id.cv_fragment_base_card_card_view)
    CardView vCardContainer;

    @ViewById(R.id.tv_fragment_ready_to_connect_title)
    TextView vTitle;

    @ViewById(R.id.tv_fragment_ready_to_connect_description)
    TextView vDescription;

    @ViewById(R.id.iv_fragment_ready_to_connect_img)
    ImageView vImage;

    @ViewById(R.id.fab_fragment_ready_to_connect_progress_button)
    FloatingActionButton vFab;

    @Bean
    TypeFaceTools ttfTools;

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    Callbacks mCallback;

    private AtomicBoolean isStreaming = new AtomicBoolean(false);

    public ReadyToConnectCardFragment() {
    }

    public void setCallback(Callbacks callbacks) {
        this.mCallback = callbacks;
    }

    @AfterViews
    void setupViews() {
        ViewTools.setTypeface(RobotoTools.getRobotoTypeFace(RobotoTools.ROBOTO_MEDIUM), vTitle);
        ViewTools.setTypeface(RobotoTools.getRobotoTypeFace(RobotoTools.ROBOTO_REGULAR), vDescription);

        vTitle.setText(R.string.getting_things_ready);
        vDescription.setText(R.string.registration);

        vFab.setOnClickListener(this);

        vTitle.setText(ipCamPreferences.streamingEnabled().get() ? R.string.ready_to_connect : R.string.streaming_disabled);
        vDescription.setText("U: " + ipCamPreferences.accessToken().get() + " | P: " + ipCamPreferences.accessPassword().get());
        vImage.setImageResource(R.drawable.ic_cloud_done_grey600_48dp);
    }

    @Override
    public void onClick(View v) {
        if (mCallback != null) {
            if (isStreaming.get()) {
                mCallback.onStopStreamingClick();
            } else {
                mCallback.onShareClick();
            }
        }
    }

    @Override
    @UiThread
    public void onStreamingStarted() {
        isStreaming.set(true);

        final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(vFab, "colorNormal", new ArgbEvaluator(), getResources().getColor(R.color.toolbarAccentColor), getResources().getColor(R.color.material_red_700));
        backgroundColorAnimator.setDuration(500);
        backgroundColorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        backgroundColorAnimator.addListener(new EmptyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Drawable d;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    d = getResources().getDrawable(R.drawable.ic_stop_white_24dp, null);
                } else {
                    d = getResources().getDrawable(R.drawable.ic_stop_white_24dp);
                }
                vFab.setIcon(d, false);
            }
        });
        backgroundColorAnimator.start();

        ViewCompat.animate(vCardContainer)
                .translationY(-2000)
                .alpha(0)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        vCardContainer.setVisibility(View.INVISIBLE);
                    }
                });

        // TODO: change share button to stop button and hide the card
    }

    @Override
    @UiThread
    public void onStreamingStopped() {
        isStreaming.set(false);

        final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(vFab, "colorNormal", new ArgbEvaluator(), getResources().getColor(R.color.material_red_700), getResources().getColor(R.color.toolbarAccentColor));
        backgroundColorAnimator.setDuration(500);
        backgroundColorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        backgroundColorAnimator.addListener(new EmptyAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Drawable d;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    d = getResources().getDrawable(R.drawable.ic_share_white_24dp, null);
                } else {
                    d = getResources().getDrawable(R.drawable.ic_share_white_24dp);
                }
                vFab.setIcon(d, false);
            }
        });
        backgroundColorAnimator.start();

        ViewCompat.animate(vCardContainer)
                .translationY(0)
                .alpha(1)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        vCardContainer.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void refreshStreamInformation() {
        if (vTitle != null) {
            setupViews();
        }
    }
}
