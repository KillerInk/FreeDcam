package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.List;

import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;
import freed.cam.ui.videoprofileeditor.modelview.VideoProfileEditorModelView;

public class EncoderModel extends ButtonModel
{
    private List<String> values;
    private ProfileLevelModel profileLevelModel;
    private VideoProfileEditorModelView modelView;
    private boolean visibility = false;
    public EncoderModel(PopupModel popupModel, ProfileLevelModel profileLevelModel, VideoProfileEditorModelView modelView) {
        super(popupModel);
        this.profileLevelModel = profileLevelModel;
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

    @Override
    public void setTxt(String txt) {
        super.setTxt(txt);
    }

    public void setToDefault()
    {
        if (values != null && values.size() > 0)
            setTxt(values.get(0));
        else
            setTxt("Default");
        setValues();
        profileLevelModel.setDefault();
    }

    @Override
    public List<String> getStrings() {
        return values;
    }

    @Override
    public void onPopupItemClick(String item) {
        super.onPopupItemClick(item);
        modelView.currentProfile.get().encoderName = item;
        setValues();

    }

    public void setValues()
    {
        profileLevelModel.setValues(modelView.getProfileLevels(getTxt()));
        if (!getTxt().equals("Default")) {
            profileLevelModel.setVisibility(true);
            profileLevelModel.setDefault();
        }
        else {
            profileLevelModel.setVisibility(false);
            modelView.currentProfile.get().profile = -1;
            modelView.currentProfile.get().level = -1;
        }
    }
}
