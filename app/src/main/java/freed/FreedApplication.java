package freed;

import com.troop.freedcam.utils.ContextApplication;

public class FreedApplication extends ContextApplication {


    public static String getStringFromRessources(int id)
    {
        return getContext().getResources().getString(id);
    }

    public static String[] getStringArrayFromRessource(int id)
    {
        return getContext().getResources().getStringArray(id);
    }

}
