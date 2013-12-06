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
/*
 */

package hu.sztaki.lpds.pgportal.services.is.lcg2;

import java.util.Hashtable;
import hu.sztaki.lpds.pgportal.services.pgrade.GridConfiguration;
import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;

/**
  *
  * @author  Tamas Boczko
  */
public class GridStorageTimerManager {
    
    private static GridStorageTimerManager instance;
    private Hashtable timerStore;

    /** Creates a new instance of MDSResourceListStorageTimerManager */
    public GridStorageTimerManager() {
        this.timerStore = new Hashtable();
    }

    public static GridStorageTimerManager getInstance(){
        if (GridStorageTimerManager.instance == null)
            GridStorageTimerManager.instance = new GridStorageTimerManager();
        return GridStorageTimerManager.instance;
    }

    public void startTimerForGrid(GridConfiguration aGridConfiguration){
    // HG change 2008.02.08    
    //    MiscUtils.printlnLog(this.getClass().getName() + ".startTimerForGrid("+aGridConfiguration.getName()+")","called.");
    // HG change end    
        this.timerStore.put(aGridConfiguration.getName(),
                            new GridStorageTimer(aGridConfiguration, LCGInformationSystem.getInstance().getRefreshPeriod()));
    }

    public void stopTimerForGrid(GridConfiguration gc){
      GridStorageTimer gST = (GridStorageTimer)this.timerStore.get(gc.getName());
      if ( gST!= null){
        gST.stop();
        this.timerStore.remove(gc.getName());
      }
    }
    
}
