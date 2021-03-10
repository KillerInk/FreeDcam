package freed.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import freed.FreedApplication;

public class DisplayUtil {

    public static Point getDisplaySize()
    {
        Point out = new Point();
        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager) FreedApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (FreedApplication.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                out.x = size.x;
                out.y = size.y;
            }
            else
            {
                out.y = size.x;
                out.x = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = FreedApplication.getContext().getResources().getDisplayMetrics();
            if (FreedApplication.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                out.x = metrics.widthPixels;
                out.y = metrics.heightPixels;
            }
            else
            {
                out.x = metrics.heightPixels;
                out.y = metrics.widthPixels;
            }

        }
        return out;
    }
}
