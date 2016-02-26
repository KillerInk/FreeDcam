package troop.com.themesample.subfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.VideoMediaProfile;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
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
    private Button button_delete;
    private VideoMediaProfile currentProfile;
    private Switch switch_Audio;


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
        this.switch_Audio = (Switch)view.findViewById(R.id.switchAudio);

        button_save.setOnClickListener(onSavebuttonClick);
        this.button_delete = (Button)view.findViewById(R.id.button_delete_profile);
        button_delete.setOnClickListener(ondeleteButtonClick);
        videoMediaProfiles = new HashMap<String, VideoMediaProfile>();

        File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
        if(f.exists())
            VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);



        AppSettingsManager appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(getActivity()), getContext());


        try {
            setMediaProfile(videoMediaProfiles.get(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE)));
        }
        catch (NullPointerException ex)
        {

        }

        switch_Audio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    VideoMediaProfile.SetAudioActive(true);
                } else {
                    VideoMediaProfile.SetAudioActive(false);
                }

            }
        });


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
            if(videoMediaProfiles.get(item.toString()).toString().length() > 1)
                setMediaProfile(videoMediaProfiles.get(item.toString()));
            return false;
        }
    };

    private View.OnClickListener ondeleteButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Delete Current Profile?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    videoMediaProfiles.remove(currentProfile.ProfileName);
                    currentProfile = null;
                    VideoMediaProfile.saveCustomProfiles(videoMediaProfiles);
                    videoMediaProfiles.clear();
                    File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
                    if(f.exists())
                    VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
                    clearProfileItems();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void clearProfileItems()
    {
        button_profile.setText("Select Profile");
        editText_profilename.setText("");
        editText_audiobitrate.setText("");
        editText_audiosamplerate.setText("");
        editText_videobitrate.setText("");
        editText_videoframerate.setText("");
        editText_maxrecordtime.setText("");
    }

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
            if (currentProfile == null) {
                Toast.makeText(getContext(),"Pls Select first a profile to edit", Toast.LENGTH_SHORT);
                return;
            }
            currentProfile.audioBitRate = Integer.parseInt(editText_audiobitrate.getText().toString());
            currentProfile.audioSampleRate = Integer.parseInt(editText_audiosamplerate.getText().toString());

            currentProfile.videoBitRate = Integer.parseInt(editText_videobitrate.getText().toString());
            currentProfile.videoFrameRate = Integer.parseInt(editText_videoframerate.getText().toString());
            currentProfile.duration = Integer.parseInt(editText_maxrecordtime.getText().toString());
            //if currentprofile has no new name the the profile in videomediaprofiles gets updated
            if (videoMediaProfiles.containsKey(editText_profilename.getText().toString()))
            {
                videoMediaProfiles.put(currentProfile.ProfileName, currentProfile);
            }
            else // it has a new name add it as new profile
            {
                VideoMediaProfile p = currentProfile.clone();
                p.ProfileName = editText_profilename.getText().toString();
                videoMediaProfiles.put(p.ProfileName, p);
            }
            VideoMediaProfile.saveCustomProfiles(videoMediaProfiles);
            videoMediaProfiles.clear();
            File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
            if(f.exists())
                VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
            Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_SHORT);
        }
    };
}
