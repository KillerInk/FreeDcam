package freed.cam.ui.themenextgen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R;

import java.util.List;

public class NextGenSettingSubItemAdapter extends ArrayAdapter {

    private int selected;

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public NextGenSettingSubItemAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);
        TextView tv = v.findViewById(R.id.listviewlayout_textview);
        if (position == selected)
        {
            tv.setTextColor(Color.GREEN);
        }
        else
            tv.setTextColor(Color.WHITE);
        return v;
    }
}
