package freed.cam.ui.themenextgen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import freed.cam.apis.CameraApiManager;
import freed.cam.ui.themenextgen.layoutconfig.SettingItemConfig;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class NextGenSettingItemFragmentAdapter extends ArrayAdapter
{
    private static final String TAG = NextGenSettingItemFragmentAdapter.class.getSimpleName();
    private SettingItemConfig[] keyList;
    private SettingsManager settingsManager;
    private CameraApiManager cameraApiManager;

    public NextGenSettingItemFragmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    public void setCameraApiManager(CameraApiManager cameraApiManager) {
        this.cameraApiManager = cameraApiManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public SettingItemConfig[] getKeyList() {
        return keyList;
    }

    public void setKeyList(List<SettingItemConfig> keyList) {

        NextGenSettingItemAdapterTools adapterTools = new NextGenSettingItemAdapterTools();
        List<SettingItemConfig> l = adapterTools.getValidSettingItemConfigList(keyList,cameraApiManager,settingsManager);
        this.keyList = l.toArray(new SettingItemConfig[l.size()]);
        adapterTools.fillConfigsWithViews(this.keyList,cameraApiManager,settingsManager,getContext());

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        if (keyList != null)
            return keyList.length;
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SettingItemConfig config = keyList[position];
        convertView = config.getView();
        if(convertView == null) {
            if (config.getKey() != null)
                Log.e(TAG,"error getView for:" + config.getKey().toString());
            else
                Log.e(TAG, "error getView for:" + getContext().getResources().getText(config.getHeader()));
        }
        return convertView;
    }
}
