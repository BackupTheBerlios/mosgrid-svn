/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */

package hu.sztaki.lpds.pgportal.services.pgrade;



public class GridConfiguration
    implements Comparable
{

    public GridConfiguration(String gridName)
    {
        name = null;
        isHost = null;
        isPort = null;
        isBaseDn = null;
        isType = 0;
        mpsUsername = null;
        mpsPass = null;
        name = gridName;
    }

    public String getISType()
    {
        return IS_TYPES[isType];
    }

    public void setMPSData(String mpsUsername, String mpsPass)
    {
        this.mpsUsername = mpsUsername;
        this.mpsPass = mpsPass;
    }

    public void defineIS(short isType, String isHost, String isPort, String isBaseDn)
    {
        this.isType = isType;
        this.isHost = isHost;
        this.isPort = isPort;
        this.isBaseDn = isBaseDn;
        //ISFacade.startMonitor(this);
    }

    public void removeIS()
    {
        //ISFacade.stopMonitor(this);
        isType = 0;
        isHost = null;
        isPort = null;
        isBaseDn = null;
    }

    public String getName()
    {
        return name;
    }

    public String getGenuineGridName()
    {
        return name;
    }

    public boolean isISDefined()
    {
        return isType != 0;
    }

    public String getISHost()
    {
        return isHost;
    }

    public String getISPort()
    {
        return isPort;
    }

    public String getISBaseDn()
    {
        return isBaseDn;
    }

    public String getMPSUsername()
    {
        return mpsUsername;
    }

    public String getMPSPass()
    {
        return mpsPass;
    }

    public int compareTo(Object other)
        throws ClassCastException
    {
        return getName().compareTo(((GridConfiguration)other).getName());
    }

    public String toString()
    {
        return getName() + " " +getName()+" "+ getISType() + (isHost == null ? "" : " " + isHost) + (isPort == null ? "" : " " + isPort) + (isBaseDn == null ? "" : " " + isBaseDn) + (mpsUsername == null ? "" : " " + mpsUsername) + (mpsPass == null ? "" : " " + mpsPass);
    }

    public static final String IS_TYPES[] = {
        "NOT_DEFINED", "MDS2", "LCG2"
    };

    private String name;
    private String isHost;
    private String isPort;
    private String isBaseDn;
    private short isType;
    private String mpsUsername;
    private String mpsPass;

}
