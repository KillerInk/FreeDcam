package freed.cam.ui.themenextgen.view.button;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import freed.cam.apis.basecamera.parameters.AbstractParameter;

public class NextGenMfItem extends NextGenTextItem {
    public NextGenMfItem(@NonNull Context context) {
        super(context);
    }

    public NextGenMfItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NextGenMfItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static NextGenMfItem getInstance(@NonNull Context context, String header, AbstractParameter parameter)
    {
        NextGenMfItem item = new NextGenMfItem(context);
        item.binding.textViewHeader.setText(header);
        if (parameter != null) {
            item.binding.setParameter(parameter);
            item.binding.notifyChange();
            item.parameter =parameter;
        }
        return item;
    }
}
