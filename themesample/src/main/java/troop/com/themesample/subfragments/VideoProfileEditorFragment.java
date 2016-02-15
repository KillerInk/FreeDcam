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
    private EditText editText_profilename;
    private EditText editText_audiobitrate;
    private EditText editText_audiosamplerate;
    private EditText editText_videobitrate;
    private EditText editText_videoframerate;
    private EditText editText_maxrecordtime;
    private Button button_save;
    private VideoMediaProfile currentProfile;


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
        this.editText_profilename= (EditText)view.findViewById(R.id.editText_ProfileName);
        this.editText_audiobitrate = (EditText)view.findViewById(R.id.editText_audioBitrate);
        this.editText_audiosamplerate = (EditText)view.findViewById(R.id.editText_audioSampleRate);
        this.editText_videobitrate = (EditText)view.findViewById(R.id.editText_videoBitrate);
        this.editText_videoframerate = (EditText)view.findViewById(R.id.editText_videoframerate);
        this.editText_maxrecordtime = (EditText)view.findViewById(R.id.editText_recordtime);
        this.button_save = (Button)view.findViewById(R.id.button_Save_profile);
        button_save.setOnClickListener(onSavebuttonClick);
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
        currentProfile = profile;
        button_profile.setText(profile.ProfileName);
        editText_profilename.setText(profile.ProfileName);
        editText_audiobitrate.setText(profile.audioBitRate+"");
        editText_audiosamplerate.setText(profile.audioSampleRate+"");
        editText_videobitrate.setText(profile.videoBitRate+"");
        editText_videoframerate.setText(profile.videoFrameRate+"");
        editText_maxrecordtime.setText(profile.duration+"");
    }

    private View.OnClickListener onSavebuttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (currentProfile == null)
                return;

            currentProfile.audioBitRate = Integer.parseInt(editText_audiobitrate.getText().toString());
            currentProfile.audioSampleRate = Integer.parseInt(editText_audiosamplerate.getText().toString());
            currentProfile.videoBitRate = Integer.parseInt(editText_videobitrate.getText().toString());
            currentProfile.videoFrameRate = Integer.parseInt(editText_videoframerate.getText().toString());
            currentProfile.duration = Integer.parseInt(editText_maxrecordtime.getText().toString());

            if (videoMediaProfiles.containsKey(editText_profilename.getText().toString()))
            {
                videoMediaProfiles.put(currentProfile.ProfileName, currentProfile);
            }
            else
            {
                VideoMediaProfile p = currentProfile.clone();
                p.ProfileName = editText_profilename.getText().toString();
                videoMediaProfiles.put(p.ProfileName, p);
            }
            VideoMediaProfile.saveCustomProfiles(videoMediaProfiles);
            videoMediaProfiles.clear();
            VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
        }
    };
}
