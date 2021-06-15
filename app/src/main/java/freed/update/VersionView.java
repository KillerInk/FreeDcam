package freed.update;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.troop.freedcam.R;


public class VersionView extends FrameLayout {

    public interface ButtonEvents
    {
        void onDownloadClick();
        void onCloseClick();
    }

    private ButtonEvents eventListener;

    public VersionView(@NonNull Context context, ButtonEvents eventListener) {
        super(context);
        this.eventListener = eventListener;
        inflate(context);
    }


    private void inflate(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.new_version_view, this);
        Button dl = findViewById(R.id.button_downloadfreedcam);
        dl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadButtonClick();
            }
        });

        Button close = findViewById(R.id.button_close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseButtonClick();
            }
        });
    }

    public void onDownloadButtonClick()
    {
        if (eventListener != null)
            eventListener.onDownloadClick();

    }

    public void onCloseButtonClick()
    {
        if (eventListener != null)
            eventListener.onCloseClick();
    }


}
