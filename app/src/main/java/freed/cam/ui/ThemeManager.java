package freed.cam.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import javax.inject.Inject;

import freed.cam.apis.CameraApiManager;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.ui.themenextgen.NextGenMainFragment;
import freed.cam.ui.themesample.ThemeSampleMainFragment;

public class ThemeManager
{
    int layoutholder;
    private FragmentManager manager;
    CameraApiManager cameraApiManager;
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

    public void changeTheme(String theme,boolean restartcam)
    {
//        if (restartcam)
//            cameraApiManager.onPause();
        if (theme.equals("Default"))
            inflateIntoHolder(layoutholder, new ThemeSampleMainFragment());
        else if (theme.equals("NextGen"))
            inflateIntoHolder(layoutholder, new NextGenMainFragment());
//        if (restartcam)
//            cameraApiManager.onResume();
    }

    private void inflateIntoHolder(int id, Fragment fragment)
    {
        if (activeFragment!= null)
        {
            //cameraApiManager.clearEventListners();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(activeFragment);
            transaction.commit();
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.commit();
        activeFragment = fragment;
    }
}
