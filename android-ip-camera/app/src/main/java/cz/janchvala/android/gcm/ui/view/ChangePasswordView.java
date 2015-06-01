package cz.janchvala.android.gcm.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cz.janchvala.android.ipcamera.R;

/**
 * Created by jan on 13.03.2015.
 */
@EViewGroup(R.layout.view_change_password)
public class ChangePasswordView extends LinearLayout implements Switch.OnCheckedChangeListener {

    @ViewById(R.id.flfl_view_change_password_input)
    EditText floatLabel;

    @ViewById(R.id.tv_view_change_password_show_password)
    TextView showPassword;

    @ViewById(R.id.sv_view_change_password_show_password_switch)
    Switch showPasswordSwitch;

    String initPassword = null;

    public ChangePasswordView(Context context) {
        super(context);
    }

    public ChangePasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChangePasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public ChangePasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setInitPassword(String initPassword) {
        this.initPassword = initPassword;
        if (!Strings.isNullOrEmpty(initPassword) && floatLabel != null) {
            EditText etPassword = floatLabel;
            etPassword.setText(initPassword);
            onCheckedChanged(showPasswordSwitch, showPasswordSwitch.isChecked());
        }
    }

    @AfterViews
    void setupViews() {
        showPasswordSwitch.setOnCheckedChangeListener(this);
        floatLabel.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setInitPassword(initPassword);
    }

    @Click(R.id.tv_view_change_password_show_password)
    void onTextShowPasswordClick() {
        boolean toggled = !showPasswordSwitch.isChecked();
        showPasswordSwitch.setChecked(toggled);
        onCheckedChanged(showPasswordSwitch, toggled); // need to call explicitly (setChecked does not fire the callback)
    }

    public String getPassword() {
        return Strings.nullToEmpty(floatLabel.getText().toString());
    }

    @Override
    public void onCheckedChanged(Switch aSwitch, boolean showPassword) {
        EditText etPassword = floatLabel;
        int inputType = InputType.TYPE_CLASS_TEXT;

        if (!showPassword) {
            inputType |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }

        etPassword.setInputType(inputType);
        etPassword.setSelection(etPassword.getText().length());
    }
}
