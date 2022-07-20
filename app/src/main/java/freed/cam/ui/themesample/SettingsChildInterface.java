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

package freed.cam.ui.themesample;

import android.view.View;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.event.module.ModuleChangedEvent;

/**
 * Created by troop on 16.06.2016.
 */
public interface SettingsChildInterface extends ModuleChangedEvent,View.OnClickListener
{
    /**
     * Set the parameter to work with
     * @param parameter
     */
    void SetParameter(ParameterInterface parameter);

    /**
     *
     * @return the stored parameters
     */
    ParameterInterface GetParameter();

    /**
     *
     * @return the stored string values
     */
    String[] GetValues();

    /**
     * Set the string
     * @param value to apply
     */
    void SetValue(String value);

    /**
     * Set the Listner for the click event
     * @param menuItemClick the listner
     * @param fromleft if true click was from left side else from right
     */
    void SetMenuItemClickListner(SettingsChildAbstract.SettingsChildClick menuItemClick, boolean fromleft);
}
