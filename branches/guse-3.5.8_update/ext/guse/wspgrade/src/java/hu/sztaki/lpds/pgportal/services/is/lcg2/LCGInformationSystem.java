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
  * LCGInformationSystem.java
  *
  * Created on February 7, 2005, 4:52 PM
  */

package hu.sztaki.lpds.pgportal.services.is.lcg2;
import hu.sztaki.lpds.pgportal.services.is.ISUtils;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.services.pgrade.GridConfiguration;
import hu.sztaki.lpds.pgportal.services.pgrade.GridConfigs;
import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;
// HG change 2006.11.22
// add
import java.util.Date;
// HG change end

/**
  *
  * @author  boci
  */
public class LCGInformationSystem  {
    
    public static LCGInformationSystem instance;
    
    private boolean isLcg2Enabled;
    private int ldapConnectionTimeout;
    private int refreshPeriod;
    
    private String prefixDir;
    
    private static boolean isPropertiesLoaded;  // true: if ALL! properties were loaded successfully, false otherwise

//    private static GridConfiguration[] gcList;
    
    public static LCGInformationSystem getInstance(){
        if (LCGInformationSystem.instance == null) {
            LCGInformationSystem.instance = new LCGInformationSystem();
        }
        return LCGInformationSystem.instance;
    }    
    
    /** Creates a new instance of LCGInformationSystem */
    public LCGInformationSystem() {
        this.init();
    }

    private void loadProperties(){
        String p= new String("");
        try {
//            MiscUtils.printlnLog("hu.sztaki.lpds.pgportal.services.is.lcg2.LCGInformationSystem.loadProperties()","Called.");
            PropertyLoader pl = PropertyLoader.getInstance();
            
            p = "is.lcg2.enabled"; 
            this.isLcg2Enabled = Boolean.valueOf(pl.getProperty(p)).booleanValue();
//            LCGInformationSystem.isLcg2Enabled = true;
            if ( this.isLcg2Enabled ) {
            
                p = "is.lcg2.resource.list.refresh.period";
                this.refreshPeriod = Integer.parseInt(pl.getProperty(p));
//                LCGInformationSystem.refreshPeriod = 120;
//                System.out.println("is.mds2.resource.list.refresh.period: " + MDSInformationSystem.refreshPeriod);
                
                p = "is.lcg2.ldap.connection.timeout";
                this.ldapConnectionTimeout = Integer.parseInt(pl.getProperty(p));
                //LCGInformationSystem.ldapConnectionTimeout = 15000;

                p = "portal.prefix.dir";
                this.prefixDir = pl.getProperty(p);
                this.isPropertiesLoaded = true;                
            }
        }
        catch (NumberFormatException nFE) {
            nFE.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    public void init(){
        loadProperties();
        if (this.isLcg2Enabled) {
            GridStorage.getInstance().init();
//            GridConfigs.getInstance().getGridConfigs();
            
/*            gcList = new GridConfiguration[2];
            gcList[0] = new GridConfiguration("EGEE-Testzone");
            gcList[0].defineIS((short)2, "lxn1189.cern.ch", "2170", "mds-vo-name=local,o=grid");
            
            gcList[1] = new GridConfiguration("EGEE-HunGrid");
            gcList[1].defineIS((short)2, "grid152.kfki.hu", "2170", "mds-vo-name=local,o=grid");
//            GridConfiguration gc = new GridConfiguration("EGEE", "lxn1189.cern.ch", "2170", "mds-vo-name=local,o=grid");
            startLCGMonitor(gcList[0]);
            startLCGMonitor(gcList[1]);
//            startLCGMonitor(gcList[2]);
 */
        }
    }
    
    public boolean getLcg2Enabled(){
        return this.isLcg2Enabled;
    }
    
    public int getLdapConnectionTimeout(){
        return this.ldapConnectionTimeout;
    }
    
    public int getRefreshPeriod(){
        return this.refreshPeriod;
    }
    
    public String getPrefixDir(){
        return this.prefixDir;
    }
    
// Implementing InformationSystem interface
/*    
    public Resource[] getResources() {
        Resource[] res = new Resource[1];
        return res;        
    }
    
    public ResourceDetails getResourceDetails(Resource resource) {
        return new ResourceDetails();
    }
*/

// Public methods *************************************************************        
    

/*    
    public static GridConfiguration[] getGridConfigs(){
        return gcList;
    }
  
 */
    
/*    
    public static LCGResource[] getResourceListOnline(String gridName){
        if (LCGInformationSystem.isPropertiesLoaded) {
            GridConfiguration gc = new GridConfiguration("EGEE", "lxn1189.cern.ch", "2170", "mds-vo-name=local,o=grid");        
            return QueryResourceList.getResources(gc);
        }
        else{
           MiscUtils.printlnLog(LCGInformationSystem.class.getName() + ".getResourceListOnline()","The properties couldn't be loaded successfully from the property file so cannot get resources. See log above for more details.");
           return null;
            
        }
    }
*/
    public LCGGrid getGridInfo(String gridName){
        if (this.isPropertiesLoaded) {
            return GridStorage.getInstance().getGrid(gridName);
        }
        else {
           MiscUtils.printlnLog(this.getClass().getName() + ".getResourceList()","The properties couldn't be loaded successfully from the property file so cannot get resources. See log above for more details.");
           return null;
        }
    }
    
    public boolean isValidLCG(String ldapBdiiHost, String ldapBdiiPort, String ldapBaseDn){
    // HG changed 2008.02.08     
    //    MiscUtils.printlnLog(this.getClass().getName() + ".isValidLCG()", "called.");
    // HG changed end    
        if ( MiscUtils.isHostValid(ldapBdiiHost) 
           &&  MiscUtils.isPortValid(ldapBdiiPort) 
           &&  ISUtils.isBaseDnValid(ldapBaseDn) ) return true;
        else return false;        
    }
    
    public void startLCGMonitor(GridConfiguration gc){
    // HG changed 2008.02.08    
    //    MiscUtils.printlnLog(this.getClass().getName() + ".startLCGMonitor("+gc.getName()+")", "called.");
    // HG changed end    
        if (this.isLcg2Enabled)  {
            GridStorageTimerManager.getInstance().startTimerForGrid(gc);
        }
    }
    
    public void stopLCGMonitor(GridConfiguration gc){
    // HG changed 2008.02.08    
    //    MiscUtils.printlnLog(this.getClass().getName() + ".stopMDSMonitor("+gc.getName()+")", "called.");
    // HG changed end    
        if (this.isLcg2Enabled)  {
            GridStorageTimerManager.getInstance().stopTimerForGrid(gc);        
        }
    }
    
    // 
    public boolean isExecutedFirstTime(String gridName){
        if (! this.isLcg2Enabled)  { return false; }
        LCGGrid lcgG = GridStorage.getInstance().getGrid(gridName);
        if ( lcgG == null ) { return false; }
        if ( lcgG.getIsExecutedFirstTime() ) { return true; }
        return false;
    }
    
   // HG change new 2006.11.22
   // Checks the existence of the grid data and tries to reload if the previous refrishment was older than elapsedSeconds  
   synchronized public boolean isExecutedRecently(String gridName, long elapsedSeconds)
   {
       // HG  change 2008.02.08  more changes deletion of printings  
       // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "called."); 
       boolean ret = isExecutedFirstTime(gridName);
       if (ret)
       {
           LCGGrid lcgG = GridStorage.getInstance().getGrid(gridName);
           if (new Date().getTime() > (lcgG.getCreationTime() + 1000*elapsedSeconds))
           {
              GridConfiguration gc = GridConfigs.getInstance().getGridConfig(gridName);
              stopLCGMonitor(gc);
              // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "--gc stopped."); 
              startLCGMonitor(gc);
              // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "--gc started."); 
              long time = new Date().getTime();
              ret= false;
              while((time +this.ldapConnectionTimeout) > new Date().getTime())
              {
                    
                   ret = isExecutedFirstTime(gridName);
                   // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "--ret in while="+ret); 
                   if(ret)
                   {
                       if (GridStorage.getInstance().getGrid(gridName).getIsDataAvailable())
                       {
                           // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "--break."); 
                           break;
                       }
                       else
                           ret =false;                           
                       
                   }    
                   
                   try {
                         wait(1000);
                   } 
                   catch (InterruptedException ex) {
                      ex.printStackTrace();
                   }
                   
              }   
              
              
               
           }    
       }
       // MiscUtils.printlnLog(this.getClass().getName() + ".isExecutedRecently(gridname="+gridName+",elapsedSeconds="+elapsedSeconds+")", "--Terminated as="+ret); 
       return ret;
   }
   // HG change end 
    
    public boolean isDataAvailable(String gridName){
        if (! this.isLcg2Enabled)  { return false; }
        LCGGrid lcgG = GridStorage.getInstance().getGrid(gridName);
        if ( lcgG == null ) { return false; }
        if ( lcgG.getIsDataAvailable() ) { return true; }
        return false;
    }
    
    
}
