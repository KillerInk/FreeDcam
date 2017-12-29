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

package freed.cam.ui.themesample.handler;

import freed.ActivityAbstract;
import freed.cam.apis.basecamera.FocusHandlerInterface;

/**
 * Created by troop on 09.06.2015.
 */
abstract class AbstractFocusImageHandler implements FocusHandlerInterface
{
    /**
     * Holds toplayerd fragment that has created this
     */
    ActivityAbstract fragment;

    /**
     *
     * @param view the view that contains the focus imageviews
     * @param fragment the toplayerd fragment wich create this
     */
    AbstractFocusImageHandler(ActivityAbstract fragment)
    {
        this.fragment = fragment;
    }


}
