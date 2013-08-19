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
 * Property loader
 */

package hu.sztaki.lpds.information.local;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Hashtable;
import java.util.List;


/**
 * @author krisztian karoczkai
 */
public class PropertyLoader
{

  Properties props = null;
  static PropertyLoader instance = null;
  static String propFile = null;
  static Hashtable<String,String> properties=new Hashtable<String,String>();

/**
 * Request of the singleton class instance
 * @return singleton instance
 */  
  public static synchronized PropertyLoader getInstance()
  {
    if (PropertyLoader.instance == null) {PropertyLoader.instance = new PropertyLoader(); }
    return PropertyLoader.instance;
  }

  /**
   * Constructor.
   * @throws java.lang.Exception If loadProperties() fails.
   */
  private PropertyLoader()
  {
      if(properties==null) properties=new Hashtable<String, String>();
      properties.put("portal.prefix.dir",System.getProperty("java.io.tmpdir")+"/");
      properties.put("prefix.dir",System.getProperty("java.io.tmpdir")+"/");
  }


  /**
   * Gets all property's name and value.
   * @return The property hash<key,value>.
   */
  public Hashtable<String,String> getAllPropertyes(){return properties;}

  /**
   * Gets property's value.
   * @param name Name of the property.
   * @return The propertys' value.
   */
  public synchronized String getProperty(String name)
  {
      try{return properties.get(name);}
      catch(Exception e){System.out.println("loggg");e.printStackTrace();}
      return "";
  }

  /**
   * Gets properties key, when start prefix.
   * @param pPrefix  Prefix of property
   * @return The properties' key.
   */
  public List<String> getPropertiesKey(String pPrefix){
     List<String> res=new ArrayList<String>();
     Enumeration<String> enm=properties.keys();
     String key;
     while(enm.hasMoreElements()){
        key=enm.nextElement();
        if(key.startsWith(pPrefix)) res.add(key);
     }
     return res;
  }

  /**
   * Gets property's value for User.
   * @param pUser name of user
   * @param pName Name of the property.
   * @return The propertys' value.
   */
  public synchronized String getUserProperty(String pUser, String pName)
  {
        String res=getProperty(pUser+"."+pName);
        if("".equals(res)) res=getProperty(pName);
        return res;
  }

  /**
   * is valid property's key?.
   * @param name Name of the property.
   * @return true=valid
   */
  public synchronized boolean isProperty(String name)
  {
      return properties.get(name)!=null;
  }

  /**
   * Sets property's value.
   * @param pkey  Name of the property.
   * @param pValue Value of the property.
   */
  public void setProperty(String pkey,String pValue)
  {
      properties.put(pkey, pValue);
      System.out.println("SET-PROPERTY:"+pkey+"="+properties.get(pkey));
  }

 

}