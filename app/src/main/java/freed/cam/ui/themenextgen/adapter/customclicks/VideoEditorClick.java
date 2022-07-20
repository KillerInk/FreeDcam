package freed.cam.ui.themenextgen.adapter.customclicks;

import android.content.Context;
import android.content.Intent;

import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;
import freed.cam.ui.videoprofileeditor.views.VideoProfileEditorActivity;

public class VideoEditorClick implements NextGenSettingButton.NextGenSettingButtonClick{

    private final Context context;

    public VideoEditorClick(Context context)
    {
        this.context = context;
    }

    @Override
    public void onSettingButtonClick() {
        Intent i = new Intent(context, VideoProfileEditorActivity.class);
        context.startActivity(i);
    }
}
