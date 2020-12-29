package freed.cam.ui.videoprofileeditor.models;

import android.media.MediaCodecInfo;

import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.List;

import freed.cam.ui.videoprofileeditor.modelview.VideoProfileEditorModelView;

public class ProfileLevelModel extends ButtonModel
{

    private List<String> values;
    private boolean visibility = false;

    private VideoProfileEditorModelView modelView;

    public ProfileLevelModel(PopupModel popupModel, VideoProfileEditorModelView modelView) {
        super(popupModel);
        this.modelView = modelView;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }

    @Bindable
    public boolean getVisibility() {
        return visibility;
    }



    public void setValues(List<String> values) {
        this.values = values;

    }

    public void setDefault()
    {
        if (values != null && values.size() >0) {
            setTxt(values.get(0));
            updateProfileLevel(values.get(0));
        }
    }

    private void updateProfileLevel(String s) {
        MediaCodecInfo.CodecProfileLevel level = modelView.getCodecProfileLevel(s);
        modelView.currentProfile.get().level = level.level;
        modelView.currentProfile.get().profile = level.profile;
    }

    @Override
    public List<String> getStrings() {
        return values;
    }

    @Override
    public void onPopupItemClick(String item) {
        super.onPopupItemClick(item);
        updateProfileLevel(item);
    }
}
