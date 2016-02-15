package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.troop.freedcam.camera.modules.VideoMediaProfile;

import java.util.HashMap;

import troop.com.themesample.R;

/**
 * Created by troop on 15.02.2016.
 */
public class VideoProfileEditorFragment extends Fragment
{
    private Button button_profile;
    private EditText editText_audiobitrate;
    private EditText editText_audiosamplerate;
    private EditText editText_videobitrate;
    private EditText editText_videoframerate;
    private HashMap<String, VideoMediaProfile> videoMediaProfiles;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.video_profile_editor_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.button_profile = (Button)view.findViewById(R.id.button_Profile);
        button_profile.setOnClickListener(profileClickListner);
        this.editText_audiobitrate = (EditText)view.findViewById(R.id.editText_audioBitrate);
        this.editText_audiosamplerate = (EditText)view.findViewById(R.id.editText_audioSampleRate);
        this.editText_videobitrate = (EditText)view.findViewById(R.id.editText_videoBitrate);
        this.editText_videoframerate = (EditText)view.findViewById(R.id.editText_videoframerate);
        videoMediaProfiles = new HashMap<String, VideoMediaProfile>();
        VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private View.OnClickListener profileClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.setOnMenuItemClickListener(profileMenuitemListner);
            for (VideoMediaProfile m : videoMediaProfiles.values())
                menu.getMenu().add(m.ProfileName);
            menu.show();
        }
    };

    private PopupMenu.OnMenuItemClickListener profileMenuitemListner = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            setMediaProfile(videoMediaProfiles.get(item.toString()));
            return false;
        }
    };

    private void setMediaProfile(VideoMediaProfile profile)
    {
        button_profile.setText(profile.ProfileName);
        editText_audiobitrate.setText(profile.audioBitRate+"");
        editText_audiosamplerate.setText(profile.audioSampleRate+"");
        editText_videobitrate.setText(profile.videoBitRate+"");
        editText_videoframerate.setText(profile.videoFrameRate+"");
    }
}
