package com.troop.freedcam.ui.menu.themes.classic.shutter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockHandler implements View.OnClickListener, I_ParametersLoaded, AbstractModeParameter.I_ModeParameterEvent
{
    View activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    TextView textView;
    AppSettingsManager appSettingsManager;
    AbstractModeParameter exposureLock;
    ImageView view;
    Bitmap[] bitmaps;

    private static String TAG = ExposureLockHandler.class.getSimpleName();

    public ExposureLockHandler(View activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        this.view = (ImageView) activity.findViewById(R.id.imageView_exposurelock);
        view.setVisibility(View.GONE);
        view.setClickable(true);
        view.setOnClickListener(this);
        bitmaps = new Bitmap[2];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), getThemeAEButton()[0]);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(),getThemeAEButton()[1]);
        bitmaps[1] = front;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

    }

    private int[] getThemeAEButton()
    {
        int a;
        int b;

        String theme = appSettingsManager.GetTheme();

        if (theme.equals("Ambient")){
          a = R.drawable.button_expolockfalse; b = R.drawable.button_expolocktrue;}
       else if (theme.equals("Classic")){
            a = R.drawable.button_expolockfalse; b = R.drawable.button_expolocktrue;}

       else if (theme.equals("Material")){
            a = R.drawable.ic_ae_lock_off; b = R.drawable.ic_ae_lock_on;}

       else if (theme.equals("Minimal")){
            a = R.drawable.minimal_ui_ae_off; b = R.drawable.minimal_ui_ae_on;}

        else if (theme.equals("Nubia")){
            a = R.drawable.button_expolockfalse; b = R.drawable.button_expolocktrue;}
        else
        {
            a = R.drawable.minimal_ui_ae_off; b = R.drawable.minimal_ui_ae_on;}
        int [] ab = {a,b};
        return ab;
    }

    @Override
    public void ParametersLoaded()
    {
        if (cameraUiWrapper != null)
        {
            if (cameraUiWrapper.camParametersHandler.ExposureLock != null)
                exposureLock = cameraUiWrapper.camParametersHandler.ExposureLock;

            if (cameraUiWrapper.camParametersHandler != null && cameraUiWrapper.camParametersHandler.FocusMode != null)
                cameraUiWrapper.camParametersHandler.FocusMode.addEventListner(onFocusmodeChanged);

            if (exposureLock != null && exposureLock.IsSupported() && cameraUiWrapper.camParametersHandler.FocusMode != null && !cameraUiWrapper.camParametersHandler.FocusMode.GetValue().contains("continuous")) {

                exposureLock.addEventListner(this);
                String val = exposureLock.GetValue();
                view.setVisibility(View.VISIBLE);
                setBitmap(val);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        if (exposureLock != null && exposureLock.IsSupported()) {
            String toSet = "";
            if (exposureLock.GetValue().equals("true")) {
                toSet = "false";
            } else
                toSet = "true";

            Log.d(TAG, "set to: " + toSet);
            exposureLock.SetValue(toSet, true);
            setBitmap(toSet);
        }
    }

    private void setBitmap(final String value)
    {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (value.equals("true"))
                    view.setImageBitmap(bitmaps[1]);
                else
                    view.setImageBitmap(bitmaps[0]);
            }
        });

    }

    @Override
    public void onValueChanged(String val)
    {
        if (exposureLock != null && exposureLock.IsSupported())
            exposureLock.SetValue(val, true);
        setBitmap(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    AbstractModeParameter.I_ModeParameterEvent onFocusmodeChanged = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            if (exposureLock != null && exposureLock.IsSupported()) {
                exposureLock.SetValue("false", true);
                setBitmap("false");
                if (!val.contains("continuous"))
                    view.setVisibility(View.VISIBLE);
                else {
                    view.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }
    };


}
