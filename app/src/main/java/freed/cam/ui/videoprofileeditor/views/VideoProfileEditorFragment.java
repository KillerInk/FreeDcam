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

package freed.cam.ui.videoprofileeditor.views;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.troop.freedcam.R;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.databinding.VideoProfileEditorFragmentBinding;

import java.util.HashMap;
import java.util.List;

import freed.cam.ui.videoprofileeditor.MyMediaCodec;
import freed.cam.ui.videoprofileeditor.enums.AudioCodecs;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;
import freed.cam.ui.videoprofileeditor.models.AudioCodecModel;
import freed.cam.ui.videoprofileeditor.models.EncoderModel;
import freed.cam.ui.videoprofileeditor.models.OpcodeModel;
import freed.cam.ui.videoprofileeditor.models.PopupModel;
import freed.cam.ui.videoprofileeditor.models.PreviewOpcodeModel;
import freed.cam.ui.videoprofileeditor.models.ProfileLevelModel;
import freed.cam.ui.videoprofileeditor.models.ProfileModel;
import freed.cam.ui.videoprofileeditor.models.RecordModel;
import freed.cam.ui.videoprofileeditor.models.VideoCodecModel;
import freed.cam.ui.videoprofileeditor.modelview.VideoProfileEditorModelView;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;

/**
 * Created by troop on 15.02.2016.
 */
public class VideoProfileEditorFragment extends Fragment {
    final String TAG = VideoProfileEditorFragment.class.getSimpleName();

    private VideoProfileEditorFragmentBinding videoProfileEditorFragmentBinding;
    private VideoProfileEditorModelView videoProfileEditorModelView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        videoProfileEditorFragmentBinding = DataBindingUtil.inflate(inflater,layout.video_profile_editor_fragment,null,false);
        videoProfileEditorModelView = new ViewModelProvider(this).get(VideoProfileEditorModelView.class);
        videoProfileEditorFragmentBinding.setVideoProfileModelView(videoProfileEditorModelView);
        return videoProfileEditorFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoProfileEditorFragmentBinding.buttonSaveProfile.setOnClickListener(onSavebuttonClick);
        videoProfileEditorFragmentBinding.buttonDeleteProfile.setOnClickListener(ondeleteButtonClick);
        videoProfileEditorModelView.getPopupModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                PopupMenu menu = null;
                Context context = new ContextThemeWrapper(getContext(), R.style.PopupStyle);
                if (((PopupModel)sender).getPopUpItemClick() instanceof ProfileModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonProfile);
                if (((PopupModel)sender).getPopUpItemClick() instanceof RecordModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonRecordMode);
                if (((PopupModel)sender).getPopUpItemClick() instanceof VideoCodecModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonVideoCodec);
                if (((PopupModel)sender).getPopUpItemClick() instanceof AudioCodecModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonAudioCodec);
                if (((PopupModel)sender).getPopUpItemClick() instanceof EncoderModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonVideoEnCoder);
                if (((PopupModel)sender).getPopUpItemClick() instanceof ProfileLevelModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonProfileLevel);
                if (((PopupModel)sender).getPopUpItemClick() instanceof PreviewOpcodeModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonPreviewOpcode);
                else if (((PopupModel)sender).getPopUpItemClick() instanceof OpcodeModel)
                    menu = new PopupMenu(context, videoProfileEditorFragmentBinding.buttonOpcode);
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ((PopupModel)sender).getPopUpItemClick().onPopupItemClick(item.toString());
                        return false;
                    }
                });
                List<String> strings = ((PopupModel)sender).getPopUpItemClick().getStrings();
                if (strings != null && strings.size() >0) {
                    for (String m : strings)
                        menu.getMenu().add(m);
                    menu.show();
                }
            }
        });
    }


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
                    videoProfileEditorModelView.getVideoMediaProfiles().remove(videoProfileEditorModelView.getProfile().ProfileName);
                    //videoProfileEditorModelView.setProfile(null);
                    SettingsManager.getInstance().saveMediaProfiles(videoProfileEditorModelView.getVideoMediaProfiles());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private int convertFromMinToMS(int time)
    {
        if (time !=0)
            return time *60 * 1000;
        return 0;
    }

    private long convertMbToByte(long mb)
    {
        if (mb != 0)
            return mb *1024;
        return 0;
    }
    private final OnClickListener onSavebuttonClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (videoProfileEditorModelView.getProfile() == null) {
                Toast.makeText(getContext(),"Pls Select first a profile to edit", Toast.LENGTH_SHORT).show();
                return;
            }
            videoProfileEditorModelView.getProfile().audioBitRate = Integer.parseInt(videoProfileEditorFragmentBinding.editTextAudioBitrate.getText().toString());
            videoProfileEditorModelView.getProfile().audioSampleRate = Integer.parseInt(videoProfileEditorFragmentBinding.editTextAudioSampleRate.getText().toString());

            videoProfileEditorModelView.getProfile().videoBitRate = Integer.parseInt(videoProfileEditorFragmentBinding.editTextVideoBitrate.getText().toString());
            videoProfileEditorModelView.getProfile().videoFrameRate = Integer.parseInt(videoProfileEditorFragmentBinding.editTextVideoframerate.getText().toString());
            videoProfileEditorModelView.getProfile().duration = convertFromMinToMS(Integer.parseInt(videoProfileEditorFragmentBinding.editTextRecordtime.getText().toString()));
            videoProfileEditorModelView.getProfile().maxRecordingSize = convertMbToByte(Long.parseLong(videoProfileEditorFragmentBinding.editTextRecordsize.getText().toString()));
            videoProfileEditorModelView.getProfile().isAudioActive = videoProfileEditorFragmentBinding.switchAudio.isChecked();
            videoProfileEditorModelView.getProfile().Mode = VideoMode.valueOf((String) videoProfileEditorFragmentBinding.buttonRecordMode.getText());
            videoProfileEditorModelView.getProfile().videoFrameHeight = Integer.parseInt(videoProfileEditorFragmentBinding.editTextProfileheight.getText().toString());
            videoProfileEditorModelView.getProfile().videoFrameWidth = Integer.parseInt(videoProfileEditorFragmentBinding.editTextProfilewidth.getText().toString());

            VideoCodecs videoCodec = VideoCodecs.valueOf((String)videoProfileEditorFragmentBinding.buttonVideoCodec.getText());
            videoProfileEditorModelView.getProfile().videoCodec = videoCodec.GetInt();

            AudioCodecs audioCodec = AudioCodecs.valueOf((String)videoProfileEditorFragmentBinding.buttonAudioCodec.getText());
            videoProfileEditorModelView.getProfile().audioCodec = audioCodec.GetInt();

            OpCodes opcodes = OpCodes.valueOf((String)videoProfileEditorFragmentBinding.buttonOpcode.getText());
            videoProfileEditorModelView.getProfile().opcode = opcodes.GetInt();

            OpCodes opcodes2 = OpCodes.valueOf((String)videoProfileEditorFragmentBinding.buttonPreviewOpcode.getText());
            videoProfileEditorModelView.getProfile().preview_opcode = opcodes2.GetInt();

            videoProfileEditorModelView.getProfile().videoHdr = videoProfileEditorFragmentBinding.videoHDR.isChecked();

            //if currentprofile has no new name the the profile in videomediaprofiles gets updated
            if (videoProfileEditorModelView.getVideoMediaProfiles().containsKey(videoProfileEditorFragmentBinding.editTextProfileName.getText().toString()))
            {
                videoProfileEditorModelView.getVideoMediaProfiles().put(videoProfileEditorModelView.getProfile().ProfileName, videoProfileEditorModelView.getProfile());
            }
            else // it has a new name add it as new profile
            {
                VideoMediaProfile p = videoProfileEditorModelView.getProfile().clone();
                p.ProfileName = videoProfileEditorFragmentBinding.editTextProfileName.getText().toString().replace(" ","_");
                videoProfileEditorModelView.getVideoMediaProfiles().put(p.ProfileName, p);
            }
            SettingsManager.getInstance().saveMediaProfiles(videoProfileEditorModelView.getVideoMediaProfiles());
            SettingsManager.getInstance().save();
            Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_SHORT).show();
        }
    };
}
