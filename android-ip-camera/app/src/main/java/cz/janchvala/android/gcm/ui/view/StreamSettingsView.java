package cz.janchvala.android.gcm.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.preferences.IPcamPreferences_;

/**
 * Created by jan on 13.03.2015.
 */
@EViewGroup(R.layout.view_stream_settings)
public class StreamSettingsView extends LinearLayout implements Spinner.OnItemSelectedListener, Slider.OnPositionChangeListener {

    @ViewById(R.id.et_view_stream_settings_server_url)
    EditText etServerUrl;

    @ViewById(R.id.s_view_stream_settings_min_resolution)
    Spinner minResolution;

    @ViewById(R.id.s_view_stream_settings_max_resolution)
    Spinner maxResolution;

    @ViewById(R.id.sl_view_stream_settings_bandwidth)
    Slider bandwidth;

    @ViewById(R.id.sl_view_stream_settings_min_frame_rate)
    Slider minFrameRate;

    @ViewById(R.id.tv_view_stream_settings_bandwidth_label)
    TextView minBandwidthLabel;

    @ViewById(R.id.tv_view_stream_settings_frame_rate_label)
    TextView minFrameRateLabel;

    @Pref
    IPcamPreferences_ prefs;

    ArrayList<String> resolutions = Lists.newArrayList("320:180", "320:240", "640:480", "960:720", "1280:720", "1920:1080");

    public StreamSettingsView(Context context) {
        super(context);
    }

    public StreamSettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StreamSettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void initValues() {
        String minRes = String.format("%s:%s", prefs.streamMinWidth().get(), prefs.streamMinHeight().get());
        String maxRes = String.format("%s:%s", prefs.streamMaxWidth().get(), prefs.streamMaxHeight().get());

        etServerUrl.setText(prefs.serverUrl().get());

        minResolution.setAdapter(new ArrayAdapter<>(getContext(),R.layout.spinner_view, resolutions));
        minResolution.setSelection(resolutions.indexOf(minRes));
        minResolution.setOnItemSelectedListener(this);

        maxResolution.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_view, resolutions));
        maxResolution.setSelection(resolutions.indexOf(maxRes));
        maxResolution.setOnItemSelectedListener(this);

        bandwidth.setValue((float) prefs.streamBandWidth().get(), false);
        bandwidth.setOnPositionChangeListener(this);
        setBandwidthLabel();

        minFrameRate.setValue((float) prefs.streamMinFrameRate().get(), false);
        minFrameRate.setOnPositionChangeListener(this);
        setFrameRateLabel();
    }

    private int getResolution(int index, int part){
        String parts[] = resolutions.get(index).split(":");
        return Integer.parseInt(parts[part]);
    }

    public String getServerUrl() {
        return etServerUrl.getText().toString();
    }

    public int getMinWidth(){
        return getResolution(minResolution.getSelectedItemPosition(), 0);
    }

    public int getMinHeight(){
        return getResolution(minResolution.getSelectedItemPosition(), 1);
    }

    public int getMaxWidth(){
        return getResolution(maxResolution.getSelectedItemPosition(), 0);
    }

    public int getMaxHeight(){
        return getResolution(maxResolution.getSelectedItemPosition(), 1);
    }

    public int getBandwidth(){
        return Math.round(bandwidth.getValue());
    }

    public int getMinFrameRate(){
        return Math.round(minFrameRate.getValue());
    }

    private void setBandwidthLabel()
    {
        minBandwidthLabel.setText(getContext().getString(R.string.bandwidth_label, getBandwidth()));
    }

    private void setFrameRateLabel()
    {
        minFrameRateLabel.setText(getContext().getString(R.string.framerate_label, getMinFrameRate()));
    }

    @Override
    public void onItemSelected(Spinner spinner, View view, int position, long l) {
        if (spinner.equals(minResolution)) {
            if(maxResolution.getSelectedItemPosition() < position){
                maxResolution.setSelection(position);
            }
        } else {
            if(minResolution.getSelectedItemPosition() > position){
                minResolution.setSelection(position);
            }
        }
    }

    @Override
    public void onPositionChanged(Slider slider, boolean b, float v, float v1, int i, int i1) {
        if (slider.equals(bandwidth)) {
            setBandwidthLabel();
        } else {
            setFrameRateLabel();
        }
    }
}
