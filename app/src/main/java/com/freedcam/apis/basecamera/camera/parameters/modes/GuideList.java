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

package com.freedcam.apis.basecamera.camera.parameters.modes;

//defcomg was here

/**
 * Created by George on 1/19/2015.
 */
public class GuideList extends AbstractModeParameter
{

    private String value;
    public GuideList() {
        super();
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
