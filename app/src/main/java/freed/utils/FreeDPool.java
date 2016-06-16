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

package freed.utils;

/**
 * Created by troop on 29.03.2016.
 */
public class FreeDPool
{
    /*private static ExecutorService executor;
    private static boolean isInit = false;
    private static int multiplier = 2;

    public static void INIT(int _multiplier)
    {
        int number_of_cores = Runtime.getRuntime().availableProcessors();
        multiplier = _multiplier;
        executor = Executors.newFixedThreadPool(number_of_cores *multiplier);
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
    }*/

    public static void Execute(Runnable runnable)
    {
        new Thread(runnable).start();
        /*try {
            if (executor == null)
                INIT(multiplier);
            if(runnable !=  null)
                executor.execute(runnable);
        }
        catch (NullPointerException ex)
        {}*/

    }
}
