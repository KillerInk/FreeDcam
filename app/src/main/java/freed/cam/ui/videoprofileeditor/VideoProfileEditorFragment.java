/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.videoprofileeditor;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Switch;
import android.widget.Toast;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.HashMap;

import freed.settings.SettingsManager;
import freed.settings.Settings;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;

/**
 * Created by troop on 15.02.2016.
 */
public class VideoProfileEditorFragment extends Fragment {
    final String TAG = VideoProfileEditorFragment.class.getSimpleName();

    enum VideoCodecs {
        H263(1),
        H264(2),
        MPEG_4_SP(3),
        VP8(4),
        HEVC(5);

        private VideoCodecs(int value)
        {
            this.value = value;
        }
        private int value;
        public int GetInt()
        {
            return value;
        }
    }

    enum AudioCodecs {
        AMR_NB(1),
        AMR_WB(2),
        AAC(3),
        HE_AAC(4),
        AAC_ELD(5),
        VORBIS(6);


        private AudioCodecs(int value)
        {
            this.value = value;
        }
        private int value;
        public int GetInt()
        {
            return value;
        }
    }

    private Button button_profile;
    private EditText editText_profilename;
    private EditText editText_audiobitrate;
    private EditText editText_audiosamplerate;
    private EditText editText_videobitrate;
    private EditText editText_videoframerate;
    private EditText editText_maxrecordtime;
    private EditText editText_width;
    private EditText editText_height;
    private Button button_recordMode;
    private Button button_videoCodec;
    private Button button_audioCodec;
    private VideoMediaProfile currentProfile;
    private Switch switch_Audio;

    private HashMap<String, VideoMediaProfile> videoMediaProfiles;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.video_profile_editor_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_profile = (Button)view.findViewById(id.button_Profile);
        button_profile.setOnClickListener(profileClickListner);
        editText_profilename = (EditText)view.findViewById(id.editText_ProfileName);
        editText_audiobitrate = (EditText)view.findViewById(id.editText_audioBitrate);
        editText_audiosamplerate = (EditText)view.findViewById(id.editText_audioSampleRate);
        editText_videobitrate = (EditText)view.findViewById(id.editText_videoBitrate);
        editText_videoframerate = (EditText)view.findViewById(id.editText_videoframerate);
        editText_maxrecordtime = (EditText)view.findViewById(id.editText_recordtime);
        editText_width = (EditText)view.findViewById(id.editText_Profilewidth);
        editText_height = (EditText)view.findViewById(id.editText_Profileheight);
        Button button_save = (Button) view.findViewById(id.button_Save_profile);
        switch_Audio = (Switch)view.findViewById(id.switchAudio);
        button_recordMode = (Button)view.findViewById(id.button_recordMode);
        button_recordMode.setOnClickListener(recordModeClickListner);
        button_videoCodec =(Button)view.findViewById(id.button_videoCodec);
        button_videoCodec.setOnClickListener(onVideoCodecClickListner);
        button_audioCodec =(Button)view.findViewById(id.button_audioCodec);
        button_audioCodec.setOnClickListener(onAudioCodecClickListner);

        button_save.setOnClickListener(onSavebuttonClick);
        Button button_delete = (Button) view.findViewById(id.button_delete_profile);
        button_delete.setOnClickListener(ondeleteButtonClick);
        videoMediaProfiles = new HashMap<>();

        if (!SettingsManager.getInstance().isInit()){

            SettingsManager.getInstance().init(PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()), getResources());
        }
        SettingsManager.getInstance().getCamApi();
        videoMediaProfiles = SettingsManager.getInstance().getMediaProfiles();
        if (videoMediaProfiles != null) {

            setMediaProfile(videoMediaProfiles.get(SettingsManager.get(Settings.VideoProfiles).get()));
        }
    }

    private final OnClickListener profileClickListner = new OnClickListener() {
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

    private final OnMenuItemClickListener profileMenuitemListner = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            if(videoMediaProfiles.get(item.toString()).toString().length() > 1)
                setMediaProfile(videoMediaProfiles.get(item.toString()));
            return false;
        }
    };


    private final OnClickListener recordModeClickListner = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            PopupMenu menu = new PopupMenu(getContext(), v);
            menu.setOnMenuItemClickListener(recordModeMenuitemListner);
            menu.getMenu().add(VideoMode.Normal.toString());
            menu.getMenu().add(VideoMode.Highspeed.toString());
            menu.getMenu().add(VideoMode.Timelapse.toString());
            menu.show();
        }
    };

    private OnClickListener onVideoCodecClickListner = new OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.setOnMenuItemClickListener(videoCodecMenuitemListner);
            menu.getMenu().add(VideoCodecs.H263.toString());
            menu.getMenu().add(VideoCodecs.H264.toString());
            menu.getMenu().add(VideoCodecs.MPEG_4_SP.toString());
            menu.getMenu().add(VideoCodecs.VP8.toString());
            menu.getMenu().add(VideoCodecs.HEVC.toString());
            menu.show();
        }
    };

    private final OnMenuItemClickListener videoCodecMenuitemListner = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            button_videoCodec.setText(item.toString());
            return false;
        }
    };

    private OnClickListener onAudioCodecClickListner = new OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.setOnMenuItemClickListener(audioCodecMenuitemListner);
            for (AudioCodecs codecs : AudioCodecs.values())
                menu.getMenu().add(codecs.toString());
            menu.show();
        }
    };

    private final OnMenuItemClickListener audioCodecMenuitemListner = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            button_audioCodec.setText(item.toString());
            return false;
        }
    };

    private final OnMenuItemClickListener recordModeMenuitemListner = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            button_recordMode.setText(item.toString());
            return false;
        }
    };

    private final OnClickListener ondeleteButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Builder builder = new Builder(getContext());
            builder.setMessage("Delete Current Profile?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    };

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    videoMediaProfiles.remove(currentProfile.ProfileName);
                    currentProfile = null;
                    SettingsManager.getInstance().saveMediaProfiles(videoMediaProfiles);
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
        editText_width.setText("");
        editText_height.setText("");
        button_recordMode.setText("");
    }

    private void setMediaProfile(VideoMediaProfile profile)
    {
        if (profile == null){
            clearProfileItems();
            return;}
        currentProfile = profile.clone();
        button_profile.setText(profile.ProfileName);
        editText_profilename.setText(profile.ProfileName);
        editText_audiobitrate.setText(profile.audioBitRate+"");
        editText_audiosamplerate.setText(profile.audioSampleRate+"");
        editText_videobitrate.setText(profile.videoBitRate+"");
        editText_videoframerate.setText(profile.videoFrameRate+"");
        editText_maxrecordtime.setText(profile.duration+"");
        editText_height.setText(profile.videoFrameHeight+"");
        editText_width.setText(profile.videoFrameWidth+"");
        switch_Audio.setChecked(profile.isAudioActive);
        int videocodec = profile.videoCodec;
        switch (videocodec)
        {
            case 1:
                button_videoCodec.setText(VideoCodecs.H263.toString());
                break;
            case 2:
                button_videoCodec.setText(VideoCodecs.H264.toString());
                break;
            case 3:
                button_videoCodec.setText(VideoCodecs.MPEG_4_SP.toString());
                break;
            case 4:
                button_videoCodec.setText(VideoCodecs.VP8.toString());
                break;
            case 5:
                button_videoCodec.setText(VideoCodecs.HEVC.toString());
                break;
        }

        for (AudioCodecs audio : AudioCodecs.values())
        {
            if (audio.GetInt() == profile.audioCodec)
                button_audioCodec.setText(audio.toString());
        }

        button_recordMode.setText(profile.Mode.toString());
    }

    private final OnClickListener onSavebuttonClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (currentProfile == null) {
                Toast.makeText(getContext(),"Pls Select first a profile to edit", Toast.LENGTH_SHORT).show();
                return;
            }
            currentProfile.audioBitRate = Integer.parseInt(editText_audiobitrate.getText().toString());
            currentProfile.audioSampleRate = Integer.parseInt(editText_audiosamplerate.getText().toString());

            currentProfile.videoBitRate = Integer.parseInt(editText_videobitrate.getText().toString());
            currentProfile.videoFrameRate = Integer.parseInt(editText_videoframerate.getText().toString());
            currentProfile.duration = Integer.parseInt(editText_maxrecordtime.getText().toString());
            currentProfile.isAudioActive = switch_Audio.isChecked();
            currentProfile.Mode = VideoMode.valueOf((String) button_recordMode.getText());
            currentProfile.videoFrameHeight = Integer.parseInt(editText_height.getText().toString());
            currentProfile.videoFrameWidth = Integer.parseInt(editText_width.getText().toString());

            VideoCodecs videoCodec = VideoCodecs.valueOf((String)button_videoCodec.getText());
            currentProfile.videoCodec = videoCodec.GetInt();

            AudioCodecs audioCodec = AudioCodecs.valueOf((String)button_audioCodec.getText());
            currentProfile.audioCodec = audioCodec.GetInt();

            //if currentprofile has no new name the the profile in videomediaprofiles gets updated
            if (videoMediaProfiles.containsKey(editText_profilename.getText().toString()))
            {
                videoMediaProfiles.put(currentProfile.ProfileName, currentProfile);
            }
            else // it has a new name add it as new profile
            {
                VideoMediaProfile p = currentProfile.clone();
                p.ProfileName = editText_profilename.getText().toString().replace(" ","_");
                videoMediaProfiles.put(p.ProfileName, p);
            }
            SettingsManager.getInstance().saveMediaProfiles(videoMediaProfiles);
            videoMediaProfiles.clear();
            Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_SHORT).show();
        }
    };
}
