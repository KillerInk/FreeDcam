package com.troop.freedcam.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by troop on 29.03.2016.
 */
public class FreeDPool
{
    private static ExecutorService executor;
    private static int number_of_cores;
    private static boolean isInit = false;
    private static int multiplier = 2;

    public static void INIT(int _multiplier)
    {
        number_of_cores = Runtime.getRuntime().availableProcessors();
        multiplier = _multiplier;
        executor = Executors.newFixedThreadPool(number_of_cores*multiplier);
        isInit = true;
    }

    public static void Destroy()
    {
        if (executor != null)
            executor.shutdown();
        while (!executor.isShutdown())
        {}
        executor = null;
        isInit = false;
    }

    public static boolean IsInit()
    {
        return  isInit;
    }

    public static void Execute(Runnable runnable)
    {
        try {
            if (executor == null)
                INIT(multiplier);
            if(runnable !=  null)
                executor.execute(runnable);
        }
        catch (NullPointerException ex)
        {}

    }
}
