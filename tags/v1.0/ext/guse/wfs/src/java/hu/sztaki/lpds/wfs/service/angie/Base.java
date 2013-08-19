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
 * Base.java
 * Statuszvaltozasok aktualiassagat vizsgalo osztaly, adatbazis kapcsolat kezeles
 */

package hu.sztaki.lpds.wfs.service.angie;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.utils.Status;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author krisztian
 */
public class Base 
{
/** Statikus osztaly peldany */    
    private static Base instance=null;
/** Job statuszok nyilvantartasa */    
    private Hashtable data=new Hashtable();
    private Hashtable<String,JobDataCacheBean> jobidcache=new Hashtable<String,JobDataCacheBean>();

    private boolean saveProperty,endProperty;

    private StatusHandlerService serviceStatusHandler=null;
/**
 * Class constructor
 */    
    public Base() 
    {
        saveProperty="true".equals(PropertyLoader.getInstance().getProperty("guse.wfs.system.savestatus"));
        endProperty="true".equals(PropertyLoader.getInstance().getProperty("guse.wfs.system.saveonlyendstatus"));
        if(saveProperty){
            serviceStatusHandler=new StatusHandlerService();
            serviceStatusHandler.start();
        }
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException
    {
        Class.forName(PropertyLoader.getInstance().getProperty("guse.system.database.driver"));

         String dbURL=PropertyLoader.getInstance().getProperty("guse.system.database.url");
         String dbUser=PropertyLoader.getInstance().getProperty("guse.system.database.user");
         String dbPass=PropertyLoader.getInstance().getProperty("guse.system.database.password");

        return DriverManager.getConnection(dbURL, dbUser, dbPass);

    }
/**
 * Statikus osztalypeldanyt biztosito metodus
 * @see hu.sztaki.lpds.wfs.service.angie.Base
 */    
    public static Base getI()
    {
        if(instance==null)instance=new Base();
        return instance;
    }

    public void persistStatusItems(Vector<JobStatusBean> pValue) throws Exception {
        if (saveProperty) {
            for (JobStatusBean tmp : pValue) {
                if (setLastModify(tmp) || (tmp.getTim() == (-1))) {
                    if (endProperty && Status.isEndStatus(tmp.getStatus())) {
                            synchronized (this) {serviceStatusHandler.addPersistItem(tmp);}
                    }
                    else {synchronized (this) {serviceStatusHandler.addPersistItem(tmp);}}
                }
            }
        }
    }

   private String getKeyFromStatusBean(JobStatusBean tmp) throws Exception {
       return tmp.getWrtID()+"#"+tmp.getWorkflowSubmitID()+"#"+tmp.getJobID()+"#"+tmp.getPID();
   }

/**
 * Job statusz valtozas ervenyesseg vizsgalata
 * @param JobStatusBean tmp - job statusz bean
 * @return true eseten ervenyes a statusz valtozas, false eseteben nem
 */    
    public boolean setLastModify(JobStatusBean tmp) throws Exception {
        String pKey = getKeyFromStatusBean(tmp);
        long pTim = tmp.getTim();
        //
        if(data.get(pKey)!=null)
        {
            if(((Long)data.get(pKey)).longValue()<pTim)
            {
                data.put(pKey, new Long(pTim));
                return true;
            }
            else {return false;}
        }
        else 
        {
            data.put(pKey, new Long(pTim));
            return true;
        }
    }
    
/**
 * Torol egy Job-ot a statusz ervenyessegi vizsgalat alol
 * @param JobStatusBean tmp - job statusz bean
 * @see String
 */    
    public void delete(JobStatusBean tmp) throws Exception {
        String pKey = getKeyFromStatusBean(tmp);
        //
        Enumeration enm=data.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            if(key.startsWith(pKey))data.remove(key);
        }
    
    }
    
    public void addJobIdToJobIdCache(String pPortalID, String pUserID, String pWFIF,String pJobID, String pID)
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) jobidcache.put(pPortalID+"."+pUserID+"."+pWFIF,new JobDataCacheBean());
        jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).setJobID(pJobID, pID);
    }

    public void addWorkflowIdToJobIdCache(String pPortalID, String pUserID, String pWFIF,String pID)
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) jobidcache.put(pPortalID+"."+pUserID+"."+pWFIF,new JobDataCacheBean());
        jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).setWorkflowid(pID);
    }
    
    public String getJobIdFromJobIdCache(String pPortalID, String pUserID, String pWFIF,String pJobID) throws NullPointerException
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) throw new NullPointerException("workflow not exist");
        else if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).getJobID(pJobID)==null) throw new NullPointerException("job not exist");
        else return jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).getJobID(pJobID);
    }

    public String getWorkflowIdFromJobIdCache(String pPortalID, String pUserID, String pWFIF) throws NullPointerException
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) throw new NullPointerException("workflow not exist");
        if("".equals(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).getWorkflowid())) throw new NullPointerException("workflowid not inited");
        return jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).getWorkflowid();
    }
    
    public void deleteFromJobIdCache(String pPortalID, String pUserID, String pworkflow)
    {
        jobidcache.remove(pPortalID+"."+pUserID+"."+pworkflow);
        deleteAllJobDescriptionFromCache(pPortalID, pUserID, pworkflow);
    }

    public void addJobDescriptionToCache(ComDataBean pValue, String pCacheData) {
        addJobDescriptionToCache(pValue.getPortalID(), pValue.getUserID(), pValue.getWorkflowID(), pValue.getWorkflowRuntimeID(), pValue.getJobID(), pCacheData);
    }

    public void addJobDescriptionToCache(String pPortalID, String pUserID, String pWFIF, String pWfrid, String pJobID,String pCacheData)
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) jobidcache.put(pPortalID+"."+pUserID+"."+pWFIF,new JobDataCacheBean());
        jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).addJobDescription(pWfrid, pJobID, pCacheData);
    }

    public String getJobDescriptionFromCache(ComDataBean pValue) throws NullPointerException
    {
        return getJobDescriptionFromCache(pValue.getPortalID(), pValue.getUserID(), pValue.getWorkflowID(), pValue.getWorkflowRuntimeID(), pValue.getJobID());
    }

    public String getJobDescriptionFromCache(String pPortalID, String pUserID, String pWFIF, String pWfrid, String pJobID) throws NullPointerException
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)==null) throw new NullPointerException("workflow not exist");
        return jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).getJobDescription(pWfrid, pJobID);
    }
    
    public void deleteJobDescriptionFromCache(String pPortalID, String pUserID, String pWFIF, String pWfrid) 
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)!=null)
            jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).deleteInstanceDesciptions(pWfrid);
    }
        
    public void deleteAllJobDescriptionFromCache(String pPortalID, String pUserID, String pWFIF) 
    {
        if(jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF)!=null)
            jobidcache.get(pPortalID+"."+pUserID+"."+pWFIF).deleteAllInstanceDesciptions();
    }
    
}
