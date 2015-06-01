package cz.janchvala.android.ipcamera.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.common.base.Strings;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.UUID;

import cz.janchvala.android.ipcamera.R;
import cz.janchvala.android.ipcamera.helpers.IntentHelper;
import cz.janchvala.android.ipcamera.network.rest.ApplicationServerRestClient;
import cz.janchvala.android.ipcamera.network.rest.RegistrationCallbacks;
import cz.janchvala.android.ipcamera.preferences.IPCameraPreferences_;
import cz.janchvala.android.ipcamera.tools.GCMProvider;
import cz.janchvala.android.ipcamera.tools.OnRegistrationResult;
import cz.janchvala.android.ipcamera.ui.fragment.ProgressCardFragment;
import cz.janchvala.android.ipcamera.ui.fragment.ProgressCardFragment_;
import cz.janchvala.android.ipcamera.ui.fragment.ReadyToConnectCardFragment;
import cz.janchvala.android.ipcamera.ui.fragment.ReadyToConnectCardFragment_;
import cz.janchvala.android.ipcamera.ui.view.ChangePasswordView;
import cz.janchvala.android.ipcamera.ui.view.ChangePasswordView_;
import cz.janchvala.android.ipcamera.ui.view.StreamSettingsView;
import cz.janchvala.android.ipcamera.ui.view.StreamSettingsView_;

/**
 * Startup activity handles ChangeLog dialog and setup wizard dialog to be shown.
 * <p/>
 * Created by jan on 13.12.2014.
 */
@Fullscreen
@OptionsMenu(R.menu.menu_startup)
@EActivity(R.layout.activity_startup)

public class StartupActivity
        extends ToolbarBaseActivity
        implements DialogInterface.OnCancelListener, OnRegistrationResult, ReadyToConnectCardFragment.Callbacks,
        View.OnClickListener, RegistrationCallbacks, DialogInterface.OnDismissListener, Switch.OnCheckedChangeListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Pref
    IPCameraPreferences_ ipCamPreferences;

    @Bean
    ApplicationServerRestClient gcmClient;

    @Bean
    GCMProvider gcmProvider;

    @ViewById(R.id.fl_activity_start_card_container)
    FrameLayout frameContainer;

    @OptionsMenuItem(R.id.mi_toolbar_switch_action)
    MenuItem miEnableSwitch;

    ProgressCardFragment fProgress;
    ReadyToConnectCardFragment fReadyToStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    protected void setup() {
        mToolbarMain.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        mToolbarMain.setTitle(R.string.app_name);
        mToolbarMain.setSubtitle(R.string.app_subtitle);
        onStreamActiveChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        Switch sw = (Switch) miEnableSwitch.getActionView().findViewById(R.id.toolbar_switch_action);
        sw.setChecked(ipCamPreferences.streamingEnabled().get());
        sw.setOnCheckedChangeListener(this);
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEverythingIsOkAsync();
    }

    @UiThread(delay = 1000)
    protected void checkEverythingIsOkAsync() {
        String regId = ipCamPreferences.gcmRegistrationId().get();
        if (!checkPlayServices()) {
            // inform user that play services are necessary and they are missing
            logi("Play services are missing");
            showError("Play services missing");
        } else if (Strings.isNullOrEmpty(regId)) {
            // register for GCM
            logi("We are trying to register for GCM service.");
            showProgressStartLoading(R.string.getting_things_ready, R.string.google_cloud_messaging, R.drawable.ic_cloud_queue_grey600_48dp);
            gcmProvider.registerAsync(new String[]{ipCamPreferences.gcmSenderId().get()}, this);
        } else if (Strings.isNullOrEmpty(ipCamPreferences.accessToken().get())) {
            logi("We already have the registration id but server does not know about us.");
            showProgressStartLoading(R.string.getting_things_ready, R.string.registration, R.drawable.ic_cloud_done_grey600_48dp);
            gcmClient.sendRegistrationIdToBackendAsync(regId, this);
        } else if (Strings.isNullOrEmpty(ipCamPreferences.accessPassword().get())) {
            logi("Password has to be set.");
            showProgressStartLoading(R.string.getting_things_ready, R.string.password, R.drawable.ic_lock_grey600_48dp);
            showChangePasswordDialog();
        } else {
            logi("Application is ready to stream.");
            logi("We already have the registration id. " + regId);
            showReadyToStreamFragment();
        }
    }

    /**
     * Method checks whether proper version of Google Play Services are enabled or not.
     * If they are not it tries to recover from the error by showing proper dialog from Google Play
     * Service Utilities.
     *
     * @return false if the services are not supported, true otherwise
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            showProgressStartLoading(R.string.getting_things_ready, R.string.google_play_services, R.drawable.common_ic_googleplayservices);
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // we can trz to update Google Play Services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST, this).show();
            } else {
                // there is no other way to install Google Play Services
                logi("This device is not supported.");
            }
            return false;
        }
        // we are good to go
        return true;
    }

    /**
     * initialize and add the ready to stream fragment.
     */
    private void showReadyToStreamFragment() {
        if (fReadyToStream == null) {
            fReadyToStream = new ReadyToConnectCardFragment_.FragmentBuilder_().build();
            fReadyToStream.setCallback(this);
        }

        fReadyToStream.refreshStreamInformation();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.card_slide_down, R.animator.card_slide_up)
                .replace(R.id.fl_activity_start_card_container, fReadyToStream)
                .commit();
    }

    /**
     * showing the progress fragment.
     *
     * @param title       progress title
     * @param description progress description
     * @param image       image for fragment
     */
    private void showProgressStartLoading(@StringRes int title, @StringRes int description, @DrawableRes int image) {
        setupProgressFragment(title, getString(description), image, null, true);
    }

    /**
     * showing error in progress fragment.
     *
     * @param message error message
     */
    private void showError(String message) {
        setupProgressFragment(R.string.error, message, R.drawable.ic_refresh_grey600_48dp, this, false);
    }

    /**
     * setting up the progress fragment.
     *
     * @param title       message
     * @param description message
     * @param image       progress image
     * @param listener    listener for click event
     * @param progress    progress or not
     */
    @UiThread(propagation = UiThread.Propagation.REUSE)
    protected void setupProgressFragment(@StringRes int title, String description, @DrawableRes int image, View.OnClickListener listener, boolean progress) {
        boolean show = fProgress == null;
        if (show) {
            fProgress = new ProgressCardFragment_.FragmentBuilder_().build();
        }

        fProgress.setTitle(getString(title));
        fProgress.setDescription(description);
        fProgress.setImgRes(image);
        fProgress.setImageClickListener(listener);
        fProgress.showProgressAsync(progress);

        if (show) {
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.card_slide_down, R.animator.card_slide_up)
                    .replace(R.id.fl_activity_start_card_container, fProgress)
                            //.addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Called when enable streaming checkbox changes the value.
     */
    protected void onStreamActiveChange() {
        checkEverythingIsOkAsync();
        int colorResId = R.color.toolbarPrimaryColor;
        if (!ipCamPreferences.streamingEnabled().get()) {
            colorResId = R.color.disabled_straming_background;
        }
        frameContainer.setBackgroundColor(getResources().getColor(colorResId));
    }

    /**
     * Handle option item start streaming click.
     */
    @OptionsItem(R.id.mi_activity_startup_start_stream)
    void onStartStreaming() {
        IntentHelper.startStreaming(this, ipCamPreferences, false);
    }

    /**
     * Show about page on menu item click.
     */
    @OptionsItem(R.id.mi_activity_startup_about)
    void showAboutDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.about)
                .customView(R.layout.about_content_view, true)
                .positiveText(R.string.close)
                .positiveColorRes(R.color.toolbarPrimaryColor)
                .show();
    }

    /**
     * Show change password dialog on menu item click.
     */
    @OptionsItem(R.id.mi_activity_startup_change_password)
    void showChangePasswordDialog() {
        final ChangePasswordView view = ChangePasswordView_.build(this);
        view.setInitPassword(ipCamPreferences.accessPassword().get());

        new MaterialDialog.Builder(this)
                .title(R.string.about)
                .theme(Theme.LIGHT)
                .customView(view, true)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.toolbarPrimaryColor)
                .negativeText(R.string.random)
                .negativeColorRes(R.color.toolbarAccentColor)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String password = view.getPassword();
                        if (Strings.isNullOrEmpty(password) || password.length() < 6) {
                            toast("Password has to contain at least six characters.");
                        } else {
                            ipCamPreferences.accessPassword().put(password);
                            checkEverythingIsOkAsync();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // generate password
                        ipCamPreferences.accessPassword().put(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                        checkEverythingIsOkAsync();
                        dialog.dismiss();
                    }
                })
                .dismissListener(this)
                .show();
    }

    /**
     * Showing stream settings dialog when clicking menu item.
     */
    @OptionsItem(R.id.mi_activity_startup_change_settings)
    void showStreamSettingsDialog() {
        final StreamSettingsView view = StreamSettingsView_.build(this);

        new MaterialDialog.Builder(this)
                .title(R.string.stream_setting)
                .theme(Theme.LIGHT)
                .customView(view, true)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.toolbarPrimaryColor)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.toolbarAccentColor)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ipCamPreferences.streamMinWidth().put(view.getMinWidth());
                        ipCamPreferences.streamMaxWidth().put(view.getMaxWidth());
                        ipCamPreferences.streamMinHeight().put(view.getMinHeight());
                        ipCamPreferences.streamMaxHeight().put(view.getMaxHeight());

                        ipCamPreferences.streamMinFrameRate().put(view.getMinFrameRate());

                        ipCamPreferences.streamBandWidth().put(view.getBandwidth());
                        if (!ipCamPreferences.serverUrl().get().equals(view.getServerUrl())) {
                            ipCamPreferences.serverUrl().put(view.getServerUrl());
                            ipCamPreferences.accessToken().put("");
                            checkEverythingIsOkAsync();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .dismissListener(this)
                .show();
    }

    /**
     * Registration for to application server.
     *
     * @param error     error code
     * @param exception exception to log
     */
    @Override
    public void onRegistrationFinished(@ApplicationServerRestClient.RegistrationErrors int error, Throwable exception) {
        if (error == ApplicationServerRestClient.ERROR_NONE) {
            logi("Registration id was sent to server.");
            checkEverythingIsOkAsync();
        } else if (error == ApplicationServerRestClient.ERROR_NOT_SUCCESSFUL) {
            logi("Registration ID could not be sent to server.");
            String errorMsg = exception.getMessage();
            loge(errorMsg, exception);
            showError(errorMsg);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // This is called when setting password is dismissed
        // if the password is shorter than 6 chars we show it again
        if (ipCamPreferences.accessPassword().get().length() < 6) {
            showChangePasswordDialog();
        }
        dialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        logi("User cancelled google play services update..");
        int tryCount = ipCamPreferences.registrationCountGooglePlay().get();
        if (tryCount >= 2) {
            ipCamPreferences.registrationCountGooglePlay().put(0);
            finish();
        } else {
            checkEverythingIsOkAsync();
        }
    }

    @Override
    public void onRegistrationSuccess(String registrationId) {
        // registration ID to server over HTTP. The request should be authenticated if app is using accounts.
        checkEverythingIsOkAsync();
    }

    @Override
    public void onRegistrationFailed() {
        toast("Error registering device for GCM was not successful.");
        int tryCount = ipCamPreferences.registrationCountGcm().get();
        if (tryCount >= 2) {
            ipCamPreferences.registrationCountGcm().put(0);
            finish();
        } else {
            checkEverythingIsOkAsync();
        }
    }

    @Override
    public void onShareClick() {
        startActivity(IntentHelper.createShareIntent(ipCamPreferences));
    }

    @Override
    public void onStopStreamingClick() {
    }

    @Override
    public void onClick(View v) {
        checkEverythingIsOkAsync();
    }

    @Override
    public void onCheckedChanged(Switch aSwitch, boolean b) {
        ipCamPreferences.streamingEnabled().put(b);
        SnackBar.make(this)
                .applyStyle(R.style.Material_Widget_SnackBar_Tablet)
                .text(b ? R.string.streaming_enabled : R.string.streaming_disabled)
                .singleLine(true)
                .duration(2000)
                .show(this);
        onStreamActiveChange();
    }
}
