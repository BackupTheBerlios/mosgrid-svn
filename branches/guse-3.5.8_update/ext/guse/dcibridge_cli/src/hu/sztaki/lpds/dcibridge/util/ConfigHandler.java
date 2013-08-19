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
 * Resource szerviztol kapott xml feldolgozasa
 */

package hu.sztaki.lpds.dcibridge.util;

import dci.data.Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Item; 
import dci.data.Middleware;

/**
 * @author krisztian karoczkai
 */
public class ConfigHandler{
    Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> res=new Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>>();

/**
 * GLite Vo konfiguraciojanak lekerdezese a megadott konfiguraciobol
 * @param pResources Konfiguracio
 * @param pVOName VO neve
 * @return glite konfiguracio
 * @throws java.lang.NullPointerException ha a VO nem letezik a konfiguracioban
 */
    public static Item.Glite getGliteVoConfig(List<Middleware> pResources,String pVOName) throws NullPointerException{
        for(Middleware t:pResources){
            if( t.getType().equals("glite"))
                for(Item ti:t.getItem())
                    if(ti.getName().equals(pVOName))
                        return ti.getGlite();
        }
        throw new NullPointerException("not exist:"+pVOName+" glite vo in config");
    }

/**
 * Atadott konfiguracios leirobab levo Grid Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getGridMidlewares(List<Middleware> pResources){
        Vector<String> res=new Vector<String>();
        for(Middleware t:pResources){
            if( (!t.getType().equals("gae")) && (!t.getType().equals("service")) )
                if(t.isEnabled())
                    res.add(t.getType());
        }
        return res;
    }

/**
 * Atadott konfiguracios leirobab levo Service Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getServiceMidlewares(List<Middleware> pResources){
        return getMidlewaresList(pResources, "service");
    }

/**
 * Atadott konfiguracios leirobab levo Cloud Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getCloudMidlewares(List<Middleware> pResources){
        return getMidlewaresList(pResources, "gae");
    }

/**
 * Atadott konfiguracios leirobab levo Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidlewares keresett midleware-ek
 * @return midleware lista
 */
    private static Vector<String> getMidlewaresList(List<Middleware> pResources, String pMidlewares){
        Vector<String> res=new Vector<String>();
        for(Middleware t:pResources){
                if(t.getType() != null && pMidlewares.contains(t.getType()))
                    if(t.isEnabled())
                        res.add(t.getType());
        }
        return res;
    }

/**
 * Atadott konfiguracios leiroban az atadott Midleware-hoz tartozo csoport lista meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidleware kivalasztott midleware
 * @return csoport lista
 */
    public static Vector<String> getGroups(List<Middleware> pResources, String pMidleware){
        System.out.println("getGroups:"+pMidleware);
        Vector<String> res=new Vector<String>();
        for(Middleware t:pResources)
            if(t.getType().equals(pMidleware))
                for(Item it:t.getItem())
                    if(it.isEnabled())
                        res.add(it.getName());
        return res;
    }


/**
 * Returns the supported cert types for the given resource (Middleware and Group),
 * or returns null, if the resource is disabled or not supported.
 * @param pResources
 * @param pMidleware
 * @param pGroup
 * @return
 */
    public static List<Certificate> getSupportedCertsIfAvailable(List<Middleware> pResources, String pMidleware, String pGroup){
        //System.out.println("getSupportedCertsIfAvailable:"+pMidleware+"/"+pGroup);
        for (Middleware t : pResources) {
            if (t.isEnabled() && t.getType().equals(pMidleware)) {
                for (Item it : t.getItem()) {
                    if (it.isEnabled() && pGroup.equals(it.getName())) {
                        //System.out.println("   - supported:"+pGroup);
                        return t.getCertificate();
                    }
                }
            }
        }
        return null;
    }
/**
 * Atadott konfiguracios leiroban minden Midleware-hoz tartozo csoport lista meghatarozasa amihez proxy szukseges (GT-2,GT-4,GLITE,gemlca)
 * @param pResources eroforras leiro
 * @return csoport lista
 */
    public static Vector<String> getAllGroupsforProxy(List<Middleware> pResources, Certificate[] pProxyType){
        Vector<String> res=new Vector<String>();

        boolean flag=false;
        for(Middleware t:pResources){
            if (!"edgi".equals(t.getType())){
                flag=false;
                if(t.isEnabled())
                    for(Certificate c:t.getCertificate())
                    for(Certificate c0:pProxyType)
                        if(c.equals(c0)) flag=true;
                if(flag)
                    for(Item i: t.getItem())
                        if(t.isEnabled()) res.add(i.getName());
                }
        }
        return res;
    }
    
/**
 * Atadott konfiguracios leiroban az atadott Midleware-hez tartozo csoport eroforras listajanak meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidleware kivalasztott midleware neve
 * @param pGroup kivalasztott csoport neve
 * @return eroforras lista
 */
    public static Vector<String> getResources(List<Middleware> pResources, String pMidleware, String pGroup){
        System.out.println("getResources:"+pMidleware+"/"+pGroup);
        Vector<String> res=new Vector<String>();
        for(Middleware t:pResources)
            if(t.getType().equals(pMidleware))
                for(Item it:t.getItem())
                    if(it.getName().equals(pGroup)){
                        if(pMidleware.equals("boinc"))
                            for(Item.Boinc.Job job:it.getBoinc().getJob()){if(job.isState()) res.add(job.getName());}
                        else if(pMidleware.equals("gt2"))
                            for(Item.Gt2.Resource job:it.getGt2().getResource()){res.add(job.getHost());}
                        else if(pMidleware.equals("gt4"))
                            for(Item.Gt4.Resource job:it.getGt4().getResource()){res.add(job.getHost());}
                        else if(pMidleware.equals("lsf"))
                            for(String queue:it.getLsf().getQueue()){res.add(queue);}
                        else if(pMidleware.equals("pbs"))
                            for(String queue:it.getPbs().getQueue()){res.add(queue);}
                    }
        return res;
    }
/**
 * Atadott konfiguracios leiroban az atadott Midleware-hez tartozo meghatorozott csoport kivalasztott eroforrasahoz tartozo szolgaltatas listajanak meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidleware kivalasztott midleware neve
 * @param pGroup kivalasztott csoport neve
 * @param pHost kivalasztott host neve
 * @return nyujtott szolgaltatasok listaja
 */
    public static Vector<String> getServices(List<Middleware> pResources, String pMidleware,String pGroup, String pHost)
    {
        System.out.println("getServices:"+pMidleware+"/"+pGroup+"/"+pHost);
        Vector<String> res=new Vector<String>();
        for(Middleware t:pResources)
            if(t.getType().equals(pMidleware))
                for(Item it:t.getItem())
                    if(it.getName().equals(pGroup)){
                        if(pMidleware.equals("gt2")){
                            for(Item.Gt2.Resource job:it.getGt2().getResource())
                                if(job.getHost().equals(pHost)){res.addAll(job.getJobmanager());}
                        }
                        else if(pMidleware.equals("gt4"))
                            for(Item.Gt4.Resource job:it.getGt4().getResource())
                                if(job.getHost().equals(pHost))
                                    {res.addAll(job.getJobmanager());}

                    }
        return res;


    }

    
}
