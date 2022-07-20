package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.R;
import com.troop.freedcam.databinding.SettingsmenuGroupchildBinding;

/**
 * Created by troop on 01.02.2017.
 */

public class GroupChild extends LinearLayout {

    LinearLayout childHolder;
    private final SettingsmenuGroupchildBinding binding;
    public GroupChild(Context context, String headername) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater,R.layout.settingsmenu_groupchild,this,true);
        binding.groupchildHeader.setText(headername);
        childHolder = binding.groupchildChildholder;
    }

    public void addView(View view)
    {
        childHolder.addView(view);
    }

    public void clearChilds()
    {
        childHolder.removeAllViews();
    }

    public int childSize()
    {
        return childHolder.getChildCount();
    }
}
