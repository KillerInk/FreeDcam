package troop.com.themesample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildExit extends UiSettingsChild {
    public UiSettingsChildExit(Context context) {
        super(context);
    }

    public UiSettingsChildExit(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                i_activity.closeActivity();
            }
        });
    }
}
