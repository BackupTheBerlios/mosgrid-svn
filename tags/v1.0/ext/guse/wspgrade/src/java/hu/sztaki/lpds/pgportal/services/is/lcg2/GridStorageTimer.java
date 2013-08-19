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

/*
  * GridStorageTimer.java
  *
  * Created on February 10, 2005, 7:18 PM
  */

package hu.sztaki.lpds.pgportal.services.is.lcg2;

import java.util.Timer;
import java.util.TimerTask;
import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;
import hu.sztaki.lpds.pgportal.services.pgrade.GridConfiguration;
import hu.sztaki.lpds.pgportal.services.is.lcg2.ldap.QueryGridInfo;

/**
  *
  * @author  Tamas Boczko
  */
public class GridStorageTimer {

    private Timer timer;
    private GridConfiguration gc;
    private boolean isTimerStopped;    
    
    /** Creates a new instance of GridStorageTimer */
    public GridStorageTimer(GridConfiguration aGridConfiguration, int aExecutionPeriod) {
        this.isTimerStopped = false;
        this.gc= aGridConfiguration;
        this.timer = new Timer();
//HG change 2008.02.8        
//        MiscUtils.printlnLog(this.getClass().getName() + ".GridStorageTimer()","Constructor is called with ["+aExecutionPeriod+"].");
//HG cahnge end        
        this.timer.schedule(new UpdaterTask(aGridConfiguration),
	               0,        //initial delay
	               aExecutionPeriod*1000);  //subsequent rate        
    }

    public void stop(){
//HG change 2008.02.8             
//     MiscUtils.printlnLog(this.getClass().getName() + ".stop()","called.");
//HG change end        
        this.isTimerStopped = true;
        this.timer.cancel();
        GridStorage.getInstance().removeGrid(this.gc.getName());
    }
    
    
    public boolean getTimerStopped(){
        return this.isTimerStopped;
    }

    class UpdaterTask extends TimerTask {
        private GridConfiguration gc;
        
        public UpdaterTask(GridConfiguration aGridConfiguration){
            super();
            this.gc = aGridConfiguration;
        }
        
        public void run() {
            long start = System.currentTimeMillis();
//            MiscUtils.printlnLog(this.getClass().getName(),"["+this.gc.getName()+"] is STARTING.");
            
            GridStorage gs = GridStorage.getInstance();
//            LCGGrid lcgGrid = gs.getGrid(this.gc.getName());
            LCGGrid lcgGrid = new LCGGrid(this.gc.getName());
            
            // Getting the info
            boolean isSuccess = QueryGridInfo.getGridInfo( lcgGrid, this.gc);

            if (isSuccess) { lcgGrid.setIsDataAvailable(true); }
            else {lcgGrid.setIsDataAvailable(false); }
            
            lcgGrid.setIsExecutedFirstTime(true);
            
            gs.setGrid(lcgGrid);
//            MiscUtils.printlnLog(this.getClass().getName(),"["+this.gc.getName()+"] is FINISHED. Ellapsed time :"+ (System.currentTimeMillis() - start) );
        }
    }    
    
    
}
