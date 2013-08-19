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
 * ServiceType.java
 * Communication service descriptor
 */

package hu.sztaki.lpds.information.com;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * @author krisztian karoczkai
 */
public class ServiceType
{
    private Hashtable prop=new Hashtable();

/**
 * Class constructor
 */    
    public ServiceType() {}
    
/**
 * Returns the object used to call the service
 * @return the name of the client object
 */    
    public String getClientObject(){return ""+prop.get("comtype");}
    
/**
 * Returns the service URL
 * @return the access URL of the service
 */    
    public String getServiceUrl(){return ""+prop.get("url");}

/**
 * Returns the safe URL of the service
 * @return the access URL of the service
 */    
    public String getSecureServiceUrl(){return ""+prop.get("surl");}
    
/**
 * Returns the service ID
 * @return the serviceID
 */    
    public String getServiceID(){return ""+prop.get("service");}
    
/**
 * Sets the service ID
 * @param pValue The service ID needed to be set
 */    
    public void setServiceID(String pValue){prop.put("service",pValue);}
    
/**
 * Sets the service URL
 * @param pValue The service URL needed to be set
 */    

    public void setServiceUrl(String pValue){prop.put("url",pValue);}
/**
 * Sets the safe service URL
 * @param pValue The service URL needed to be set
 */    
    public void setSecureServiceUrl(String pValue){prop.put("surl",pValue);}
    
/**
 * Sets the client class used during communication
 * @param pValue The communication class needed to be set
 */    
    public void setClientObject(String pValue){prop.put("comtype",pValue);}
    
/**
 * Sets an attribute of the service
 * @param key key of attribute
 * @param value value of attribute
 */    
    public void set(String key, String value){prop.put(key, value);}
    
/**
 * Returns if the given instance of the object 
 * is eligible for the parameter conditions
 * @param value attribute hash
 * @return true/false
 */    
    public boolean isCorrect(Hashtable value)
    {


        boolean res=true;
        Enumeration enm=value.keys();
        enm=value.keys();
        String key="";
        while(enm.hasMoreElements()&&res)
        {
            key=""+enm.nextElement();
            res=res&((""+value.get(key)).trim().equals((""+prop.get(key)).trim()));
        }
        return res;
    }
    
/**
 * Equality check
 * @param pValue service URL+resource URI
 * @return true/false
 */    
    public boolean isCorrect(String pValue)
    {
        return pValue.equals(""+prop.get("url")+prop.get("service"));
    }
/**
 * @see Object
 * @return
 */
    @Override
    public String toString()
    {
        StringBuffer res=new StringBuffer();
        Enumeration<String> enm=prop.keys();
        String key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            res.append(key+"="+prop.get(key)+";");
        }
        return res.toString();
    }


}
