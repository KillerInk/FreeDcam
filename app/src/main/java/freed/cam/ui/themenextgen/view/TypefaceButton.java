package freed.cam.ui.themenextgen.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.troop.freedcam.R;

public class TypefaceButton extends androidx.appcompat.widget.AppCompatButton {

    public TypefaceButton(@NonNull Context context) {
        super(context);
        setTypeface(ResourcesCompat.getFont(context, R.font.freedcam));
    }

    public TypefaceButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(ResourcesCompat.getFont(context, R.font.freedcam));
    }

    public TypefaceButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(ResourcesCompat.getFont(context, R.font.freedcam));
    }
}
