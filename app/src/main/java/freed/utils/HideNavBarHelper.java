package freed.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class HideNavBarHelper
{
    private final int hideflags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private final int showflags = View.SYSTEM_UI_FLAG_VISIBLE;



    public void HIDENAVBAR(Window window)
    {
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        else
        {
            //HIDE nav and action bar
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(hideflags);
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility > 0) {
                    if (Build.VERSION.SDK_INT >= 16)
                        window.getDecorView().setSystemUiVisibility(hideflags);
                }
            });
        }
    }

    public void showNavbar(Window window)
    {
        if (Build.VERSION.SDK_INT < 16) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(showflags);
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility > 0) {
                    if (Build.VERSION.SDK_INT >= 16)
                        window.getDecorView().setSystemUiVisibility(showflags);
                }
            });
        }
    }
}
