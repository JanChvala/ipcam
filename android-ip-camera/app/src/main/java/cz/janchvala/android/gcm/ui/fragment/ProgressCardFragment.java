package cz.janchvala.android.gcm.ui.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.widget.ProgressView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.tools.RobotoTools;
import cz.janchvala.android.ipcamera.tools.ViewTools;

/**
 * Created by jan on 13.03.2015.
 */
@EFragment(R.layout.fragment_progress_card)
public class ProgressCardFragment extends Fragment {

    @ViewById(R.id.tv_fragment_progress_card_title)
    TextView vTitle;

    @ViewById(R.id.tv_fragment_progress_card_description)
    TextView vDescription;

    @ViewById(R.id.iv_fragment_progress_card_img)
    ImageView vImage;

    @ViewById(R.id.cpb_fragment_progress_card_progress_indicator)
    ProgressView vProgressBar;

    String title = "", description = "";
    int imgRes = 0;
    private boolean showProgressBar = true;
    private View.OnClickListener imageClickListener;


    public ProgressCardFragment() {
    }

    @AfterViews
    void setupViews() {
        ViewTools.setTypeface(RobotoTools.getRobotoTypeFace(RobotoTools.ROBOTO_MEDIUM), vTitle);
        ViewTools.setTypeface(RobotoTools.getRobotoTypeFace(RobotoTools.ROBOTO_REGULAR), vDescription);

        setTitle(title);
        setDescription(description);
        setImgRes(imgRes);
        showProgressAsync(showProgressBar);
        setImageClickListener(imageClickListener);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setTitle(String title) {
        this.title = title;
        if (vTitle != null) {
            vTitle.setText(title);
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setDescription(String description) {
        this.description = description;
        if (vDescription != null) {
            vDescription.setText(description);
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
        if (vImage != null) {
            vImage.setImageResource(imgRes);
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setTitle(int titleRes) {
        setTitle(getString(titleRes));
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setDescription(int descriptionRes) {
        setDescription(getString(descriptionRes));
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showProgressAsync(boolean show) {
        this.showProgressBar = show;
        if (vProgressBar != null) {
            vProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void setImageClickListener(View.OnClickListener callback) {
        this.imageClickListener = callback;
        if (vImage != null) {
            vImage.setOnClickListener(callback);
        }
    }


}
