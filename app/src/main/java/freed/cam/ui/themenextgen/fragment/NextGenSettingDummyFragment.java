package freed.cam.ui.themenextgen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.R;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.ui.themenextgen.adapter.NextGenSettingItemFragmentAdapter;
import freed.cam.ui.themenextgen.layoutconfig.SettingItemConfig;
import freed.cam.ui.themenextgen.view.button.NextGenSettingBoolItem;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;
import freed.cam.ui.themenextgen.view.button.NextGenSettingItem;
import freed.settings.SettingsManager;


@AndroidEntryPoint
public class NextGenSettingDummyFragment extends Fragment implements CameraHolderEvent
{
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    SettingsManager settingsManager;

    private List<SettingItemConfig> keyList;
    private ListView itemHolder;
    NextGenSettingItemFragmentAdapter adapter;
    ArrayAdapter<String> simpleTextAdapter;

    public static NextGenSettingDummyFragment getInstance(List<SettingItemConfig> list)
    {
        return new NextGenSettingDummyFragment(list);
    }

    private NextGenSettingDummyFragment()
    {

    }

    private NextGenSettingDummyFragment(List<SettingItemConfig> list)
    {
        this.keyList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nextgen_setting_dummyfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.itemHolder = view.findViewById(R.id.nextgen_itemholder);
        cameraApiManager.addEventListner(this);
        //fillUiWithParameters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraApiManager.removeEventListner(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        fillUiWithParameters();
    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        fillUiWithParameters();
    }

    @Override
    public void onCameraClose() {

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }

    private void fillUiWithParameters()
    {
        itemHolder.post(new Runnable() {
            @Override
            public void run() {

                adapter = new NextGenSettingItemFragmentAdapter(getContext(),R.id.nextgen_itemholder);
                adapter.setCameraApiManager(cameraApiManager);
                adapter.setSettingsManager(settingsManager);
                adapter.setKeyList(keyList);
                itemHolder.setAdapter(adapter);
                itemHolder.setOnItemClickListener(itemClickListener);
            }
        });

    }

    private final AdapterView.OnItemClickListener itemClickListener =  new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SettingItemConfig config = adapter.getKeyList()[position];
            if (view instanceof NextGenSettingItem && config.getViewType() != SettingItemConfig.ViewType.Custom) {
                itemHolder.setOnItemClickListener(null);
                fillUiWithValuesFromParameter((NextGenSettingItem) view);
            }
            else if (view instanceof NextGenSettingBoolItem)
            {

            }
            else if (view instanceof NextGenSettingButton && config.getViewType() == SettingItemConfig.ViewType.Custom)
            {
                ((NextGenSettingButton) view).onClick(view);


            }
        }
    };

    private NextGenSettingItem activeItem;
    private void fillUiWithValuesFromParameter(NextGenSettingItem nextGenSettingItem)
    {
        activeItem = nextGenSettingItem;
        String[] values = nextGenSettingItem.getParameter().getStringValues();
        simpleTextAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.nextgen_setting_adapterlayout_simpeltext, R.id.listviewlayout_textview, values);
        itemHolder.setAdapter(simpleTextAdapter);
        itemHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemHolder.setOnItemClickListener(null);
                String value = (String) itemHolder.getItemAtPosition(position);
                if (activeItem instanceof NextGenSettingItem) {
                    activeItem.getParameter().setStringValue(value, true);
                }
                itemHolder.setAdapter(adapter);
                itemHolder.setOnItemClickListener(itemClickListener);
                activeItem = null;
            }
        });
    }
}
