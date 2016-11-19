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

package freed.cam.apis.basecamera.modules;

/**
 * Created by troop on 26.11.2014.
 */
public interface CaptureStates
{
    final int RECORDING_STOP = 0;
    final int RECORDING_START = 1;
    final int IMAGE_CAPTURE_START=2;
    final int IMAGE_CAPTURE_STOP = 3;
    final int CONTINOUSE_CAPTURE_START = 4;
    final int CONTINOUSE_CAPTURE_STOP = 5;
    final int CONTINOUSE_CAPTURE_WORK_START =6;
    final int CONTINOUSE_CAPTURE_WORK_STOP =7;
    final int CONTINOUSE_CAPTURE_STOP_WHILE_WORKING = 8;
    final int CONTINOUSE_CAPTURE_STOP_WHILE_NOTWORKING = 9;
}
