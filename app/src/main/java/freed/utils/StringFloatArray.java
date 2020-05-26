package freed.utils;

/**
 * Created by troop on 02.01.2017.
 */

public class StringFloatArray {
    private String[] keys;
    private Float[] values;

    private final String SPLITCHAR = "#";

    public StringFloatArray(int size)
    {
        keys = new String[size];
        values =new Float[size];
    }

    public StringFloatArray(String[] array)
    {

        keys = new String[array.length];
        values =new Float[array.length];
        for (int i=0; i< array.length;i++)
        {
            String[] split = array[i].split(SPLITCHAR);
            if (split.length == 2) {
                keys[i] = split[0];
                values[i] = Float.parseFloat(split[1]);
            }
        }
    }

    public int getSize()
    {
        return keys.length;
    }

    public float getValue(int pos)
    {
        return values[pos];
    }

    public float getValue(String key)
    {
        for (int i =0; i < keys.length;i++)
            if (key.equals(keys[i]))
                return values[i];
        return 0;
    }

    public String getKey(int pos)
    {
        return keys[pos];
    }

    public String getKey(float pos)
    {
        for (int i =0; i < values.length;i++)
            if (pos == values[i])
                return keys[i];
        return keys[0];
    }

    public void add(int pos,String key, float value)
    {
        keys[pos] = key;
        values[pos] = value;
    }

    public String[] getStringArray()
    {
        String[] ret = new String[keys.length];
        for (int i = 0; i < keys.length;i++)
        {
            ret[i] = keys[i]+SPLITCHAR+values[i];
        }
        return ret;
    }

    public String[] getKeys()
    {
        return keys;
    }

    public Float[] getValues()
    {
        return values;
    }
}
