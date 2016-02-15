package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import troop.com.themesample.R;

/**
 * Created by Ar4eR on 05.02.16.
 */
public class MenuItemAEB extends LinearLayout {
    Button plus;
    Button minus;
    EditText editText;
    Context context;

    private int min = -10;
    private int max = 10;
    private final int step = 1;
    int current;
    AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;
    String settingsname;


    public MenuItemAEB(Context context) {
        super(context);
        init(context, null);
    }

    public MenuItemAEB(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null);
    }
    protected void init(Context context, AttributeSet attributeSet)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_childs_number, this);
        this.plus = (Button)findViewById(R.id.button_plus);
        this.minus = (Button)findViewById(R.id.button_minus);
        this.editText = (EditText)findViewById(R.id.editText_number);
        this.plus.setClickable(true);
        this.minus.setClickable(true);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((current - step) >= min)
                    current -= step;
                setCurrent(current);
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current + step <= max)
                    current += step;
                setCurrent(current);

            }
        });

    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper !=  null && cameraUiWrapper.camParametersHandler != null && cameraUiWrapper.camParametersHandler.ManualExposure != null) {
            min = cameraUiWrapper.camParametersHandler.ManualExposure.GetMinValue();
            max = cameraUiWrapper.camParametersHandler.ManualExposure.GetMaxValue();
        }

    }

    public void setCurrent(int current) {
        String tempcurrent = String.valueOf(current);
        appSettingsManager.setString(settingsname, tempcurrent);
        if (cameraUiWrapper != null && cameraUiWrapper.camParametersHandler != null && cameraUiWrapper.camParametersHandler.captureBurstExposures != null) {
            if ((cameraUiWrapper.camParametersHandler.captureBurstExposures.IsSupported())) {
                //String cbev[] = cameraUiWrapper.camParametersHandler.captureBurstExposures.GetValue().split(",");
                //if (cbev == null && cbev.equals(""))
                //cbev = "-10,0,10".split(",");
        /*if (settingsname.contains("1")){
            //if (cbev != null && !cbev.equals(""))
                //cameraUiWrapper.camParametersHandler.captureBurstExposures.SetValue(current+","+cbev[1]+","+cbev[2],true);
            //cameraUiWrapper.camParametersHandler.aeb1.SetValue(tempcurrent,true);
            appSettingsManager.setString("aeb1", tempcurrent);
        }
        else if (settingsname.contains("2")){
            //if (cbev != null && !cbev.equals(""))
                //cameraUiWrapper.camParametersHandler.captureBurstExposures.SetValue(cbev[0]+","+current+","+cbev[2],true);
            cameraUiWrapper.camParametersHandler.aeb2.SetValue(tempcurrent,true);
        }
        else{
            //if (cbev != null && !cbev.equals(""))
                //cameraUiWrapper.camParametersHandler.captureBurstExposures.SetValue(cbev[0]+","+cbev[1]+","+current,true);
            cameraUiWrapper.camParametersHandler.aeb3.SetValue(tempcurrent,true);
        }*/
                cameraUiWrapper.camParametersHandler.captureBurstExposures.SetValue("on", true);
            }


            editText.setText(String.valueOf(current));
        }
    }

    public void SetStuff(AppSettingsManager appSettingsManager, String settingvalue) {
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingvalue;

        String exp = this.appSettingsManager.getString(settingsname);
        if (exp == null || exp.equals("")) {
            exp = "0";
            current = Integer.parseInt(exp);
            setCurrent(current);
        }
        editText.setText(exp);
        current = Integer.parseInt(exp);
    }
}
