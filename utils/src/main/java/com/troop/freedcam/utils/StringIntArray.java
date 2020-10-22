package com.troop.freedcam.utils;

/**
 * Created by troop on 13.02.2017.
 */

public class StringIntArray {
    private String keys[];
    private int values[][];

    public StringIntArray(String[] splitar)
    {
        keys = new String[splitar.length];
        values = new int[splitar.length][3];
        int i = 0;
        for (String s : splitar)
        {
            String SPLITCHARAR = ",";
            String[] valuessplit = s.split(SPLITCHARAR);
            keys[i] =valuessplit[0];
            values[i][0] =  Integer.parseInt(valuessplit[1]);
            values[i][1] =  Integer.parseInt(valuessplit[2]);
            values[i][2] =  Integer.parseInt(valuessplit[3]);
            i++;
        }
    }

    public int[] getValue(int pos)
    {
        return values[pos];
    }

    public String getKey(int pos)
    {
        return keys[pos];
    }

    public String[] getKeys(){return keys;}
}
