package freed.cam.ui.themesample.settings.opcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.image.ImageManager;
import freed.settings.OpCodeUrl;
import freed.settings.SettingsManager;

/**
 * Created by KillerInk on 18.12.2017.
 */

public class OpCodeFragment extends Fragment implements ListView.OnItemClickListener {

    private LinearLayout itemsholder;
    private final String TAG = OpCodeFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_opcodefragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemsholder = view.findViewById(R.id.opcodefragment_itemsholder);
        if (itemsholder.getChildCount() > 0)
            itemsholder.removeAllViews();
        List<OpCodeUrl> list = SettingsManager.getInstance().opcodeUrlList;
        if (list == null)
            list = new ArrayList<>();
        int camercount = SettingsManager.getInstance().getCamerasCount();
        for (int i = 0; i< camercount;i++)
        {
            boolean added = false;
            for (OpCodeUrl url : list)
            {
                if (i == url.getID()) {
                    itemsholder.addView(new OpcodeItem(getContext(), url, i));
                    added = true;
                }
            }
            if (!added)
                itemsholder.addView(new OpcodeItem(getContext(),null,i));
        }

        Button downloadButton = view.findViewById(R.id.opcodefragment_downloadButton);
        downloadButton.setOnClickListener(v -> {
            for (int i = 0; i< itemsholder.getChildCount(); i++)
            {
                OpcodeItem item = (OpcodeItem) itemsholder.getChildAt(i);
                if (item.downLoadNeeded())
                    ImageManager.putImageLoadTask(new OpCodeDownloadTask(item.getOpCodeUrl(), item));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
