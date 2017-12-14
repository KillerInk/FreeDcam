/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 13.06.2016.
 * handel the ManualButton visibility for manual shutter and iso
 * iso and shutter gets the aeevent attached. when there a value is changed then this class gets
 * notified.
 * if its auto set shutter isSetSupported to false to disable it in ui
 * if its manual enable the shutter button.
 */
public abstract class AE_Handler_Abstract
{
    public interface AeManualEvent
    {
        void onManualChanged(AeManual fromManual, boolean automode, int value);
    }

    public enum AeManual
    {
        shutter,
        iso,
    }

    private final String TAG = AE_Handler_Abstract.class.getSimpleName();

    protected final CameraWrapperInterface cameraWrapper;
    protected final Camera.Parameters parameters;

    protected ManualParameterAEHandlerInterface shutter;
    protected ManualParameterAEHandlerInterface iso;
    protected int currentIso;
    protected int currentShutter;

    protected boolean auto = true;
    private boolean readMetaData;

    public AE_Handler_Abstract(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        this.parameters = parameters;
        this.cameraWrapper = cameraUiWrapper;
    }

    public ParameterInterface getManualIso()
    {
        return iso;
    }

    public ParameterInterface getShutterManual()
    {
        return shutter;
    }


    protected final AeManualEvent aeevent =  new AeManualEvent() {
        @Override
        public void onManualChanged(AeManual fromManual, boolean automode, int value) {
            if (shutter.IsSupported() && iso.IsSupported() && SettingsManager.getInstance().GetCurrentCamera() == 0)
            {
                if (automode) {
                    Log.d(TAG, "AutomodeActive");
                    auto = automode;


                    switch (fromManual) {
                        case shutter:
                            currentIso = iso.GetValue();
                            iso.setValue(0, true);
                            break;
                        case iso:
                            currentShutter = shutter.GetValue();
                            shutter.setValue(0,true);
                            shutter.fireIsReadOnlyChanged(false);
                            break;
                    }
                    resetManualMode();
                    startReadingMeta();

                } else {
                    if (auto) {
                        Log.d(TAG, "Automode Deactivated, set last values");
                        auto = false;
                        switch (fromManual) {
                            case shutter:
                                iso.setValue(currentIso,true);
                                break;
                            case iso:
                                if (currentShutter == 0) currentShutter = 9;
                                shutter.setValue(currentShutter,true);
                                shutter.fireIsReadOnlyChanged(true);
                                break;
                        }
                        startReadingMeta();
                    } else {
                        readMetaData = false;
                        Log.d(TAG, "Automode Deactivated, set UserValues");
                        switch (fromManual) {
                            case shutter:
                                shutter.setValue(value,true);

                                break;
                            case iso:
                                iso.setValue(value,true);

                                break;
                        }
                    }
                    resetManualMode();
                }
                Log.d(TAG,"AeManualEvent aeevent");
                ((ParametersHandler) cameraWrapper.getParameterHandler()).SetParametersToCamera(parameters);
                if (automode) {
                    String t = cameraWrapper.getParameterHandler().get(Settings.IsoMode).GetStringValue();
                    if (!t.equals(cameraWrapper.getResString(R.string.iso100_)))
                        cameraWrapper.getParameterHandler().get(Settings.IsoMode).SetValue(cameraWrapper.getResString(R.string.iso100_), true);
                    else
                        cameraWrapper.getParameterHandler().get(Settings.IsoMode).SetValue(cameraWrapper.getResString(R.string.auto_), true);
                    cameraWrapper.getParameterHandler().get(Settings.IsoMode).SetValue(t, true);
                }
            }
        }
    };

    protected abstract void resetManualMode();

    /**
     * updates the manual shutter/iso button in ui with the current iso and exposuretime values
     * when ae is in automode
     */
    private void startReadingMeta()
    {
        if (((CameraHolder)cameraWrapper.getCameraHolder()).DeviceFrameWork == CameraHolder.Frameworks.MTK)
            return;
        readMetaData = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                while (readMetaData && auto)
                {
                    try {
                        shutter.fireStringValueChanged("1/"+(int) cameraWrapper.getParameterHandler().getCurrentExposuretime());
                        iso.fireStringValueChanged(cameraWrapper.getParameterHandler().getCurrentIso()+"");
                    }
                    catch (RuntimeException ex)
                    {
                        readMetaData = false;
                        return;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.WriteEx(e);
                    }
                }
            }
        });
    }
}
