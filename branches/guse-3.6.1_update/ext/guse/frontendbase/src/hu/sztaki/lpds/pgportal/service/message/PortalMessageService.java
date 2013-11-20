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
 * PortalMessageService.java
 */

package hu.sztaki.lpds.pgportal.service.message;

import java.util.*;
import java.sql.*;

import hu.sztaki.lpds.information.local.PropertyLoader;

/**
 * Class for managing the portal's user messages
 * 
 * (key - pairs text pairs and returns them)
 *
 * @author krisztian
 */
public class PortalMessageService 
{
    private Hashtable dict=new Hashtable();
    private static PortalMessageService instance=null;
    
    /**
     * Class constructor
     */
    public PortalMessageService() {}
    
    /**
     * Returns the object instance from the JVM
     * @return PortalMessageService object instance
     */
    public static PortalMessageService getI(){

        try{Class.forName(PropertyLoader.getInstance().getProperty("guse.system.database.driver"));}
        catch(Exception e){/*e.printStackTrace();*/}
        if(instance==null){instance=new PortalMessageService();}
        return instance;
    }

    private synchronized String getSQLMessage(String pID){
        String res="";
        Connection connection=null;
        try{
            connection=getMessageDBConnection();
            PreparedStatement proc=connection.prepareStatement("SELECT txt FROM messages_texts WHERE tkey=?");
            proc.setString(1,pID);
            ResultSet rs=proc.executeQuery();
            if(rs.next()) res=rs.getString(1);
            connection.close();
            return res;
        }
        catch(Exception e){
            try{connection.close();}catch(Exception e0){/*e0.printStackTrace();*/}
        }
        return res;
    }

    /**
     * Returns the required message
     * @param ID  message ID
     * @return text of the message
     */
    public String getMessage(String ID)
    {
        boolean b=false;
        if(dict.get(ID)==null) {
            String txt=getSQLMessage(ID);
            if(!"".equals(txt)) dict.put(ID,txt);
            else return "["+ID+"]";
       
        }
        if(PropertyLoader.getInstance().getProperty("portal.text.debug")==null)
        {
            if(b||(dict.get(ID)!=null))return ""+dict.get(ID);
            else   return "["+ID+"]";
        }
        else
            return ""+dict.get(ID)+"["+ID+"]";
    }
    
    /**
     * Returns the required message
     * @param ID message ID
     * @return text of the message
     */
    public String getMessageNoCache(String ID){
        String s=getSQLMessage(ID);
        if("".equals(s)) return "[ID]";
        else return s;
        
    }
    
    
    /**
     * Returns the messages 
     * which ID starts with the given ID
     * @param ID  message ID
     * @return text of the message
     */
    public Vector getMessages(String ID)
    {
        Vector res=new Vector();
        Enumeration enm=dict.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            if(key.startsWith(ID)){res.add(dict.get(key));}
        }
        return res;
    }
    
    /**
     * Returns the messages 
     * which ID starts with the given ID
     * @param ID  message ID
     * @return text and ID of the message
     */
    public synchronized  Hashtable getMessagesWithId(String ID){
//        System.out.println("getMessagesWithId:"+ID);

        Hashtable res=new Hashtable();
        Enumeration enm=dict.keys();
        while(enm.hasMoreElements()){
            String key=""+enm.nextElement();
            if(key.startsWith(ID))
                res.put(key,dict.get(key));
        }
        if(res.size()==0){
            Connection connection=null;
            try{
                connection=getMessageDBConnection();
                PreparedStatement proc=connection.prepareStatement("SELECT tkey,txt FROM messages_texts WHERE tkey like ?");
                proc.setString(1,ID+"%");
                ResultSet rs=proc.executeQuery();
                while(rs.next()){
                    res.put(rs.getString(1),rs.getString(2));
                }
                connection.close();
            } 
            catch(Exception e){/*not message connection*/ e.printStackTrace();}
        }

        return res;
    }
    
     /**
     * Modifying the message in the message cache
     * @param pKey    message ID
     * @param pValue    message
     */
    public void setMessage(String pKey, String pValue)
    {
        dict.put(pKey,pValue);
    }
/**
 * Adds a new connection to the database containing the texts which will be showed
 * @return SQL Connection
 * @throws java.sql.SQLException a new connection cannot be made with the current settings
 */
    public Connection getMessageDBConnection() throws SQLException{
        String dbURL=PropertyLoader.getInstance().getProperty("guse.system.database.url");
            String dbUser=PropertyLoader.getInstance().getProperty("guse.system.database.user");
            String dbPass=PropertyLoader.getInstance().getProperty("guse.system.database.password");
            return DriverManager.getConnection(dbURL, dbUser, dbPass);
    }
}
