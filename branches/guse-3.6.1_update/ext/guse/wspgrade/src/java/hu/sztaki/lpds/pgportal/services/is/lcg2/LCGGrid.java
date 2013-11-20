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

import hu.sztaki.lpds.pgportal.services.is.lcg2.resource.LCGResource;
import hu.sztaki.lpds.pgportal.services.is.lcg2.resource.ResourceComparator;
import hu.sztaki.lpds.pgportal.services.is.lcg2.vo.*;

import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
//HG change 2006.11.22
//add
import java.util.Date;
//HG change end

import java.util.regex.*;
/**
  *
  * @author  Tamas Boczko
  */
public class LCGGrid {
    
    private String gridName;
    private TreeMap resourceList;
    private TreeMap voList;    
    private boolean isExecutedFirstTime;
    private boolean isDataAvailable;
    //HG changed 2006.11.22
    // new
    private long     dateOfCreation; 
    //HG change  end
    
    /** Creates a new instance of LCGGrid */
    public LCGGrid() {
        this.init();
    }
   
    public LCGGrid(String gridName) {
        this.gridName = gridName;
        this.init();
    }
// HG change 2005.05.25
//old    
//    private void init(){
    public void init(){
// HG change end        
        this.resourceList = new TreeMap(new ResourceComparator());
        this.voList = new TreeMap();
    }
    
    public String getGridName(){
        return this.gridName;
    }
    
    public void addResource(String siteId) {
//        Object o = this.resourceList.get(siteId);
        if ( this.resourceList.get(siteId) == null ) {
            LCGResource res = new LCGResource(siteId);
            res.setSiteName(siteId);
//            res.setIsTouched(true);
            this.resourceList.put(siteId, res);
        }        
//        else {
//            ((LCGResource)o).setIsTouched(true);
//        }
    }
    
    public LCGResource getResource(String siteId){
        return (LCGResource)this.resourceList.get(siteId);
    }
/*    
    public LCGResource[] getResourceList(){
        LCGResource[] lcgR = new LCGResource[this.resourceList.size()];
        
        Collection values = this.resourceList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgR[i] = (LCGResource)it.next();
        }
        return lcgR;
    }
*/
    
    public LCGResource[] getResourceList(String selectedVO){
        // if ALl resource must be presented
        if (selectedVO.equals("")) { 
            return this.getAllResourceList();
        }
        else {
           return this.getFilteredResourceList(selectedVO);
        }
    }
    
    public void addVo(String voName, String siteId){
        if ( ! this.voList.containsKey(voName.toLowerCase()) ) {
//            System.out.print("LCGGrid.addVO()- "+ voName+" is adding - ");
            if ( this.isVONameValid(voName) ) { 
//                System.out.println("Done.");
                this.voList.put( voName.toLowerCase() , new LCGVO(voName, siteId) );
            }
        }
        else {
            ((LCGVO)this.voList.get( voName.toLowerCase() )).addSupportedResource(siteId);
        }
    }
    
    public LCGVO[] getVOList(){
        LCGVO[] lcgVO = new LCGVO[this.voList.size()];
        Collection values = this.voList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgVO[i] = (LCGVO)it.next();
        }
        return lcgVO;
    }

    public boolean getIsExecutedFirstTime(){
        return this.isExecutedFirstTime;
    }
    
    public void  setIsExecutedFirstTime(boolean value){
        this.isExecutedFirstTime = value;
        
    // HG change 2006.11.22 
    //new    
        if (this.isExecutedFirstTime) 
             dateOfCreation = new Date().getTime();
        else dateOfCreation = 0;    
    // HG change end        
    }
    
    // HG change 2006.11.22
    public long getCreationTime()
    {
        return dateOfCreation;
    }
    // HG change end
    
    public boolean getIsDataAvailable(){
        return this.isDataAvailable;
    }
    
    public void setIsDataAvailable(boolean value){
        this.isDataAvailable = value;
    }
    
    public void printData(){
        this.printResourceData();
    }
    
/*    
    public void removeNotUsedComponents(){
        this.removeNotUsedComponentsFromResources();
        // to do from VOs !
    }
    
    public void clearAllComponentsData(){
        Collection values = this.resourceList.values();
        LCGResource lcgR;
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            lcgR = (LCGResource)it.next();
//            lcgR.clearAllComponentsData();
        }
    }
  */  
// Private methods *************************************************************
    
    private boolean isVONameValid(String voName){
    
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9|.|-]*$");
       
        Matcher matcher = pattern.matcher(voName);
        if (matcher.find()) {   return true;     }        
        else return false;
    }

    private LCGResource[] getAllResourceList(){
        LCGResource[] lcgR = new LCGResource[this.resourceList.size()];
        
        Collection values = this.resourceList.values();
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            lcgR[i] = (LCGResource)it.next();
        }
        return lcgR;
    }
    
    private LCGResource[] getFilteredResourceList(String voName){
        String[] sites = ((LCGVO)this.voList.get(voName.toLowerCase())).getSupportedResources();
        LCGResource[] lcgR = new LCGResource[sites.length];
        for (int i = 0; i< sites.length; i++) {
            lcgR[i] = (LCGResource)this.resourceList.get(sites[i]);
        }
        return lcgR;
    }
        
    private void printResourceData(){
        LCGResource[] lcgR = this.getAllResourceList();
        for (int i = 0; i < lcgR.length; i++){
            lcgR[i].printData();
        }
    }
        
        
/*    
    private void removeNotUsedComponentsFromResources(){
        Set keySet = this.resourceList.keySet();
        LCGResource lcgR;
        String key;
        for (Iterator it = keySet.iterator(); it.hasNext(); ) {
             key = (String)it.next();
             lcgR = (LCGResource)this.resourceList.get(key);
             if ( lcgR.getIsTouched() ) { 
                 lcgR.removeNotUsedComponents();
                 lcgR.setIsTouched(false);
             }
             else { it.remove();
                 //this.resourceList.remove(key);
             }
        }        
    }
  */  
}
