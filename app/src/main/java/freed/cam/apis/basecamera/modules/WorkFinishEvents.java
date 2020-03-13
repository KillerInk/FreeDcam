package freed.cam.apis.basecamera.modules;

import freed.file.holder.BaseHolder;

/**
 * Created by troop on 12.06.2017.
 */

public interface WorkFinishEvents {
    /**
     * Notifys the ui that a new file is saved
     * @param file that is new
     */
    void fireOnWorkFinish(BaseHolder file);

    /**
     * Notifys the ui that a new files are saved
     * @param files that are new
     */
    void fireOnWorkFinish(BaseHolder[] files);

    void internalFireOnWorkDone(BaseHolder file);

}
