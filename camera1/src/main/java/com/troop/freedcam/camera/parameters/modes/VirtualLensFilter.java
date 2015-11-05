package com.troop.freedcam.camera.parameters.modes;

/**
 * Created by GeorgeKiarie on 9/24/2015.
 */
public class VirtualLensFilter{} /*{BaseCameraHolder baseCameraHolder;

    public VirtualLensFilter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder)
    {
        super(handler,parameters, parameterChanged, value, values);

        if(DeviceUtils.isZTEADV()) {
            String tmp = parameters.get("video-hfr");
            if (tmp != null && !tmp.equals("")) {
                this.isSupported = true;
                this.values = "video-hfr-values";
                this.value = "video-hfr";
            }
        }
        else
        {
            this.isSupported = false;
        }

        this.baseCameraHolder = (BaseCameraHolder) baseCameraHolder;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues()
    {
        if (DeviceUtils.isZTEADV())
            return new String[] {"0","1","2","3","4","5","6","7","8","9","10","11"};

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //baseCameraHolder.StopPreview();
        super.SetValue("0", setToCam);

        const-string v2, "100 0 0 0 100 0 0 0 100 0 0 80"

        aput-object v2, v0, v1

        const/4 v1, 0x2

        const-string v2, "100 0 0 0 100 0 0 0 100 12 50 100"

        aput-object v2, v0, v1

        const/4 v1, 0x3

        const-string v2, "100 0 0 0 100 0 0 0 100 0 100 100"

        aput-object v2, v0, v1

        const/4 v1, 0x4

        const-string v2, "100 0 0 0 100 0 0 0 100 0 85 0"

        aput-object v2, v0, v1

        const/4 v1, 0x5

        const-string v2, "100 0 0 0 100 0 0 0 100 80 80 0"

        aput-object v2, v0, v1

        const/4 v1, 0x6

        const-string v2, "100 0 0 0 100 0 0 0 100 80 0 0"

        aput-object v2, v0, v1

        const-string v1, "100 0 0 0 100 0 0 0 50 115 20 70"

        aput-object v1, v0, v4

        const/16 v1, 0x8

        const-string v2, "100 0 0 0 100 0 0 0 40 -60 -60 -60"

        aput-object v2, v0, v1

        const/16 v1, 0x9

        const-string v2, "100 0 0 0 100 0 0 0 40 -60 -60 -60"

        aput-object v2, v0, v1

        const/16 v1, 0xa

        const-string v2, "100 0 0 0 100 0 0 0 40 -60 -60 -60"

        aput-object v2, v0, v1

        const/16 v1, 0xb

        String[] elev = "100, 0, 0, 0, 100, 0, 0, 0, 40, -60, -60, -60";



        //baseCameraHolder.StartPreview();
    }

    @Override
    public String GetValue()
    {
        String ret = super.GetValue();
        if (ret == null || ret == "")
            ret = "0";


        return "0";
    }
} */