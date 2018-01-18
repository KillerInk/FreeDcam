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

package freed.cam.apis.basecamera;

/**
 * Created by troop on 24.08.2015.
 */
public class Size
{
    public Integer width;
    public Integer height;
    public Size(int w, int h)
    {
        height = h;
        width = w;
    }
    public Size(String s)
    {
        String[] split = s.split("x");
        if (split.length == 2) {
            height = Integer.parseInt(split[1]);
            width = Integer.parseInt(split[0]);
        }
        else
            new Size(1280,720);
    }


}