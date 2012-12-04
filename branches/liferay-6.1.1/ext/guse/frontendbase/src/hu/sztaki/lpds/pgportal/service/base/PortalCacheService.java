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
 * PortalCacheService.java
 * Static class of the data cached by the portal
 */

package hu.sztaki.lpds.pgportal.service.base;

import hu.sztaki.lpds.pgportal.service.base.data.UserData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Static class of the data cached by the portal
 *
 * Portal registry
 *
 * @author krisztian
 */
public class PortalCacheService 
{
    private static PortalCacheService instance=new PortalCacheService();
    private ConcurrentHashMap<String,UserData> users=new ConcurrentHashMap<String,UserData>();

    /**
     * Class constructor
     */
    public PortalCacheService() {/*System.out.println("CREATE:PortalCacheService");*/}
    
    /**
     * Returns the object's static instance
     * @return static object instance;
     */
    public static PortalCacheService getInstance(){return instance;}
        
    /**
     * Returns the given user's cache object
     * @param userID - user ID
     * @return object including the user's cached data;
     */
    public UserData getUser(String userID)
    {
        if(users.get(userID)==null)
        {
            users.put(userID, new UserData());
            ((UserData)users.get(userID)).initData(userID);
        }
        ((UserData)users.get(userID)).setTimeOut();
        return (UserData)users.get(userID);
    }
  
    /**
     * Actual time on the server,
     * return value in string.
     * @return time stamp (for example: 2007-05-17 13:42)
     */
    public String getNowDateTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(Calendar.getInstance().getTime());
    }

    /**
     * Actual time on the server,
     * return value in string.
     * @return time stamp with seconds(for example: 2007-05-17 13:42:33)
     * 
     */
    public String getNowDateTimeStampWithSeconds() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(Calendar.getInstance().getTime());
    }
    
    /**
     * Getting the timed workflows (for all users)
     * @return timed workflows list
     */
  

    /**
     * Returns the list of the users
     * 
     * (Enumeration)
     *
     * @return Enumeration - keys in the user's hashtable
     */
    public Enumeration getUserListEnum() {
        return users.keys();
    }

/**
 * get all users in portal cache
 * @return user hash
 * @return
 */
    public ConcurrentHashMap<String,UserData> getAllUsers(){return users;}
}
