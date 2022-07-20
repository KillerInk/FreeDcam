package freed.cam.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.R;

import javax.inject.Inject;

import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.ui.themenextgen.NextGenMainFragment;
import freed.cam.ui.themesample.ThemeSampleMainFragment;

public class ThemeManager
{

    public static final String DEFAULT = "Default";
    public static final String NEXTGEN = "NextGen";

    int layoutholder;
    private FragmentManager manager;
    CameraApiManager cameraApiManager;
    private String currentTheme;
    private Fragment activeFragment;

    @Inject
    public ThemeManager(CameraApiManager cameraApiManager)
    {
        this.cameraApiManager = cameraApiManager;
    }

    public void setLayoutholderAndFragmentManager(int layoutholder, FragmentManager fragmentManager)
    {
        this.layoutholder = layoutholder;
        this.manager = fragmentManager;
    }

    public void changeTheme(String theme)
    {
        if (theme.equals(currentTheme))
            return;
        if (cameraApiManager.getCamera() != null)
            cameraApiManager.getCamera().stopCamera();
        if (activeFragment != null)
            removeFragment(activeFragment);
        if (theme.equals(DEFAULT))
            activeFragment = new ThemeSampleMainFragment();
        else if (theme.equals(NEXTGEN))
            activeFragment = new NextGenMainFragment();

        inflateIntoHolder(layoutholder, activeFragment);
        currentTheme = theme;
    }

    private void inflateIntoHolder(int id, Fragment fragment)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id,fragment);
        transaction.commitNow();
    }

    private void removeFragment(Fragment fragment)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitNow();
    }
}
