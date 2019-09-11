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
 * Created by troop on 09.12.2014.
 */
public interface ModuleHandlerInterface
{
    /**
     * Load the new module
     * @param name of the module to load
     */
    void setModule(String name);

    /**
     * Get the name of the current module
     * @return name of moduke
     */
    String getCurrentModuleName();

    /**
     * get the current module instace
     * @return current active module
     */
    ModuleInterface getCurrentModule();

    /**
     * Start work on the current modulé
     * @return
     */
    boolean startWork();

    /**
     * Set Low storage flag
     */
    void SetIsLowStorage(Boolean x);

    void initModules();
}
