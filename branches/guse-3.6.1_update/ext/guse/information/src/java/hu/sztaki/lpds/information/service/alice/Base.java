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
 * InformationBase2 .java
 * Handling and storing services from server side
 */

package hu.sztaki.lpds.information.service.alice;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author krisztian
 */
public class Base 
{
    private static Base instance=null;
    private Vector serviceInfo=new Vector();
    Hashtable forbiddenService=new Hashtable();
    
/**
 * Class constructor
 */    
    public Base() {}
           
    
/** If it does not exist yet then create and give back the static object
 * @return Static object
 */
    public static Base getI()
    {
        if(instance==null)instance=new Base();
        return instance;
    }
    
/** Getting an eligible service descriptor
 * @param   pServiceType  service type
 * @param pFrom caller service
 * @param   pProp  needed parameters
 * @param   pFault  used until now but not eligible
 * @return service descriptor
 */
    public ServiceType getService(String pServiceType,String pFrom, Hashtable pProp, Vector pFault)
    {
        pProp.remove("stype");
        pProp.remove("from");
//        pProp.put("stype",pServiceType);
//        pProp.put("from", pFrom);
        ServiceType rdb=new ServiceType();
        try
        {
        if (pProp.containsKey("url"))
            if ("".equals(((String) pProp.get("url")).trim()))
                pProp.remove("url");

        List<ServiceResourceBean> dbServices=DH.getI().getServiceList(pFrom, pServiceType);
        if(pProp.get("url")!=null)
        {
            for(ServiceResourceBean t:dbServices)
            {
                for(GuseServiceBean gs:t.getDst().getServices())
                    if(gs.getUrl().equals(pProp.get("url")))
                    {
                        rdb.setClientObject(t.getCaller());
                        rdb.setServiceID(t.getRes());
                        rdb.setServiceUrl(gs.getUrl());
                        if(gs.getSurl()!=null)
                            rdb.setSecureServiceUrl(gs.getSurl());
                    }
            }
        }
        else
        {
            for(ServiceResourceBean srb:dbServices){
                Iterator<GuseServiceBean> guseServices=srb.getDst().getServices().iterator();
                GuseServiceBean gsb;
                while(guseServices.hasNext()){
                    gsb=guseServices.next();
                    if(isCorrectServiceProperty(gsb, pProp)){
                        rdb.setClientObject(srb.getCaller());
                        rdb.setServiceID(srb.getRes());
                        rdb.setServiceUrl(gsb.getUrl());
                        if(gsb.getSurl()!=null)
                            rdb.setSecureServiceUrl(gsb.getSurl());
                        return rdb;
                    }

                }
            }
        }
}
catch(Exception e){e.printStackTrace();}
        return rdb;
    }
/**
 * Looks for parameters which are contained in the property hash and the service also has them
 * @param pService gUSE Service descriptor bean
 * @param pProp property hash (<key,value>)
 * @return
 */
    private boolean isCorrectServiceProperty(GuseServiceBean pService, Hashtable<String,String> pProp){
        boolean res=true;
        Enumeration<String> paramPropsKeys=pProp.keys();
        String paramKey;
        String paramValue;
        List<String> paramValues;
        while(paramPropsKeys.hasMoreElements() && res){
            paramKey=paramPropsKeys.nextElement();
            if(paramKey.endsWith(".")){
                boolean tb=false;
                paramValues=getPropertyValues(pService, paramKey);
                for(String p:paramValues){
                    if(p.indexOf(pProp.get(paramKey))>(-1)) tb=true;
                }
                res=tb;
            }
            else{
                paramValue=getPropertyValue(pService, paramKey);
                if(paramValue!=null)
                    res=paramValue.indexOf(pProp.get(paramKey))>(-1);
                else res=false;
            }
        }
        return res;
    }
/**
 * Returns the value of the searched property from the service properties
 * @param pService service descriptor
 * @param pPropertyKey searched property key
 * @return null if a property does not exist with given key
 */
    private String getPropertyValue(GuseServiceBean pService, String pPropertyKey){
        String res=null;
        Collection<ServicePropertyBean> pProperties=pService.getProperties();
        Iterator<ServicePropertyBean> it=pProperties.iterator();
        ServicePropertyBean key;
        while(it.hasNext() && res==null){
            key=it.next();
            if(key.getPropkey().equals(pPropertyKey)) res=key.getPropvalue();
        }
        return res;
    }

/**
 * Returns the value of the searched properties from given service properties
 * @param pService service descriptor
 * @param pPropertyKey searched property key
 * @return null if a property does not exist with given key
 */
    private List<String> getPropertyValues(GuseServiceBean pService, String pPropertyKey){
        List<String> res=new ArrayList<String>();
        Collection<ServicePropertyBean> pProperties=pService.getProperties();
        Iterator<ServicePropertyBean> it=pProperties.iterator();
        ServicePropertyBean key;
        while(it.hasNext()){
            key=it.next();
            if(key.getPropkey().startsWith(pPropertyKey)) res.add(key.getPropvalue());
        }
        return res;
    }


/** Returns all the services from the given type
 * @param   pServiceType    service type
 * @param pFrom caller service
 * @param pProp  caller properties
 * @return service descriptor vector
 */
    public Vector<ServiceType> getAllService(String pServiceType,String pFrom, Hashtable pProp)
    {
        pProp.put("stype",pServiceType);
        pProp.put("from", pFrom);
        Vector<ServiceType> res=new Vector<ServiceType>();
        for(int i=0;(i<serviceInfo.size());i++)
            if(((ServiceType)serviceInfo.get(i)).isCorrect(pProp)) res.add((ServiceType)serviceInfo.get(i));
        return res;
    }
    
/**
 * Removes a service from the blacklist
 * @param pServiceType  service type
 * @param pServiceID    service ID
 */ 
    public void unSetForbiddenService(String pServiceType, String pServiceID)
    {
        if(forbiddenService.get(pServiceType)!=null)
        ((Vector)forbiddenService.get(pServiceType)).remove(pServiceID);
    }
    
/**
 * Adds a service to the blacklist
 * @param pServiceType  service type
 * @param pServiceID    service ID
 */ 
    public void setForbiddenService(String pServiceType, String pServiceID)
    {
        if(forbiddenService.get(pServiceType)==null)forbiddenService.put(pServiceType, new Vector());
        else ((Vector)forbiddenService.get(pServiceType)).remove(pServiceID);
        ((Vector)forbiddenService.get(pServiceType)).add(pServiceID);
    }
}
