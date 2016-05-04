package com.freedcam.apis.i_camera.parameters;

import android.os.Handler;

import com.freedcam.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 02.05.2016.
 */
public class MatrixChooserParameter extends AbstractModeParameter
{
    private String[] custommatrixes;
    private String currentval = "off";
    private boolean isSupported =false;
    public MatrixChooserParameter(Handler uiHandler) {
        super(uiHandler);
        File confFolder = new File(StringUtils.GetFreeDcamConfigFolder+"matrix/");
        if (!confFolder.exists())
            confFolder.mkdir();
        File[] files = confFolder.listFiles();
        List<String> tmp = new ArrayList<>();
        tmp.add("off");
        if(files == null || files.length == 0)
        {
            return;
        }
        for (File f: files)
        {
            tmp.add(f.getName());
        }
        custommatrixes = new String[tmp.size()];
        tmp.toArray(custommatrixes);
        if (custommatrixes.length >0)
            isSupported = true;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        currentval = valueToSet;
    }

    @Override
    public String GetValue() {
        return currentval;
    }

    @Override
    public String[] GetValues() {
        return custommatrixes;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }
 }
