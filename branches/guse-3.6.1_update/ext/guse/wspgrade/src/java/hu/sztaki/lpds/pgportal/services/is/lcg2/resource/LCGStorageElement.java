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
import java.text.DecimalFormat;
/**
  *
  * @author  Tamas Boczko 
  * revised by G Hermann
  */
public class LCGStorageElement {
    
    // keys
    // HG changed 2008.02.05 begin
    public static final int STATE_OK = 0,
                            STATE_NOT_AVAIL=1,
                            STATE_UNRELIABLE=2;
    public static final int KIND_CLASSIC=0,
                            KIND_TAPE=1,
                            KIND_CASTOR=2,  
                            KIND_SRM=3,
                            KIND_UNKNOWN=4,
                            KIND_VOPART =5;
    private static final String K_multidisk ="multidisk",
                                K_disk      ="disk",
                                K_tape      ="tape",
                                K_srm       ="srm",
                                K_srm_v1    ="srm_v1",
                                K_castor    ="castor"; 
            
    private  int state= STATE_NOT_AVAIL,
                 kind = KIND_UNKNOWN;
    private boolean fullSE;
    public boolean isFull() {return fullSE;}
    public void setState(int st){state=st;}
    public boolean isStateOk(){return state == STATE_OK;}
    
    
    public void setKind(String s)
    
    {  if      (s.compareTo(this.K_disk)     ==0 ) kind = KIND_CLASSIC;
       else if (s.compareTo(this.K_multidisk)==0 ) kind = KIND_CLASSIC;
       else if (s.compareTo(this.K_castor)   ==0 ) kind = KIND_CASTOR;
       else if (s.compareTo(this.K_srm)      ==0 ) kind = KIND_SRM;
       else if (s.compareTo(this.K_srm_v1)   ==0 ) kind = KIND_SRM;
       else if (s.compareTo(this.K_tape)     ==0 ) kind = KIND_TAPE;
       
    }
    private String badStateMessage()
    { 
        String ret="";
        if      (state == STATE_NOT_AVAIL)  ret = "N/A";
        else if (state == STATE_UNRELIABLE) ret = "bad value";
        return ret;
    }
    public int  getState(){return state;}
    
    public int  getKind() {return kind;}
   // HG cahanged end 
    private String hostName;
    private String vo;
    
    private long availableSpace;
   
    private long usedSpace;
    private long totalSpace;

    
    /** Creates a new instance of LCGStorgeElement */
    public LCGStorageElement(String seHostName, String seVO) {
        this.hostName = seHostName;
        this.vo = seVO;
        state= STATE_NOT_AVAIL;
        kind = KIND_UNKNOWN;
        fullSE = false;
    }
    
    public void setTotalAndAvalable(long total,long available)
    {
        state = STATE_OK;
        totalSpace = total;
        availableSpace = available;
        usedSpace = totalSpace - availableSpace;
        fullSE = true;
    }
    public void setAvalableAndUsed(long available,long used)
    
    {
        state = STATE_OK;
        kind  = KIND_VOPART; 
        availableSpace = available;
        usedSpace = used;
    }
    
    public String getHostName(){
        return this.hostName;
    }
    public String getVoOrKind()
    {
        String ret = "kind = ";
        switch (this.getKind())
            {
                case KIND_CLASSIC: { ret += "Classic"; break; } 
                case KIND_CASTOR:  { ret += "Castor";  break; }
                case KIND_SRM:     { ret += "Srm";     break; }
                case KIND_TAPE:    { ret += "Tape";    break; }
                case KIND_VOPART:  { ret = this.getVO(); break; }
                default: ret += "unknown";
            }    
                
        return ret;
    }

    public String getVO(){
        return this.vo;
    }
    
    public long getAvailableSpaceLong(){
        long ret = 0;
        if ( isStateOk()) ret = availableSpace;
        return ret;
    }
    
    public String getAvailableSpace(){
        String ret = "";
        if ( isStateOk()) ret = MiscUtils.getSpaceSizeFromKB( this.availableSpace );
        else              ret =this.badStateMessage();
        return ret;

    }    
    
     public String getUsedSpace(){
        String ret = "";
        if ( isStateOk()) ret = MiscUtils.getSpaceSizeFromKB( this.usedSpace );
        else              ret =this.badStateMessage();
        return ret;
    }
     
    public long getUsedSpaceLong(){
        long ret = 0;
        if ( isStateOk()) ret = usedSpace;
        return ret;
    }
      
    
     public String getTotalSpace(){
        String ret = "";
        if ( isStateOk()&& this.fullSE ) ret = MiscUtils.getSpaceSizeFromKB( this.totalSpace );
        else              ret =this.badStateMessage();
        return ret;
    }
     
    public long getTotalSpaceLong(){
        long ret = 0;
        if(!this.fullSE) ret = 0;
        else if ( isStateOk()) ret = totalSpace;
        return ret;
    }
      
    
    //HG change 2008.01.29 new 
    public void addToTotal_old(long value)
    {
        this.totalSpace += value;
    }    
    // HG change end
    
   
    public void setTotalSpace_old(long l){ 
        
        this.totalSpace = l;
     
    }    
 // HG changed end   
    public final int getSpacePercent(){
        String p = this.getSpacePercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }    
    
    public final String getSpacePercentStr(){
        String ret ="-";
        long total = this.totalSpace;
        if ( this.isStateOk())
        {
            if (! this.fullSE  ) total = this.availableSpace + this.usedSpace; 
            if ( total != 0)
            {    
               DecimalFormat myFormatter = new DecimalFormat("###");
               ret = myFormatter.format( 100 - ( (double)this.availableSpace / (double)total * 100 ) );  
            }
        }
        return ret;
    }    
    
}
