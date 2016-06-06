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

package com.freedcam.apis.camera1.camera.modules;

import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.lge.media.CamcorderProfileEx;

/**
 * Created by troop on 24.02.2016.
 */
public class VideoMediaProfileLG extends VideoMediaProfile
{
   public VideoMediaProfileLG(CamcorderProfileEx ex, String ProfileName, VideoMode mode, boolean isAudioActive)
    {
        super(ex.audioBitRate,ex.audioChannels,ex.audioCodec,ex.audioSampleRate,ex.duration,ex.fileFormat,ex.quality,ex.videoBitRate,ex.videoCodec,ex.videoFrameRate,ex.videoFrameHeight,ex.videoFrameWidth,ProfileName,mode, isAudioActive);
    }
}
