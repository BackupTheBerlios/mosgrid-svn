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
 * InformationBase.java
 * Local service cache
 */

package hu.sztaki.lpds.information.local;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.information.inf.InformationClient;
import hu.sztaki.lpds.information.net.wsaxis13.InformationClientImpl;
//import hu.sztaki.lpds.information.net.wsaxis13.InformationServiceImpl;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public class InformationBase implements InformationClient
{
    
    private static InformationBase instance=null;
    private Vector serviceInfo=new Vector();
    private Hashtable forbiddenService=new Hashtable();

/**
 * Class constructor
 */    
    public InformationBase() {}
    
/**
 * get singleton instance
 * @return singleton instance
 */    
    public static InformationBase getI()
    {
        if(instance==null)instance=new InformationBase();
        return instance;
    }
/*
    public <T> T getServiceClient(Class<T> pClient) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ServiceType st=InformationBase.getI().getService(pServiceType, pFrom, new Hashtable(), new Vector());
        BaseCommunicationFace ic=null;
        ic=(BaseCommunicationFace)Class.forName(st.getClientObject()).newInstance();
        ic.setServiceURL(st.getServiceUrl());
        ic.setServiceID(st.getServiceID());
        return (T) ic;
    }
*/
/**
 * get communication client object instance
 * @param pServiceType    Service type
 * @param pFrom  source service
 * @param pParams   required parameters
 * @param pFailed     used until now, but not eligible * @return
 * @return instance of Client object
 * @throws ClassNotFoundException if client class not available
 * @throws InstantiationException if client class not initable
 * @throws IllegalAccessException if not valid acces for client class
 */
    public Object getServiceClient(String pServiceType, String pFrom, Hashtable pParams, Vector pFailed) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ServiceType st=InformationBase.getI().getService(pServiceType, pFrom, pParams, pFailed);
        BaseCommunicationFace ic=null;
        ic=(BaseCommunicationFace)Class.forName(st.getClientObject()).newInstance();
        ic.setServiceURL(st.getServiceUrl());
        ic.setServiceID(st.getServiceID());
        return ic;
    }

/**
 * get communication client object instance
 * @param pServiceType    Service type
 * @param pFrom  source service
 * @param   pParams   required parameters
 * @return instance of Client object
 * @throws ClassNotFoundException if client class not available
 * @throws InstantiationException if client class not initable
 * @throws IllegalAccessException if not valid acces for client class
 */
    public Object getServiceClient(String pServiceType, String pFrom, Hashtable pParams) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ServiceType st=InformationBase.getI().getService(pServiceType, pFrom, pParams, new Vector());
        BaseCommunicationFace ic=null;
        ic=(BaseCommunicationFace)Class.forName(st.getClientObject()).newInstance();
        ic.setServiceURL(st.getServiceUrl());
        ic.setServiceID(st.getServiceID());
        return ic;
    }
/**
 * get communication client object instance
 * @param pServiceType    Service type
 * @param pFrom  source service
 * @return instance of Client object
 * @throws ClassNotFoundException if client class not available
 * @throws InstantiationException if client class not initable
 * @throws IllegalAccessException if not valid acces for client class
 */
    public Object getServiceClient(String pServiceType, String pFrom) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ServiceType st=InformationBase.getI().getService(pServiceType, pFrom, new Hashtable(), new Vector());
        BaseCommunicationFace ic=null;
        ic=(BaseCommunicationFace)Class.forName(st.getClientObject()).newInstance();
        ic.setServiceURL(st.getServiceUrl());
        ic.setServiceID(st.getServiceID());
        return ic;
    }
    
/**
 * Looks for a suitable service descriptor
 * @param   pServiceType    Service type
 * @param   pProp   required parameters
 * @param   pFault    used until now, but not eligible
 * @return service descriptor
 */
    public ServiceType getService(String pServiceType,String pFrom, Hashtable pProp, Vector pFault)
    {

        pProp.put("stype",pServiceType);
        pProp.put("from", pFrom);
        ServiceType res=null;
        int i=0;
        for(i=0;(i<serviceInfo.size())&&(!((ServiceType)serviceInfo.get(i)).isCorrect(pProp));i++){
            res=(ServiceType)serviceInfo.get(i);
        }
        try{
            res=(ServiceType)serviceInfo.get(i);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            InformationClientImpl ic=new InformationClientImpl();
            ic.setServiceURL(PropertyLoader.getInstance().getProperty("is.url"));
            ic.setServiceID(PropertyLoader.getInstance().getProperty("is.id"));
            try
            {
                res=ic.getService(pServiceType, pFrom, pProp,pFault);
                if(res!=null)
                {
                    serviceInfo.add(res);
                    Enumeration enm=pProp.keys();
                    String key="";
                    while(enm.hasMoreElements())
                    {
                        key=""+enm.nextElement();
                        res.set(key,""+pProp.get(key));
                    }
                }

            }
            catch(Exception e0)
            {
                System.out.println("INFORMATION ERORRO:"+e.getMessage());
                e0.printStackTrace();
            }
        }
        return res;
    }
    
/** Returns all of the services from the required type
 * @param   pServiceType    service type
 * @param pFrom  source service
 * @param   pProp    required parameters
 * @return vector of the service descriptors
 */
    public Vector<ServiceType> getAllService(String pServiceType,String pFrom, Hashtable pProp)
    {
        Vector<ServiceType> res=new Vector<ServiceType>();
        Vector<ServiceType> tmp=new Vector<ServiceType>();
        pProp.put("stype",pServiceType);
        pProp.put("from", pFrom);
        if(serviceInfo.size()==0)
        {
            InformationClientImpl ic=new InformationClientImpl();
            ic.setServiceURL(PropertyLoader.getInstance().getProperty("is.url"));
            ic.setServiceID(PropertyLoader.getInstance().getProperty("is.id"));
            try
            {
                tmp=ic.getAllService(pServiceType, pFrom, pProp);
                if(tmp!=null)
                {
                    serviceInfo.add(tmp);
                    Enumeration enm=pProp.keys();
                    String key="";
                    while(enm.hasMoreElements())
                    {
                        key=""+enm.nextElement();
                        for(int i=0;i<tmp.size();i++)
                        ((ServiceType)tmp.get(i)).set(key,""+pProp.get(key));
                    }
                }
            }
            catch(Exception e0){e0.printStackTrace();}
        }
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

    public void setServiceURL(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setServiceID(String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HashMap getAllProperties(String pServiceURL) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
