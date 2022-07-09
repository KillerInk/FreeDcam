package freed.cam.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.troop.freedcam.R;

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
        if (theme.equals("Default"))
            inflateIntoHolder(layoutholder, new ThemeSampleMainFragment());
        else if (theme.equals("NextGen"))
            inflateIntoHolder(layoutholder, new NextGenMainFragment());
    }

    private void inflateIntoHolder(int id, Fragment fragment)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(id,fragment);
        transaction.commit();
    }
}
