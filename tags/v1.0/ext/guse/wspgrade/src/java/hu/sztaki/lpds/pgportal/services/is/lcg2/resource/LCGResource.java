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

package hu.sztaki.lpds.pgportal.services.is.lcg2.resource;

import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;

import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.LinkedHashSet;

import java.text.DecimalFormat;

/**
  *
  * @author  boci
  */
public class LCGResource implements hu.sztaki.lpds.pgportal.services.is.Resource {
    
    private String siteId;   // { mds-vo-name=siteId }
    private String siteName;


    private int sumTotalCpu;
    private int sumFreeCpu;
    private int sumRunningJob;
    private int sumWaitingJob;
    
    private long sumAvailableSpace;
//    private long sumUsedSpace;
    private long sumTotalSpace;
//HG change 2008.01.29
    private boolean sumSpaceUseable;
    private boolean sumSpaceOneceCalculated;
    private long actVoTotalUsed,
                 actVoTotalAvailable;    
//HG cahange end    
    
//HG change 2005-08-19
//HG change 2006-07-17
//old    
    
//    private TreeMap hostList;
// new
      private TreeMap diffVoHostNumFreeNumList,
                      diffHostNumFreeNumList;
//HG change end       
//HG change end    
    private TreeMap ceList;
    private TreeMap seList;
    private TreeMap subClusterList;
    
// HG changes 2005.05.25
// new    
    private HashSet touchSEhost;
// HG changes end    
    
//    private boolean isTouched;
    
    /** Creates a new instance of Resource */
    public LCGResource() {
        this.init();
    }

    public LCGResource(String siteId){
        this.siteId = siteId;
        this.init();
    }

    private void init(){
        this.ceList = new TreeMap();
        this.seList = new TreeMap();
        this.subClusterList = new TreeMap();
  //HG change 2005-08-19
  //HG change 2006-07-17
  //old      
  //      this.hostList = new TreeMap();
  //new
          this.diffVoHostNumFreeNumList = new TreeMap();
          this.diffHostNumFreeNumList   = new TreeMap();
  //HG change end      
        
//        this.isTouched = false;
        
        this.siteName = "";
        this.sumTotalCpu = -999;
        this.sumFreeCpu = -999;
        this.sumRunningJob = -999;
        this.sumWaitingJob = -999;
        
        this.sumAvailableSpace = -999;
//        this.sumUsedSpace = -999;
        this.sumTotalSpace = -999;
        
      
        
// HG changes 2005.05.25
// new        
        touchSEhost = new HashSet();
        
//if (siteName.compareTo("CERN-CIC")==0) System.out.println("<<<<<<< LCGResource for CERN-CIC");        
        
// HG change end; 
// HG cahanged 2008.02.05
        this.sumSpaceOneceCalculated=false;
        this.sumSpaceUseable=false;
// HG cahanged end        
    }
    
// HG changes 2005.05.25
// new     
    private boolean firstOccurenceOfSE( String name)
    {
       
        String probe=name.trim();
        boolean ret= ! touchSEhost.contains(probe);
        touchSEhost.add(probe);
//if (siteName.compareTo("CERN-CIC")==0) System.out.println("------->>>> first of "+name+" ="+ret +"id="+ this.hashCode());
    
        return ret;
    }    
// HG change end    
    
// Implementing Resource Interface:
    
    public java.util.Hashtable getBasicProperties() {
        return new java.util.Hashtable();
    }
    
    public String getName() {
        return this.siteName;
    }
    
//********* PUBLIC FUNC *********************************

    public String getSiteName(){
        return this.siteName;
    }
    
    public void setSiteName(String siteName){
        this.siteName = siteName;
// HG changes 2005.05.25
// new        
        
        
 //if (siteName.compareTo("CERN-CIC")==0) System.out.println("<<<<<<< LCGResource for CERN-CIC separately");        
        
// HG change end;     
    }
    
    public String getSiteId(){
        return this.siteId;
    }
    
/*    
    public boolean getIsTouched(){
        return this.isTouched;
    }
    
    public void setIsTouched(boolean value){
        this.isTouched = value;
    }    
  */  
    
    
// CE methods start    
    
    public final String getSumTotalCpu(){
        if (this.sumTotalCpu == -999) return "N/A";
        else return String.valueOf(this.sumTotalCpu);
    }

    public final String getSumFreeCpu(){
        if (this.sumFreeCpu == -999) return "N/A";
        else return String.valueOf(this.sumFreeCpu);
    }

    public final int getCpuPercent(){
        String p = this.getCpuPercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }
    
    public final String getCpuPercentStr(){
        if (this.sumTotalCpu == -999 || this.sumFreeCpu == -999 || this.sumTotalCpu == 0   ) return "-";
        else {
            DecimalFormat myFormatter = new DecimalFormat("###");
            return myFormatter.format( 100 - ((double)this.sumFreeCpu / (double)this.sumTotalCpu * 100 ) );
        }
    }
    
    public final String getSumRunningJob(){
        if (this.sumRunningJob == -999) return "N/A";
        else return String.valueOf(this.sumRunningJob);
    }

    public final String getSumWaitingJob(){
        if (this.sumWaitingJob == -999) return "N/A";
        else return String.valueOf(this.sumWaitingJob);
    }
    
    public final int getJobPercent(){
        String p = this.getJobPercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }
    
    public final String getJobPercentStr(){
        if (this.sumWaitingJob == -999 || this.sumRunningJob == -999 ) return "-";
        int totalJob = this.sumWaitingJob + this.sumRunningJob;
        if (totalJob == 0) return "0";
        else {
            DecimalFormat myFormatter = new DecimalFormat("###");
            return myFormatter.format( (double)this.sumWaitingJob / (double)totalJob * 100);
        }
    }
    
//HG cahnage 2007.07.17
//new    
    private String getVoHostNumFreeNum(LCGComputingElement ce)
    {
        String res="";
        res= res+ce.getTotalCpuInt()+"_"+ce.getRunningJobInt()+"_"+ce.getWaitingJobInt();    
        return res;
    }
    private String getHostNumFreeNum(LCGComputingElement ce)
    {
        String res="";
        res= res+ce.getTotalCpuInt();    
        return res;
    }    
//HG cahnage end 
    
    public void addCE(LCGComputingElement ce){
        String key = ce.getHostName() + ce.getCeName() + ce.getLrmsType(); 
        this.ceList.put( key , ce);
        
//HG changes 2005.08.19
//old
//      this.updateSumsForCE(ce);
// contoll whether the computing element belons to a new host
        // String hostKey = ce.getHostName();

//HG cahange 2006.07.17   
        
//old
/*        
        String hostKey = ce.getHostName() + ce.getCeName();
        if( hostList.get(hostKey)== null)
        {
          this.hostList.put( hostKey , ce);
          this.updateSumsForCE(ce); 
*/
//new 
        String voHostNumFreeNumKey = getVoHostNumFreeNum(ce),
                 hostNumFreeNumKey = getHostNumFreeNum(ce);
        boolean changeHost    = diffHostNumFreeNumList.get(hostNumFreeNumKey) == null;
        boolean changeProcess = diffVoHostNumFreeNumList.get(voHostNumFreeNumKey) == null;
        if(changeHost) 
           diffHostNumFreeNumList.put(hostNumFreeNumKey,ce);  
        if(changeProcess) 
           diffVoHostNumFreeNumList.put(voHostNumFreeNumKey,ce);  
        this.updateSumsForCE(ce,changeHost,changeProcess);

 // HG changes end       
    }
    
    public void addCE(String hostName, String ceName, String lrmsType){
        
        String key =  hostName + ceName +  lrmsType;
/*        
        Object o = this.ceList.get(key);
        if ( o == null) {
            */
//            LCGComputingElement ce = new LCGComputingElement(hostName, ceName , lrmsType);
//            ce.setIsTouched(true);
            this.ceList.put( key , new LCGComputingElement(hostName, ceName , lrmsType) );
/*        }
        else {
            ((LCGComputingElement)o).setIsTouched(true);
        }
 */
    }
    
    public LCGComputingElement getCE(String hostName, String ceName, String lrmsType) {
        return (LCGComputingElement)this.ceList.get( hostName + ceName +  lrmsType );
    }
    
    public LCGComputingElement[] getCEList(){
        LCGComputingElement[] lcgCE = new LCGComputingElement[this.ceList.size()];
        Collection values = this.ceList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgCE[i] = (LCGComputingElement)it.next();
        }
        return lcgCE;
    }    

//HG change 2006.07.17
// old    
//    public void updateSumsForCE(LCGComputingElement ce){
// new
      private void updateSumsForCE(LCGComputingElement ce, boolean changeHost,boolean changeProcess){
//HG change end
          
//HG change 2006.07.17
// new
     if(changeHost)
     {
//HG cahange end         
        int totalCpu = ce.getTotalCpuInt();
        if (totalCpu != -999 ) { 
            //HG changes 2006.05.10
            //old
            /*
              if (this.sumTotalCpu == -999) this.sumTotalCpu = 0;
              this.sumTotalCpu += totalCpu;
            */ 
            // new
             //HG changes 2006.07.17
            //old
            /*
             if (this.sumTotalCpu == -999) this.sumTotalCpu = totalCpu;
             */
            //new
              if (this.sumTotalCpu == -999) this.sumTotalCpu = 0;
              this.sumTotalCpu += totalCpu;
            
             //HG change end
           //HG change end
        }
        
        int freeCpu = ce.getFreeCpuInt();
        if (freeCpu != -999 ) { 
            //HG changes 2006.05.10
            //old
            /*
              if (this.sumFreeCpu == -999) this.sumFreeCpu = 0;
              this.sumFreeCpu += freeCpu; 
             */
            // new 
             //HG changes 2006.07.17
            //old
            /*
            
              if (this.sumFreeCpu == -999) this.sumFreeCpu = freeCpu; 
             */
            //new
              if (this.sumFreeCpu == -999) this.sumFreeCpu = 0;
              this.sumFreeCpu += freeCpu; 
            
            //HG change end
            //HG change end
        }
//HG change 2006.07.17
//new
     }
     if(changeProcess)
     {
//HG cahange end         
        int runningJob = ce.getRunningJobInt();
        if (runningJob != -999 ) { 
            if (this.sumRunningJob == -999) this.sumRunningJob = 0;
            this.sumRunningJob += runningJob; 
        }

        int waitingJob = ce.getWaitingJobInt();
        if (waitingJob != -999 ) { 
            if (this.sumWaitingJob == -999) this.sumWaitingJob = 0;
            this.sumWaitingJob += waitingJob; 
        }
//HG change 2006.07.17
//new
     }
//HG cahange end     
    }

// SE methods start ***********************************************************       

    //HG changed 2008.02.07 new      
    public final String getSumAvailableSpace(String vo)
    {
        String ret="N/A";
        if (vo.compareTo("All")==0) ret =getSumAvailableSpace();
        else
        {
            if (setVoStore(vo))
            {
                ret = MiscUtils.getSpaceSizeFromKB( this.actVoTotalAvailable );
            }    
        }
        return ret;
    }
    // changed end
    
    public final String getSumAvailableSpace(){
    //HG changed 2008.02.06
    // new
        setTotalStore();
        if (!this.sumSpaceUseable) return "N/A";
        else return MiscUtils.getSpaceSizeFromKB( this.sumAvailableSpace ); 
    // old
    /*    
        if (this.sumAvailableSpace == -999) return "N/A";
//        else return String.valueOf(this.sumAvailableSpace);
        else return MiscUtils.getSpaceSizeFromKB( this.sumAvailableSpace );
     */
     //HG change end   
    }
/*
    public final String getSumUsedSpace(){
        if (this.sumUsedSpace == -999) return "N/A";
//        else return String.valueOf(this.sumUsedSpace);
        else return MiscUtils.getSpaceSizeFromKB( this.sumUsedSpace );        
    }
*/
    //HG changed 2008.02.07 new      
    public final String getSumTotalSpace(String vo)
    {
        String ret="N/A";
        if (vo.compareTo("All")==0) ret =getSumTotalSpace();
        else
        {
            if (setVoStore(vo))
            {
                ret = MiscUtils.getSpaceSizeFromKB( this.actVoTotalAvailable + this.actVoTotalUsed );
            }    
        }
        return ret;
    }
    //HG changed end
    
    public final String getSumTotalSpace(){
        // HG change 2008.01.29 start 
        setTotalStore();
        // HG cahange end
        // HG changed 2008.02.06
        if (!this.sumSpaceUseable) return "N/A";
        // old
        // if (this.sumTotalSpace == -999) return "N/A";
        // HG cahng end
        else return MiscUtils.getSpaceSizeFromKB( this.sumTotalSpace );        
    }
    
    //HG changed 2008.02.07 new      
    public final int getSpacePercent(String vo)
    {
        int ret;
        if (vo.compareTo("All")==0) ret =getSpacePercent();
        else
        {
            String p = this.getSpacePercentStr(vo);
            if (p.equals("-")) ret = -1;
            else {
                   try {
                        ret = Integer.valueOf(p).intValue();
                   }
                   catch (NumberFormatException e) {e.getMessage(); return -1;}
             }              
        }
        return ret;
    }    
    //HG changed end
    
    public final int getSpacePercent(){
        // HG change 2008.01.29 start 
        setTotalStore();
        // HG cahange end
        
        String p = this.getSpacePercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }    
    
    //HG changed 2008.02.07 new      
    public final String getSpacePercentStr(String vo)
    {
        String ret="-";
        if (vo.compareTo("All")==0) ret =getSpacePercentStr();
        else
        {
            if (setVoStore(vo))
            {
                long total = this.actVoTotalAvailable + this.actVoTotalUsed;
                if (total != 0)
                {
                  DecimalFormat myFormatter = new DecimalFormat("###");
                  ret = myFormatter.format( 100 - ( (double)this.actVoTotalAvailable / (double)total * 100 ) );  
                }    
            }    
        }
        return ret;
    }
    //HG changed end
    
    public final String getSpacePercentStr(){
        // HG change 2008.01.29 start 
        setTotalStore();
        // HG cahange end
        // HG changed 2008.02.06
        // new
        if (!this.sumSpaceUseable || this.sumTotalSpace == 0   ) return "-";
        // old
        // if (this.sumAvailableSpace == -999 || this.sumTotalSpace == -999 || this.sumTotalSpace == 0   ) return "-";
        // HG change end
        
        else {
            DecimalFormat myFormatter = new DecimalFormat("###");
            return myFormatter.format( 100 - ( (double)this.sumAvailableSpace / (double)this.sumTotalSpace * 100 ) );            
        }
    }
    
// HG change 2008.01.29 start   
    
    private LCGStorageElement findSe(String vo)
    {
        LCGStorageElement ret= null;
        if (seList != null)
        {
             Iterator i = seList.values().iterator();
             while(i.hasNext())
             {
                 try
                 {        
                     LCGStorageElement actSE = (LCGStorageElement)i.next();
                     if (actSE.getVO().compareTo(vo)== 0)
                     {ret = actSE; break;}    
                 }
                 catch (NoSuchElementException e){} 
             }     
        }    
        return ret;
    }
   //HG change 2008.02.07
    private boolean setVoStore(String vo)
    {
        boolean ret= false;
//System.out.println("setVoStore() called vo= "+ vo);       
             Set hosts= new LinkedHashSet();
             actVoTotalUsed=0;
             actVoTotalAvailable=0;
             Iterator i = seList.values().iterator();
             int state= 0; //virgin=0, at least one arrived = 1 , error occured = 2
             while(i.hasNext())
             {
                 try
                 {        
                     LCGStorageElement actSE = (LCGStorageElement)i.next();
                     if(!actSE.isFull() && actSE.getVO().compareTo(vo)==0)
                     {  
//System.out.println( "setVoStore() has found the vo=" + vo      );                         
                        if(actSE.isStateOk())
                        {    
                           String actHost =  actSE.getHostName();
                           if (! hosts.contains(actHost))
                           {
                              if(state == 0) state = 1;
                              if(state == 1)
                              {    
                                 hosts.add(actHost);
                                 actVoTotalUsed   += actSE.getUsedSpaceLong();
                                 actVoTotalAvailable += actSE.getAvailableSpaceLong();
                              }   
                           }
                        }
                        else { state = 2;}
                     }
                     
                 }    
                 catch (NoSuchElementException e){}    
             
             }    
            if (state == 1)
            {
               ret = true;
            }    
          
       return ret;     
    }
   //HG change end  
   
    private void setTotalStore()
    {
        if (!this.sumSpaceOneceCalculated)
        {
             Set hosts= new LinkedHashSet();
             long accTotalStorage=0,
                  accTotalAvailable=0;
             Iterator i = seList.values().iterator();
             int state= 0; //virgin=0, at least one arrived = 1 , error occured = 2
             while(i.hasNext())
             {
                 try
                 {        
                     LCGStorageElement actSE = (LCGStorageElement)i.next();
                     if(actSE.isFull())
                     {    
                        if(actSE.isStateOk())
                        {    
                           String actHost =  actSE.getHostName();
                           if (! hosts.contains(actHost))
                           {
                              if(state == 0) state = 1;
                              if(state == 1)
                              {    
                                 hosts.add(actHost);
                                 accTotalStorage   += actSE.getTotalSpaceLong();
                                 accTotalAvailable += actSE.getAvailableSpaceLong();
                              }   
                           }
                        }
                        else { state = 2;}
                     }
                     
                 }    
                 catch (NoSuchElementException e){}    
             
             }    
            if (state == 1)
            {
                this.sumTotalSpace     = accTotalStorage;
                this.sumAvailableSpace = accTotalAvailable;
                this.sumSpaceUseable = true;
            }    
            this.sumSpaceOneceCalculated =true;
        }    
    }
    
    
//HG  change 2008.01.30 new
//old
/*    
    public void addSE(LCGStorageElement se)     
    {
        long   ownUsedSpace = se.getUsedSpaceLong();
        String ownHost    =   se.getHostName();
        String key        =   ownHost + se.getVO();
        if (! seList.containsKey(key))
        {    
           seList.put(key,se);
           Iterator i = seList.values().iterator();
           while ( i.hasNext())
           {
               
             LCGStorageElement actSe;  
             try
             {
                 
               if (ownHost.compareTo(((actSe =(LCGStorageElement)i.next())).getHostName())==0)
               {
                actSe.addToTotal(ownUsedSpace);
               }    
              
             }    
             catch (NoSuchElementException e){}    
           }
        }
    }
*/    
 // new 
     public void addSE(LCGStorageElement se){
        String key =  se.getHostName() + se.getVO();
        this.seList.put( key, se );
     }
 // HG change ended    
 // old   
 /*   
    public void addSE(LCGStorageElement se){
        String key =  se.getHostName() + se.getVO();
        this.seList.put( key, se );
        this.updateSumsForSE(se);
    }
 */
// HG change end    
    public void addSE(String hostName, String vo){
        String key =  hostName + vo;
/*        Object o = this.seList.get(key);
        if ( o == null) {
 */
//            LCGStorageElement se = new LCGStorageElement(hostName, vo);
//            se.setIsTouched(true);
            this.seList.put( key , new LCGStorageElement(hostName, vo) );
/*        }
        else {
            ((LCGStorageElement)o).setIsTouched(true);
        }
 */
    }
    
    public LCGStorageElement getSE(String hostName, String vo) {
        return (LCGStorageElement)this.seList.get( hostName +  vo );
    }
    
    public LCGStorageElement[] getSEList(){
        LCGStorageElement[] lcgSE = new LCGStorageElement[this.seList.size()];
        Collection values = this.seList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgSE[i] = (LCGStorageElement)it.next();
        }
        return lcgSE;
    }
    
// HG cahanged 2008 begin     
    void updateSumsForSE_obsolateNotUsed(LCGStorageElement se){ 
// old:      
//    public void updateSumsForSE(LCGStorageElement se){
// HG changed end        
        long availableSpace = se.getAvailableSpaceLong();
// HG change 2005.05.25
//new
// HG change 2006.07.18
// old        
//        boolean first = firstOccurenceOfSE( se.getHostName());  
//new
        boolean first = firstOccurenceOfSE( ""+se.getTotalSpace());  
//new
//HG change end 2006.07.18        
// HG change end        
        if (availableSpace != -999 ) { 
 // HG change 2005.05.25
 // old
 /*
             if (this.sumAvailableSpace == -999) this.sumAvailableSpace = 0;
            this.sumAvailableSpace += availableSpace;  
  */           
 // new
            if( first)
            {
               if (this.sumAvailableSpace == -999) this.sumAvailableSpace = 0;
//if (siteName.compareTo("CERN-CIC")==0)
//   System.out.println("<<<["+siteName+"] AV "+se.getHostName()+ " avOld="+ sumAvailableSpace + " add="+availableSpace);                  
               this.sumAvailableSpace += availableSpace;               
            }    
            else if (this.sumAvailableSpace == -999)  
            {
// if (siteName.compareTo("CERN-CIC")==0)
//   System.out.println("<<<["+siteName+"] AV "+se.getHostName()+ " avOld="+ sumAvailableSpace + " add="+availableSpace);                
               this.sumAvailableSpace = availableSpace;  
            }    
 // HG change end             

        }
/*        
        long usedSpace = se.getUsedSpaceLong();
        if (usedSpace != -999 ) { 
            if (this.sumUsedSpace == -999) this.sumUsedSpace = 0;
            this.sumUsedSpace += usedSpace; 
        }
  */      
        long totalSpace = se.getTotalSpaceLong();
        if (totalSpace != -999 ) { 
 // HG change 2005.05.25
 // old
 /*
            if (this.sumTotalSpace == -999) this.sumTotalSpace = 0;
            this.sumTotalSpace += totalSpace;   
  */           
 // new
            if (first)                
            {
               if (this.sumTotalSpace == -999) this.sumTotalSpace = 0;
//if (siteName.compareTo("CERN-CIC")==0)
//   System.out.println("<<<["+siteName+"] TO "+se.getHostName()+ " lenghOld="+ sumTotalSpace + " add="+totalSpace);   
               this.sumTotalSpace += totalSpace;                  
            }
            else if (this.sumTotalSpace == -999)
            {
//if (siteName.compareTo("CERN-CIC")==0)
//   System.out.println("<<<["+siteName+"] TO "+se.getHostName()+ " lenghOld="+ sumTotalSpace + " add="+totalSpace);                 
                 this.sumTotalSpace = totalSpace;                
            }    
 // HG change end             

        }

    }
    
    
// Sub CLuster methods start ***********************************************************       

    public void addSubCluster(LCGSubCluster sc){
       this.subClusterList.put( sc.getSubClusterName() , new LCGSubCluster( sc.getSubClusterName() ) );
    }
    
    public void addSubCluster(String subClusterName){
//        Object o = this.subClusterList.get( subClusterName );
//        if ( o == null ) {
//            LCGSubCluster sc = new LCGSubCluster(subClusterName);
//            sc.setIsTouched(true);
            this.subClusterList.put( subClusterName ,new LCGSubCluster(subClusterName) );
/*        }
        else {
            ((LCGSubCluster)o).setIsTouched(true);
        }
 */
    }
    
    public LCGSubCluster getSubCluster(String subClusterName) {
        return (LCGSubCluster)this.subClusterList.get( subClusterName );
    }
    
    public LCGSubCluster[] getSubClusterList(){
        LCGSubCluster[] lcgSC = new LCGSubCluster[this.subClusterList.size()];
        Collection values = this.subClusterList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgSC[i] = (LCGSubCluster)it.next();
        }
        return lcgSC;
    }    
 
// Misc methods ****************************************************************    
    
    public void printData(){
        this.printCEList();
        this.printSEList();
    }
    
    
// Private methods *************************************************************    
    
    private void printCEList(){
        
    }

    private void printSEList(){
        LCGStorageElement[] lcgSE = this.getSEList();
        for (int i = 0; i < lcgSE.length; i++) {
            System.out.println(lcgSE[i].getHostName() +" - " +
                                lcgSE[i].getVO() + " - " + 
                                lcgSE[i].getAvailableSpace() + " - " + 
                                lcgSE[i].getUsedSpaceLong());
        }
        System.out.println("SE DATA STARTING..." );
    }
    
/*
    private void cleanCEList(){
        Set keySet = this.ceList.keySet();
        LCGComputingElement lcgCE;
        String key;
        for (Iterator it = keySet.iterator(); it.hasNext(); ) {
             key = (String)it.next();
             lcgCE = (LCGComputingElement)this.ceList.get(key);
             if ( lcgCE.getIsTouched() ) { lcgCE.setIsTouched(false); }
             else { 
                 it.remove();
             //    this.ceList.remove(key);
             }
        }
    }

    private void cleanSEList(){
        Set keySet = this.seList.keySet();
        LCGStorageElement lcgSE;
        String key;
        for (Iterator it = keySet.iterator(); it.hasNext(); ) {
             key = (String)it.next();
             lcgSE = (LCGStorageElement)this.seList.get(key);
             if ( lcgSE.getIsTouched() ) { lcgSE.setIsTouched(false); }
             else { 
                 it.remove();
                 // this.seList.remove(key); 
             }
        }
    }

    private void cleanSubClusterList(){
        Set keySet = this.subClusterList.keySet();
        LCGSubCluster lcgSC;
        String key;
        for (Iterator it = keySet.iterator(); it.hasNext(); ) {
             key = (String)it.next();
             lcgSC = (LCGSubCluster)this.subClusterList.get(key);
             if ( lcgSC.getIsTouched() ) { lcgSC.setIsTouched(false); }
             else { 
                 it.remove();
             //    this.subClusterList.remove(key);
             }
        }
        
    }
  */  
    class CEKey {
        private String lrmsType;
        private String ceName;
        
        public CEKey(String aLrmsType, String aCeName) {
            lrmsType = aLrmsType;
            ceName = aCeName;
        }
        
        public String getLrmsType(){
            return lrmsType;
        }
       
        public String getCeName(){
            return ceName;
        }
        
        public boolean equals(Object o){
            try {
                CEKey key = (CEKey)o;
                if ( key.getLrmsType().equals(this.lrmsType) && key.getCeName().equals(this.ceName) )  {
                    return true;
                }
                return false;
            }
            catch (ClassCastException e) {
                return false;
            }
        }
        
        public int hashCode(){
            long code = this.lrmsType.hashCode() + this.ceName.hashCode();
            int i = (int)code;
            return i;
        }        
        
    }
    
    
}
