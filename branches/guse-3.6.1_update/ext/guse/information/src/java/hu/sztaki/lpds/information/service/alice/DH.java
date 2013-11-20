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
 * Data handling class
 */

package hu.sztaki.lpds.information.service.alice;

import hu.sztaki.lpds.information.data.GroupPropertyBean;
import hu.sztaki.lpds.information.data.GroupsTypeBean;
import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.OptionBean;
import hu.sztaki.lpds.information.data.ResourceBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.data.ServiceUserBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * @author krisztian
 */
public class DH
{
/** Singleton instance */
    private static DH instance=new DH();
/** Entity handling instance, all database handling operation goes through it */
    private EntityManager em;
/**
 * Constructor
 * Creates the entity handler instance
 */
    public DH() {em=Persistence.createEntityManagerFactory("informationPU").createEntityManager();}
/**
 * Getting singleton instance
 * @return DH instance
 */
    public static DH getI(){return instance;}

/**
 * Getting entity handler instance
 * @return
 */
    public EntityManager getEM(){return em;}
/**
 * Getting guse service type based on ID
 * @param pvalue service type ID
 * @return service type instance
 */
    public GuseServiceTypeBean getGuseServiceType(long pvalue)
    {
        return em.find(GuseServiceTypeBean.class, pvalue);
    }

/**
 * Getting guse service parameter based on ID
 * @param pvalue property ID
 * @return property instance
 */
    public ServicePropertyBean getGuseServiceProperty(long pvalue)
    {
        return em.find(ServicePropertyBean.class, pvalue);
    }



/**
 * Getting guse service property
 * @param pSid  service ID
 * @param pKey parameter name
 * @return property instance
 */
    public ServicePropertyBean getGuseServiceProperty(long pSid, String pKey)
    {
        Query q=em.createNamedQuery("ServicePropertyBean.skey");
        q.setParameter("key", pKey);
        q.setParameter("sid", pSid);
        List<ServicePropertyBean> tmp=q.getResultList();
        if(tmp.size()>0) return tmp.get(0);
        throw new NullPointerException(pKey+"property not valid in "+pSid+" service");
    }

/**
 * Getting guse service type based on ID
 * @param pvalue service type ID
 * @return service type instance
 * @throws NullPointerException if not exists
 */
    public GuseServiceTypeBean getGuseServiceType(String pvalue) throws NullPointerException
    {
        Query q=em.createNamedQuery("GuseServiceTypeBean.sname");
        q.setParameter("value", pvalue);
        List<GuseServiceTypeBean> tmp=q.getResultList();
        if(tmp.size()>0) return tmp.get(0);
        throw new NullPointerException("service type not valid:"+pvalue);
    }
/**
 * Getting available guse service types
 * @return service type list
 */
    public List<GuseServiceTypeBean> getAllGuseServiceType()
    {
        Query q=em.createNamedQuery("GuseServiceTypeBean.all");
        return q.getResultList();
    }

/**
 * Adding new guse service type
 * @param pValue service type descriptor
 */
    public synchronized void newServiceType(GuseServiceTypeBean pValue)
    {

        EntityTransaction et=em.getTransaction();
        et.begin();
        em.persist(pValue);
        et.commit();
    }


/**
 * Deleting gUSE service type
 * @param pValue service type ID
 */
    public synchronized void deleteServiceType(long pValue)
    {
        EntityTransaction et=em.getTransaction();
        GuseServiceTypeBean tmp=getGuseServiceType(pValue);
        et.begin();
        em.remove(tmp);
        et.commit();
    }

/**
 * Getting guse service communication based on ID
 * @param pValue communication ID
 * @return communication instance
 */
    public GuseServiceCommunicationBean getGuseServiceComm(long pValue)
    {
        return em.find(GuseServiceCommunicationBean.class, pValue);
    }

/**
 * Getting guse service based on ID
 * @param pValue service ID
 * @return service instance
 */
    public GuseServiceBean getGuseService(long pValue)
    {
        return em.find(GuseServiceBean.class, pValue);
    }
/**
 * Getting available guse services
 * @param pURL service url
 * @return service list
 */
    public List<GuseServiceBean> getGuseService(String pURL)
    {
        Query q=em.createNamedQuery("GuseServiceBean.url");
        q.setParameter("url", pURL);
        return q.getResultList();
    }

/**
 * Getting available guse services
 * @return service list
 */
    public synchronized List<GuseServiceBean> getAllGuseService()
    {
        Query q=em.createNamedQuery("GuseServiceBean.all");
        return q.getResultList();
    }

/**
 * Adding new gUSE service
 * @param pValue service descriptor
 */
    public synchronized void newService(GuseServiceBean pValue)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        em.persist(pValue);
        pValue.getTyp().getServices().add(pValue);
        et.commit();
    }
/**
 * Modifying an existing gUSE service (ID cannot be changed)
 * @param pID  unique internal service ID
 * @param pValue new service descriptor
 */
    public synchronized void modService(long pID,GuseServiceBean pValue)
    {
        GuseServiceBean tmp=getGuseService(pValue.getId());
        tmp=pValue;
        tmp.setId(pID);
        EntityTransaction et=em.getTransaction();
        et.begin();
        em.persist(tmp);
        et.commit();
    }

/**
 * Deleting a gUSE service 
 * @param pValue service ID
 * @throws Exception Data handling error
 */
    public synchronized void deleteService(long pValue) throws Exception
    {
        GuseServiceBean tmp=getGuseService(pValue);
        if(tmp==null) throw new NullPointerException(""+pValue+" not valid service");
        EntityTransaction et=em.getTransaction();
        et.begin();
        em.remove(tmp);
        et.commit();
    }
/**
 * Getting service communication type
 * @param pValue type internal ID
 * @return communication descriptor
 */
    public GuseServiceCommunicationBean getGuseServieComm(long pValue)
    {
        return em.find(GuseServiceCommunicationBean.class, pValue);
    }

/**
 * Getting available guse service communication types
 * @return communication list
 */
    public List<GuseServiceCommunicationBean> getAllGuseServiceComm()
    {
        Query q=em.createNamedQuery("GuseServiceCommunicationBean.all");
        return q.getResultList();
    }

/**
 * Adding new gUSE service type
 * @param pValue service type descriptor
 */
    public synchronized void newServiceComm(GuseServiceCommunicationBean pValue)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        em.persist(pValue);
        et.commit();
    }

/**
 * Deleting gUSE service communication
 * @param pValue service communication ID
 */
    public synchronized void deleteServiceComm(long pValue)
    {
        EntityTransaction et=em.getTransaction();
        GuseServiceCommunicationBean tmp=getGuseServieComm(pValue);
        et.begin();
        em.remove(tmp);
        et.commit();
    }
/**
 * Saving the user using the service
 * @param pService service descriptor
 * @param pUser user descriptor
 */
    public synchronized void persistServiceUser(GuseServiceBean pService, ServiceUserBean pUser)
    {
        Query q=em.createNamedQuery("ServiceUserBean.lname");
        q.setParameter("lname", pUser.getLname());
        EntityTransaction et=em.getTransaction();
        et.begin();
        try
        {
            ServiceUserBean tmp=(ServiceUserBean)q.getSingleResult();
            if(tmp!=null)
            {
                if(!pService.getUsers().contains(tmp))
                {
                    pService.getUsers().add(tmp);
                    em.persist(pService);
                }
            }
        }
        catch(NoResultException e) //new user
            {
                em.persist(pUser);
                pService.getUsers().add(pUser);
                em.persist(pService);
            }
        et.commit();
    }
/**
 * Remove permission of a service usage
 * @param pService service descriptor
 * @param pUser user descriptor
 */
    public synchronized void removeAuth(GuseServiceBean pService, ServiceUserBean pUser)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        pService.getUsers().remove(pUser);
        em.persist(pService);
        em.persist(pUser);
        et.commit();
    }
/**
 * Getting service usage permission
 * @param pValue user right internal ID
 * @return descriptor
 */
    public ServiceUserBean getServiceUserBean(long pValue)
    {
        return em.find(ServiceUserBean.class, pValue);
    }

/**
 * Saving service communication
 * @param pCom communication descriptor
 * @param pRes resource
 */
    public synchronized void persistComResource(GuseServiceCommunicationBean pCom, ServiceResourceBean pRes)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        pRes.setCom(pCom);
        em.persist(pRes);
        pCom.getResources().add(pRes);
        em.persist(pCom);
        et.commit();
    }
/**
 * Deleting service communication
 * @param pCom communication descriptor
 * @param pValue  resource
 * @throws NullPointerException unsuccessful deletion
 */
    public synchronized void removeComResource(GuseServiceCommunicationBean pCom, ServiceResourceBean pValue) throws NullPointerException
    {
        if(pCom.getResources().contains(pValue))
        {
            EntityTransaction et=em.getTransaction();
            et.begin();
            pCom.getResources().remove(pValue);
            em.remove(pValue);
            et.commit();
        }
        else new NullPointerException(pValue.getId()+" not elemnt "+pCom.getId()+" resources list");
    }
/**
 * Getting service resource
 * @param pValue internal ID
 * @return resource descriptor
 */
    public ServiceResourceBean getServiceResourceBean(long pValue)
    {
        return em.find(ServiceResourceBean.class, pValue);
    }
/**
 * Getting gUSe information option - with this option it can be decided if the database needs to be loaded with base values
 * @param pID system parameter name
 * @return value
 */
    public String getOptionValue(String pID)
    {
        OptionBean tmp=em.find(OptionBean.class, pID);
        return tmp.getTxt();
    }
/**
 * Setting the system parameter
 * @param pValue parameter descriptor
 */
    public synchronized void setOptionValue(OptionBean pValue)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        em.persist(pValue);
        et.commit();
    }
/**
 * Getting configured gUSE service lists
 * @param pFrom source service type
 * @param pStype required service type
 * @return available configurations list
 */
    public synchronized List<ServiceResourceBean> getServiceList(String pFrom, String pStype)
    {

        Query q=em.createNamedQuery("ServiceResourceBean.com");
//        Query q=em.createNamedQuery("GuseServiceBean.resource");
        q.setParameter("s0", pFrom);
        q.setParameter("s1", pStype);
        return q.getResultList();
    }
/**
 * Saving service property
 * @param pService service descriptor
 * @param pProperty property descriptor
 */
    public synchronized void persistProperty(GuseServiceBean pService,ServicePropertyBean pProperty)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        pProperty.setService(pService);
        em.persist(pProperty);
        pService.getProperties().add(pProperty);
        et.commit();
    }
/**
 * Getting service property
 * @param pService service descriptor
 * @param pPropKey property key
 * @return property descriptor
 * @throws java.lang.NullPointerException property not found
 */
    public ServicePropertyBean getProperty(GuseServiceBean pService, String pPropKey) throws NullPointerException
    {
        Iterator<ServicePropertyBean> it=pService.getProperties().iterator();
        ServicePropertyBean res;
        while(it.hasNext())
        {
            res=it.next();
            if(res.getPropkey().equals(pPropKey)) return res;
        }
        throw new NullPointerException("Not exist:"+pPropKey+" in "+pService.getUrl());
    }
/**
 * Deleting service property
 * @param pService service descriptor
 * @param pValue property descriptor
 */
    public synchronized void deleteServiceProperty(GuseServiceBean pService,ServicePropertyBean pValue)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        pService.getProperties().remove(pValue);
        em.remove(pValue);
        et.commit();
    }
/**
 * Saving resource group (for example grid/vo)
 * @param pMidleware middleware type
 * @param pGrid grid/vo name
 * @param pProperties properties
 * @return saved group descriptor
 */
    public synchronized GroupsTypeBean persistGroup(String pMidleware, String pGrid, HashMap pProperties)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();

        GroupsTypeBean tmp=null;
        List<GroupsTypeBean> grps=null;
        try{grps=getExistGroups(pMidleware, pGrid);}
        catch(Exception e){e.printStackTrace();}
        if(grps==null || grps.size()==0)
        {
            tmp=new GroupsTypeBean();
            tmp.setName(pGrid);
            tmp.setType(pMidleware);
            tmp.setProperties(new HashMap<String, GroupPropertyBean>());
            em.persist(tmp);
        }
        else
        {
            tmp=grps.get(0);
        }

        if(pProperties==null) {et.commit();return tmp;}
        if(pProperties.size()==0) {et.commit();return tmp;}

        Iterator<String> it=pProperties.keySet().iterator();
        String key;
        GroupPropertyBean gpb;
        while(it.hasNext())
        {
            key=it.next();
            gpb=tmp.getProperties().get(key);
            if(gpb==null)
            {
                gpb=new GroupPropertyBean();
                gpb.setName(key);
                gpb.setValue((String)pProperties.get(key));
                tmp.getProperties().put(gpb.getName(), gpb);
                em.persist(gpb);
            }
            else{
                gpb.setName(key);
                gpb.setValue((String)pProperties.get(key));
                tmp.getProperties().put(gpb.getName(), gpb);
                em.merge(gpb);
            }
        }
        et.commit();
        return tmp;
    }
/**
 * Search in configured executor resources
 * @param pMidleware middleware types
 * @param pGrid grid/vo name
 * @return available resources
 */
    public synchronized  List<GroupsTypeBean> getExistGroups(String pMidleware, String pGrid)
    {
        try
        {
            Query q=em.createNamedQuery("GroupsTypeBean.exist");
            q.setParameter("ptype", pMidleware);
            q.setParameter("pname", pGrid);
            List<GroupsTypeBean> grp=q.getResultList();
            return grp;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ArrayList<GroupsTypeBean>();
        }
    }
/**
 * Getting all the executor resource groups
 * @return available groups list
 */
    public synchronized  List<GroupsTypeBean> getAllRunnableResourceGroups()
    {
        try
        {
            Query q=em.createNamedQuery("GroupsTypeBean.all");
            List<GroupsTypeBean> grp=q.getResultList();
            return grp;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ArrayList<GroupsTypeBean>();
        }
    }
/**
 * Adding new executor resource
 * @param pgrp group descriptor which the resource belongs to
 * @param pRes resource descriptor
 */
    public synchronized void addResource(GroupsTypeBean pgrp, ResourceBean pRes)
    {
        EntityTransaction et=em.getTransaction();
        et.begin();
        pgrp.getResources().add(pRes);
        em.persist(pRes);
        et.commit();
    }
/**
 * Deleting existing executor resource
 * @param pGroup  group descriptor which the resource belongs to
 * @param pRID resource descriptor internal ID
 * @throws Exception deletion was not successful
 */
    public synchronized void deleteResource(GroupsTypeBean pGroup, long pRID) throws Exception
    {
        ResourceBean rb=em.find(ResourceBean.class, pRID);
        EntityTransaction et=em.getTransaction();
        et.begin();
        pGroup.getResources().remove(rb);
        em.remove(rb);
        et.commit();
    }
}
