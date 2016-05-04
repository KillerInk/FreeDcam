package com.freedcam.apis.i_camera.parameters;

//defcomg was here

import android.os.Handler;

/**
 * Created by George on 1/19/2015.
 */
public class GuideList extends AbstractModeParameter
{

    private String value;
    public GuideList(Handler uiHandler) {
        super(uiHandler);
        //this.appSettingsManager = appSettingsManager;




    }





    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCam)
    {
        value = valueToSet;
        BackgroundValueHasChanged(valueToSet);

        //appSettingsManager.setString(AppSettingsManager.SETTING_GUIDE, valueToSet);
       // ImageView imageView1 = (ImageView)context.findViewById(R.id.GuideView);
       // imageView1.setImageResource(R.drawable.ic_guide_golden_spiral);
    }

    @Override
    public String GetValue()
    {
        if (value == null || value.equals(""))
            return "Rule Of Thirds";
        else
            return value;
    }

    @Override
    public String[] GetValues()
    {
        //defcomg was 24/01/15 Rearranged and added new Guides

        //return new String[]{"None","Instagram 1:1","Instagram 4:3","Instagram 16:9","Diagonal","Golden Ratio","Golden Spiral","Rule Of Thirds"};

        return new String[]{"None","Center Type +","Center Type x","Diagonal Type 1","Diagonal Type 2","Diagonal Type 3","Diagonal Type 4","Diagonal Type 5","Golden Hybrid","Golden R/S 1","Golden R/S 2","Golden Ratio","Golden Spiral","Golden Triangle","Group POV Five","Group POV Three","Group POV Potrait","Group POV Full","Group POV Elvated","Group by Depth","Group Center Lead","Rule Of Thirds","Square 1:1","Square 4:3","Square 16:9"};
    }



}
