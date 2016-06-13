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

package com.freedcam.ui.themesample.subfragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by troop on 15.02.2016.
 */
public class VideoProfileEditorFragment extends Fragment
{
    final String TAG = VideoProfileEditorFragment.class.getSimpleName();
    private Button button_profile;
    private EditText editText_profilename;
    private EditText editText_audiobitrate;
    private EditText editText_audiosamplerate;
    private EditText editText_videobitrate;
    private EditText editText_videoframerate;
    private EditText editText_maxrecordtime;
    private Button button_recordMode;
    private Button button_save;
    private Button button_delete;
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
        button_save = (Button)view.findViewById(id.button_Save_profile);
        switch_Audio = (Switch)view.findViewById(id.switchAudio);
        button_recordMode = (Button)view.findViewById(id.button_recordMode);
        button_recordMode.setOnClickListener(recordModeClickListner);

        button_save.setOnClickListener(onSavebuttonClick);
        button_delete = (Button)view.findViewById(id.button_delete_profile);
        button_delete.setOnClickListener(ondeleteButtonClick);
        videoMediaProfiles = new HashMap<>();

        File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
        if(f.exists())
            try {
                VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
            } catch (IOException e) {
                Logger.exception(e);
            }
        AppSettingsManager appSettingsManager = new AppSettingsManager(getContext());
        try {
            setMediaProfile(videoMediaProfiles.get(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE)));
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
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
                    VideoMediaProfile.saveCustomProfiles(videoMediaProfiles);
                    videoMediaProfiles.clear();
                    File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
                    if(f.exists())
                        try {
                            VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
                        } catch (IOException e) {
                            Logger.exception(e);
                        }
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
        button_recordMode.setText("");
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
        switch_Audio.setChecked(profile.isAudioActive);
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
            VideoMediaProfile.saveCustomProfiles(videoMediaProfiles);
            videoMediaProfiles.clear();
            File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
            if(f.exists())
                try {
                    VideoMediaProfile.loadCustomProfiles(videoMediaProfiles);
                } catch (IOException e) {
                    Logger.exception(e);
                }
            Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_SHORT).show();
        }
    };
}
