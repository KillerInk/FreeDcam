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

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.I_ParametersLoaded;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.FreeDPool;
import freed.utils.Logger;

/**
 * Created by troop on 13.06.2016.
 * handel the ManualButton visibility for manual shutter and iso
 * iso and shutter gets the aeevent attached. when there a value is changed then this class gets
 * notified.
 * if its auto set shutter isSetSupported to false to disable it in ui
 * if its manual enable the shutter button.
 */
public abstract class AE_Handler_Abstract implements I_ParametersLoaded
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
    private int currentIso;
    private int currentShutter;

    protected boolean auto = true;
    private boolean readMetaData;

    public AE_Handler_Abstract(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        this.parameters = parameters;
        this.cameraWrapper = cameraUiWrapper;
        cameraWrapper.GetParameterHandler().AddParametersLoadedListner(this);
    }

    public ManualParameterInterface getManualIso()
    {
        return iso;
    }

    public ManualParameterInterface getShutterManual()
    {
        return shutter;
    }

    @Override
    public void ParametersLoaded(CameraWrapperInterface cameraWrapper)
    {
        aeevent.onManualChanged(AeManual.iso,true,0);
    }

    protected final AeManualEvent aeevent =  new AeManualEvent() {
        @Override
        public void onManualChanged(AeManual fromManual, boolean automode, int value) {
            if (shutter.IsSupported() && iso.IsSupported() && cameraWrapper.GetAppSettingsManager().GetCurrentCamera() == 0)
            {
                if (automode) {
                    Logger.d(TAG, "AutomodeActive");
                    auto = automode;


                    switch (fromManual) {
                        case shutter:
                            currentIso = iso.GetValue();
                            iso.setValue(-1);
                            break;
                        case iso:
                            currentShutter = shutter.GetValue();
                            shutter.setValue(-1);
                            shutter.ThrowBackgroundIsSetSupportedChanged(false);
                            break;
                    }
                    resetManualMode();
                    startReadingMeta();

                } else {
                    if (auto) {
                        Logger.d(TAG, "Automode Deactivated, set last values");
                        auto = false;
                        switch (fromManual) {
                            case shutter:
                                iso.setValue(currentIso);
                                break;
                            case iso:
                                if (currentShutter == 0) currentShutter = 9;
                                shutter.setValue(currentShutter);
                                shutter.ThrowBackgroundIsSetSupportedChanged(true);
                                break;
                        }
                        startReadingMeta();
                    } else {
                        readMetaData = false;
                        Logger.d(TAG, "Automode Deactivated, set UserValues");
                        switch (fromManual) {
                            case shutter:
                                shutter.setValue(value);

                                break;
                            case iso:
                                iso.setValue(value);

                                break;
                        }
                    }
                    resetManualMode();
                }
                ((ParametersHandler) cameraWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                if (automode) {
                    String t = cameraWrapper.GetParameterHandler().IsoMode.GetValue();
                    if (!t.equals(KEYS.ISO100))
                        cameraWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.ISO100, true);
                    else
                        cameraWrapper.GetParameterHandler().IsoMode.SetValue(KEYS.AUTO, true);
                    cameraWrapper.GetParameterHandler().IsoMode.SetValue(t, true);
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
        if (((CameraHolder)cameraWrapper.GetCameraHolder()).DeviceFrameWork == CameraHolder.Frameworks.MTK)
            return;
        readMetaData = true;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                while (readMetaData && auto)
                {
                    try {
                        shutter.ThrowCurrentValueStringCHanged("1/"+(int) ((ParametersHandler)cameraWrapper.GetParameterHandler()).getDevice().getCurrentExposuretime());
                        iso.ThrowCurrentValueStringCHanged(((ParametersHandler)cameraWrapper.GetParameterHandler()).getDevice().getCurrentIso()+"");
                    }
                    catch (RuntimeException ex)
                    {
                        readMetaData = false;
                        return;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
