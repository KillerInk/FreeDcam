package freed.cam.apis.featuredetector;

import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;

/**
 * Created by troop on 23.01.2017.
 */

abstract class AbstractFeatureDetectorTask {

    private final  String TAG = AbstractFeatureDetectorTask.class.getSimpleName();
    private ProgressUpdate progressUpdateListner;
    AbstractFeatureDetectorTask(ProgressUpdate progressUpdate)
    {
        this.progressUpdateListner = progressUpdate;
    }

    public interface ProgressUpdate
    {
        void onProgessUpdate(String msg);
        void onTaskEnd(String msg);
    }

    void sendProgress(SettingInterface settingMode, String name)
    {
        if (settingMode instanceof SettingMode) {
            SettingMode ts = (SettingMode) settingMode;
            if (ts.isSupported()) {
                String[]ar = ts.getValues();
                String t = getStringFromArray(ar);
                publishProgress(name+" Values:" +t);
                publishProgress(name+":" + ts.get());
            }
            else
                publishProgress(name+" not supported");
        }

    }

    public abstract void detect();

    void publishProgress(String value) {
        if (progressUpdateListner != null)
            progressUpdateListner.onProgessUpdate(value);
    }

    public void onPostExecute(String s) {
        if (progressUpdateListner != null)
            progressUpdateListner.onTaskEnd(s);
    }

    String getStringFromArray(String[] arr)
    {
        if (arr == null)
            return null;
        StringBuilder t = new StringBuilder();
        for (int i =0; i<arr.length;i++)
            t.append(arr[i]);
        return t.toString();
    }


}
