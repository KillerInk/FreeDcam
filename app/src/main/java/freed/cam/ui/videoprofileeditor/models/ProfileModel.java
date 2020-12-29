package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.ArrayList;
import java.util.List;

import freed.cam.ui.videoprofileeditor.modelview.VideoProfileEditorModelView;
import freed.utils.VideoMediaProfile;

public class ProfileModel extends ButtonModel {

    private VideoProfileEditorModelView videoProfileEditorModelView;

    public ProfileModel(VideoProfileEditorModelView videoProfileEditorModelView, PopupModel popupModel)
    {
        super(popupModel);
        this.videoProfileEditorModelView = videoProfileEditorModelView;
    }

    @Override
    public void onPopupItemClick(String item) {
        super.onPopupItemClick(item);
        if(videoProfileEditorModelView.getVideoMediaProfiles().get(item).toString().length() > 1) {
            videoProfileEditorModelView.setProfile(item);
        }
    }

    @Override
    public List<String> getStrings() {
        List<String> ls = new ArrayList<>();
        for(VideoMediaProfile prof : videoProfileEditorModelView.getVideoMediaProfiles().values())
            ls.add(prof.ProfileName);
        return ls;
    }
}
