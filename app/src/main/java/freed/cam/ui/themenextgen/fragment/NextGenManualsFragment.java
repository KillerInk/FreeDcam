package freed.cam.ui.themenextgen.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.FreedApplication;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.event.module.ModuleChangedEvent;
import freed.cam.ui.KeyPressedController;
import freed.cam.ui.themenextgen.layoutconfig.ManualGroupConfig;
import freed.cam.ui.themenextgen.layoutconfig.ManualItemConfig;
import freed.cam.ui.themenextgen.view.NextGenRotatingSeekbar;
import freed.cam.ui.themenextgen.view.button.ManualButtonInterface;
import freed.cam.ui.themenextgen.view.button.NextGenManualButton;
import freed.cam.ui.themenextgen.view.button.NextGenMfItem;
import freed.cam.ui.themesample.cameraui.AfBracketSettingsView;
import freed.cam.ui.themesample.cameraui.ManualButton;
import freed.cam.ui.themesample.cameraui.ManualFragment;
import freed.cam.ui.themesample.cameraui.childs.ManualButtonMF;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.views.pagingview.PagingViewTouchState;

@AndroidEntryPoint
public class NextGenManualsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, ModuleChangedEvent, CameraHolderEvent
{
    private int currentValuePos;

    private NextGenRotatingSeekbar seekbar;

    private ManualButtonInterface currentButton;

    private AfBracketSettingsView afBracketSettingsView;

    private LinearLayout manualItemsHolder;

    private final Handler handler = new Handler();

    private final String TAG = ManualFragment.class.getSimpleName();
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    PagingViewTouchState pagingViewTouchState;
    @Inject
    KeyPressedController keyPressedController;

    private HashMap<SettingKeys.Key, ManualButtonInterface> buttonHashMap;
    private List<SettingKeys.Key> supportedManuals;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.nextgen_cameraui_manual_fragment_rotatingseekbar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonHashMap = new HashMap<>();
        supportedManuals = new ArrayList<>();
        keyPressedController.setManualModeChangedEventListner(manualModeChangedEvent);
        seekbar = view.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setVisibility(View.GONE);

        manualItemsHolder = view.findViewById(R.id.manualItemsHolder);

        afBracketSettingsView = view.findViewById(R.id.manualFragment_afbsettings);
        afBracketSettingsView.setVisibility(View.GONE);
        cameraApiManager.addEventListner(this);
        cameraApiManager.addModuleChangedEventListner(this);
        onCameraOpenFinished();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraApiManager.removeEventListner(this);
        cameraApiManager.removeModuleChangedEventListner(this);
    }

    private void setCameraToUi(CameraWrapperInterface cameraUiWrapper)
    {
        if (manualItemsHolder == null)
            return;
        //rest views to init state
        manualItemsHolder.removeAllViews();
        buttonHashMap.clear();
        supportedManuals.clear();
        seekbar.setVisibility(View.GONE);
        if (currentButton != null) {
            currentButton.SetActive(false);
            currentButton = null;
        }
        afBracketSettingsView.setVisibility(View.GONE);


        if (cameraUiWrapper != null)
        {
            ParameterHandler parms = cameraUiWrapper.getParameterHandler();

            List<ManualItemConfig> group = new ManualGroupConfig().getManualGroup();
            for (ManualItemConfig m : group)
            {
                if (m.getKey() == SettingKeys.M_FOCUS)
                {
                    NextGenMfItem btn = NextGenMfItem.getInstance(getContext(),getContext().getString(R.string.font_manual_focus), (AbstractParameter) parms.get(SettingKeys.M_FOCUS));
                    btn.setOnClickListener(manualButtonClickListner);
                    manualItemsHolder.addView(btn);
                    buttonHashMap.put(SettingKeys.M_FOCUS,btn);
                    supportedManuals.add(SettingKeys.M_FOCUS);
                    setViewWidth(btn,25);
                }
                else
                {
                    if (m.getColor() == 0)
                    {
                        addNextGenButton(parms,m.getKey(), (String) m.getHeader());
                    }
                    else
                    {
                        addNextGenButton(parms,m.getKey(), (String) m.getHeader(), m.getColor());
                    }
                }
            }

            seekbar.setVisibility(View.GONE);
            if (cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_afbracket))
                    && currentButton instanceof NextGenMfItem
                    && seekbar.getVisibility() == View.VISIBLE)
                afBracketSettingsView.setVisibility(View.VISIBLE);
            else
                afBracketSettingsView.setVisibility(View.GONE);
            keyPressedController.setSupportedManualsModes(supportedManuals);
            if (currentButton != null)
                keyPressedController.setActiveKey(getKeyFromButton(currentButton));
            else if(supportedManuals.size() > 0)
                keyPressedController.setActiveKey(supportedManuals.get(0));
        }
    }

    private void addNextGenButton(ParameterHandler parms,SettingKeys.Key key, String stringid)
    {
        if (parms.get(key) != null) {
            NextGenManualButton btn = NextGenManualButton.getInstance(getContext(),stringid, (AbstractParameter) parms.get(key));
            btn.setOnClickListener(manualButtonClickListner);
            manualItemsHolder.addView(btn);
            buttonHashMap.put(key,btn);
            supportedManuals.add(key);
            if (key == SettingKeys.M_EXPOSURE_TIME)
                setViewWidth(btn,43);
            else if (key == SettingKeys.M_MANUAL_ISO)
                setViewWidth(btn,38);
            else if (key == SettingKeys.M_EXPOSURE_COMPENSATION)
                setViewWidth(btn,17);
            else
                setViewWidth(btn, 16);
        }
    }

    private void setViewWidth(NextGenManualButton button, int size)
    {
       /* LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) button.getLayoutParams();
        p.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
        button.setLayoutParams(p);*/
        button.setValueTextWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics()));
    }

    private void addNextGenButton(ParameterHandler parms,SettingKeys.Key key, String stringid,int color)
    {
        if (parms.get(key) != null) {
            NextGenManualButton btn = NextGenManualButton.getInstance(getContext(),stringid, (AbstractParameter) parms.get(key),color);
            btn.setOnClickListener(manualButtonClickListner);
            manualItemsHolder.addView(btn);
            buttonHashMap.put(key,btn);
            supportedManuals.add(key);
            setViewWidth(btn, 15);
        }
    }

    //######## ManualButton Stuff#####
    private final View.OnClickListener manualButtonClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (v instanceof ManualButton && ((NextGenManualButton)v).getParameter().getViewState() == AbstractParameter.ViewState.Disabled)
                return;
            //when same button gets clicked second time
            if(v == currentButton && seekbar.getVisibility() == View.VISIBLE)
            {
                //hideseekbar and set color back from button
                seekbar.setVisibility(View.GONE);
                currentButton.SetActive(false);
                afBracketSettingsView.setVisibility(View.GONE);
            }
            //if no button was active or a different was clicked
            else
            {
                if (seekbar.getVisibility() == View.GONE)
                    seekbar.setVisibility(View.VISIBLE);
                //when already a button is active disable it
                if (currentButton != null) {
                    currentButton.getParameter().removeOnPropertyChangedCallback(selectedParameterObserver);
                    currentButton.SetActive(false);
                }
                //set the returned view as active and fill seekbar
                currentButton = (ManualButtonInterface) v;
                currentButton.SetActive(true);
                keyPressedController.setActiveKey(getKeyFromButton(currentButton));
                currentButton.getParameter().addOnPropertyChangedCallback(selectedParameterObserver);

                if (currentButton instanceof ManualButtonMF && cameraApiManager.getCamera().getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_afbracket)))
                    afBracketSettingsView.setVisibility(View.VISIBLE);
                else
                    afBracketSettingsView.setVisibility(View.GONE);

                String[] vals = currentButton.getStringValues();
                if (vals == null || vals.length == 0) {
                    currentButton.SetActive(false);
                    seekbar.setVisibility(View.GONE);
                    Log.e(TAG, "Values returned from currentButton are NULL!");
                    return;
                }
                seekbar.SetStringValues(vals);
                seekbar.setProgress(currentButton.getCurrentItem(), false);
                currentValuePos = currentButton.getCurrentItem();
                Log.d(TAG, "CurrentvaluePos " + currentValuePos);

            }

        }
    };

    //#########################SEEKBAR STUFF#############################

    private SettingKeys.Key getKeyFromButton(ManualButtonInterface button)
    {
        for (Map.Entry entry : buttonHashMap.entrySet())
        {
            if (entry.getValue() == button)
                return (SettingKeys.Key) entry.getKey();
        }
        return SettingKeys.M_ZOOM;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        Log.d(TAG, "onProgressChanged:" + progress);
        currentValuePos = progress;
        try {
            currentButton.setValueToParameters(progress);
        }
        catch (NullPointerException ex)
        {}

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        currentButton.setValueToParameters(currentValuePos);
    }

    Observable.OnPropertyChangedCallback selectedParameterObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.viewState)
                onViewStateChanged(((AbstractParameter)sender).getViewState());
            if (propertyId == BR.intValue)
                onIntValueChanged(((AbstractParameter)sender).getIntValue());
            if (propertyId == BR.stringValues)
                onValuesChanged(((AbstractParameter)sender).getStringValues());
        }
    };

    private void onViewStateChanged(AbstractParameter.ViewState value) {
        switch (value)
        {
            case Visible:
                break;
            case Hidden:
                seekbar.setVisibility(View.GONE);
                currentButton.SetActive(false);
                break;
            case Disabled:
                seekbar.post(() -> {
                    seekbar.setVisibility(View.GONE);
                });
                break;
            case Enabled:
                seekbar.post(() -> {
                    seekbar.setVisibility(View.VISIBLE);
                });
                break;
        }
    }

    private void onIntValueChanged(int current)
    {
        if(!seekbar.IsAutoScrolling()&& !seekbar.IsMoving())
        {
            seekbar.setProgress(current, false);
        }
    }

    private void onValuesChanged(String[] values)
    {
        seekbar.SetStringValues(values);
    }


    /**
     * Gets called when the module has changed
     *
     * @param module
     */
    @Override
    public void onModuleChanged(String module)
    {
        if (cameraApiManager.getCamera() == null || FreedApplication.getContext() == null)
            return;
        if (module.equals(FreedApplication.getStringFromRessources(R.string.module_afbracket)) && seekbar.getVisibility() == View.VISIBLE)
            afBracketSettingsView.setVisibility(View.VISIBLE);
        else
            afBracketSettingsView.setVisibility(View.GONE);
    }


    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        handler.post(() -> setCameraToUi(cameraApiManager.getCamera()));
    }

    @Override
    public void onCameraClose() {
        handler.post(() -> setCameraToUi(null));

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }

    private final KeyPressedController.ManualModeChangedEvent manualModeChangedEvent = new KeyPressedController.ManualModeChangedEvent() {
        @Override
        public void onManualModeChanged(SettingKeys.Key key) {
            ManualButtonInterface button = buttonHashMap.get(key);
            if (button != null)
                manualButtonClickListner.onClick((View) button);
        }
    };
}
