package freed;

import com.troop.freedcam.utils.ContextApplication;

public class ContextApplication extends ContextApplication {

    public static String[] getStringArrayFromRessource(int id)
    {
        return getContext().getResources().getStringArray(id);
    }

}
