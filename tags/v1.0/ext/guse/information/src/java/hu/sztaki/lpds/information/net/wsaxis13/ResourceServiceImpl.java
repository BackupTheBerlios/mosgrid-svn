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
/**
 *  Implementation of the executor resource handling
 */

package hu.sztaki.lpds.information.net.wsaxis13;

import hu.sztaki.lpds.information.data.GroupPropertyBean;
import hu.sztaki.lpds.information.data.GroupsTypeBean;
import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.ResourceBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.service.alice.DH;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author krisztian
 */
public class ResourceServiceImpl
{
/**
 * Getting the middlewares accessible for the user from given interface
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @return middleware list
 * @throws java.lang.Exception communication error
 */
    public Vector<String> getAllMidleware(String pPortalID, String pUserID) throws Exception
    {
        Vector<String> res=new Vector<String>(); //return value
        Iterator<ServicePropertyBean> it; //parameter for iterating the service list
        String[] ms; //property processing parameter
// getting the configured services
        List<GuseServiceBean> allService=DH.getI().getAllGuseService();
        for(GuseServiceBean t:allService)
        {
// choosing submitter
            if("submitter".equals(t.getTyp().getSname()))
            {
// getting submitter properties
                it=t.getProperties().iterator();
                ServicePropertyBean item;
                while(it.hasNext())
                {
                    item=it.next();
// putting the supported middlewares into a list
                    if("submitter.supportedmidleware".equals(item.getPropkey()))
                    {
                       ms=item.getPropvalue().split(",");
                       for(String s:ms)
                           if(!res.contains(s)) res.add(s);
                    }
                }
            }
        }
        return res;
    }
/**
 * Getting all the grids/groups from the middleware
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @return available resource groups (grid/VO)
 * @throws Exception error during getting the data
 */
    public Vector<String> getAllGrids(String pPortalID, String pUserID,String pMidleware) throws Exception
    {
        Vector<String> res=new Vector<String>(); //return value
        Iterator<ServicePropertyBean> it; //parameter for iterating the service list
        String[] ms; //property processing parameter
// getting the configured services
        List<GuseServiceBean> allService=DH.getI().getAllGuseService();
        for(GuseServiceBean t:allService)
        {
// choosing submitter
            if("submitter".equals(t.getTyp().getSname()))
            {
// getting submitter properties
                it=t.getProperties().iterator();
                ServicePropertyBean item;
                while(it.hasNext())
                {
                    item=it.next();
// putting the supported middlewares into a list
                    if(("submitter.grids."+pMidleware).equals(item.getPropkey()))
                    {
                       ms=item.getPropvalue().split(",");
                       for(String s:ms)
                           if(!res.contains(s)) res.add(s);
                    }
                }
            }
        }
        return res;
    }
/**
 * Getting properties belong to a resource group
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @param pGrid resource group (grid/vo)
 * @return property list
 * @throws Exception any error during processing
 */
    public HashMap getGridProperies(String pPortalID, String pUserID,String pMidleware,String pGrid) throws Exception
    {
        HashMap res=new HashMap();
        try
        {

            Collection<GroupPropertyBean> tmp=DH.getI().getExistGroups(pMidleware, pGrid).get(0).getProperties().values();
            for(GroupPropertyBean t:tmp)
                res.put(t.getName(), t.getValue());
        }
        catch(Exception e){e.printStackTrace();}
        return res;
    }
/**
 * Setting properties belong to a resource group
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @param pGrid resource group (grid/vo)
 * @param pProps property key-value pairs
 * @return success of the operation (action.ok/error.data)
 * @throws Exception any error during processing
 */
    public String setGridProperies(String pPortalID, String pUserID,String pMidleware,String pGrid,HashMap pProps) throws Exception
    {
        String res="action.ok";
        try
        {
            DH.getI().persistGroup(pMidleware, pGrid, pProps);
        }
        catch(Exception e){e.printStackTrace();res="error.data";}
        return res;
    }
/**
 * Adding new resource to an executor group (grid/vo)
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @param pGrid resource group (grid/vo)
 * @param pSite site
 * @param pJobmanager jobmanager
 * @return success of the operation (action.ok/error.data)
 * @throws Exception any error during processing
 */
    public String addResource(String pPortalID, String pUserID,String pMidleware,String pGrid,String pSite, String pJobmanager) throws Exception
    {
        String res="action.ok";
        try
        {
            List<GroupsTypeBean> tmp=DH.getI().getExistGroups(pMidleware, pGrid);
            GroupsTypeBean gtb=null;
            if(tmp.size()>0) gtb=tmp.get(0);
            else gtb=DH.getI().persistGroup(pMidleware, pGrid, new HashMap());
            ResourceBean rb=new ResourceBean();
            rb.setJobmanager(pJobmanager);
            rb.setSite(pSite);
            DH.getI().addResource(gtb, rb);
        }
        catch(Exception e){e.printStackTrace();res="error.data";}
        return res;
    }
/**
 * Getting all the available resources for an executor group
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @param pGrid resources group (grid/vo)
 * @return resources list
 */
    public Vector getAllResource(String pPortalID, String pUserID,String pMidleware,String pGrid)
    {
        Vector res=new Vector();
        try{res.addAll(DH.getI().getExistGroups(pMidleware, pGrid).get(0).getResources());}
        catch(Exception e){/*e.printStackTrace();*/}
        return res;
    }

/**
 * Deleting resource from an executor group
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @param pMidleware middleware ID
 * @param pGrid resource group (grid/vo)
 * @param pResourceID resource ID
 * @return success of the operation(action.ok/error.data)
 * @throws Exception any error during processing
 */
    public String deleteResource(String pPortalID, String pUserID,String pMidleware,String pGrid,String pResourceID) throws Exception
    {
        String res="action.ok";
        try
        {
            
            GroupsTypeBean gtb=DH.getI().getExistGroups(pMidleware, pGrid).get(0);
            DH.getI().deleteResource(gtb, Long.parseLong(pResourceID));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            res="error.data";
        }
        return res;
    }
/**
 * Getting configured resources as an XML string
 * @param pPortalID portal ID
 * @param pUserID user ID
 * @return data as an XML
 */
    public String getConfiguredResources(String pPortalID, String pUserID)
    {
        HashMap<String,HashMap<String,HashMap<String,Vector<String>>>> res=new HashMap();
        try
        {
            HashMap<String,Vector<String>> tmp;
            Vector<ResourceBean> resources;
            Vector<String> grids;
            Vector<String> midlewares=getAllMidleware(pPortalID, pUserID);
            for(String t:midlewares)
            {
                res.put(t, new HashMap());
                grids=getAllGrids(pPortalID, pUserID, t);
                for(String tt:grids)
                {
                    res.get(t).put(tt, new HashMap());
                    resources=(Vector<ResourceBean>)getAllResource(pPortalID, pUserID, t, tt);
                    for(ResourceBean ttt:resources)
                    {
                        tmp=res.get(t).get(tt);
                        if(tmp.get(ttt.getSite())==null) tmp.put(ttt.getSite(), new Vector<String>());
                        tmp.get(ttt.getSite()).add(ttt.getJobmanager());
                    }
                }
            }
        }
        catch(Exception e){e.printStackTrace();}
//xml setup
        boolean flag;
        StringBuffer sb=new StringBuffer("<configure>\n");
        HashMap<String,HashMap<String,HashMap<String,Vector<String>>>> tmp=(HashMap<String,HashMap<String,HashMap<String,Vector<String>>>>)res;

        Iterator<String> it2,it1,it0=tmp.keySet().iterator();
        HashMap<String,HashMap<String,Vector<String>>> tmp0;
        HashMap<String,Vector<String>> tmp1;
        Vector<String> tmp2;
        String key0,key1,key2;
        while(it0.hasNext()) //iterating middlewares
        {
            key0=it0.next();
            tmp0=tmp.get(key0);
            it1=tmp0.keySet().iterator();
            while(it1.hasNext()) //iterating groups
            {
                key1=it1.next();
                tmp1=tmp0.get(key1);
                it2=tmp1.keySet().iterator();
                flag=true;
                while(it2.hasNext()) //iterating resources
                {
                    key2=it2.next();
                    tmp2=tmp1.get(key2);
                    for(String jm:tmp2)
                    {
                        sb.append("<item midleware=\""+key0+"\" group=\""+key1+"\" service=\""+key2+"\" resource=\""+jm+"\" />\n");
                        flag=false;
                    }
                }
                if(flag)
                    sb.append("<item midleware=\""+key0+"\" group=\""+key1+"\" service=\"\" resource=\"\" />\n");
           }
        }
        sb.append("</configure>");
        return sb.toString();
    }

}
