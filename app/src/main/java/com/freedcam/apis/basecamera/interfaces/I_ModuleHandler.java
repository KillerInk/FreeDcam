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

package com.freedcam.apis.basecamera.interfaces;

import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.I_worker;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_ModuleHandler
{
    /**
     * Load the new module
     * @param name of the module to load
     */
    void SetModule(String name);

    /**
     * Get the name of the current module
     * @return name of moduke
     */
    String GetCurrentModuleName();

    /**
     * get the current module instace
     * @return current active module
     */
    AbstractModule GetCurrentModule();

    /**
     * Start work on the current modulé
     * @return
     */
    boolean DoWork();

    /**
     * Add worklistner that listen to the current module
     * @param workerListner to add
     */
    void SetWorkListner(I_worker workerListner);

    void initModules();
}
