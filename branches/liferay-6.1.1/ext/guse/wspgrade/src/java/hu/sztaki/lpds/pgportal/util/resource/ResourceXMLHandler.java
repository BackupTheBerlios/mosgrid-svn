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

package hu.sztaki.lpds.pgportal.util.resource;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author krisztian
 */
public class ResourceXMLHandler extends DefaultHandler
{
    Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> res=new Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>>();
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException
    {
//item tag parsolasa
        if("item".equals(arg2))
        {
            String midleware=arg3.getValue("midleware");
            String group=arg3.getValue("group");
            String service=arg3.getValue("service");
            String resource=arg3.getValue("resource");
            if(res.get(midleware)==null) res.put(midleware, new Hashtable<String,Hashtable<String,Vector<String>>>());
            if(res.get(midleware).get(group)==null) res.get(midleware).put(group, new Hashtable<String,Vector<String>>());
            if(res.get(midleware).get(group).get(service)==null) res.get(midleware).get(group).put(service, new Vector<String>());
            res.get(midleware).get(group).get(service).add(resource);

        }
    }
/**
 * Konfiguralt eroforrasok lekerdezes
 * @return eroforraslista
 */
    public Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> getResources(){return res;}

/**
 * Atadott konfiguracios leirobab levo Grid Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getGridMidlewares(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources)
    {
        return getMidlewaresList(pResources, "gt2,gt4,glite,gemlca,local,random,finish,boinc,pbs,lsf,unicore,arc,slurm");
    }

/**
 * Atadott konfiguracios leirobab levo Grid Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getServiceMidlewares(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources)
    {
        return getMidlewaresList(pResources, "WebService");
    }

/**
 * Atadott konfiguracios leirobab levo Grid Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @return midleware lista
 */
    public static Vector<String> getCloudMidlewares(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources)
    {
        return getMidlewaresList(pResources, "GAE");
    }

/**
 * Atadott konfiguracios leirobab levo Midleware tipusok meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidlewares keresett midleware-ek
 * @return midleware lista
 */
    private static Vector<String> getMidlewaresList(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources, String pMidlewares)
    {
        Vector<String> res=new Vector<String>();
        if(pResources!=null)
        {
            Enumeration<String> resourcesENM=pResources.keys();
            String key;
            while(resourcesENM.hasMoreElements())
            {
                key=resourcesENM.nextElement();
                if(pMidlewares.contains(key))
                    res.add(key);
            }
        }
        return res;

    }

/**
 * Atadott konfiguracios leiroban az atadott Midleware-hoz tartozo csoport lista meghatarozasa
 * @param pResources eroforras leiro
 * @param pMidleware kivalasztott midleware
 * @return csoport lista
 */
    public static Vector<String> getGroups(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources, String pMidleware)
    {
//        print(pResources);
        Vector<String> res=new Vector<String>();
        if(pResources!=null)
        {
            Hashtable<String,Hashtable<String,Vector<String>>> resourcesGROUPS=pResources.get(pMidleware);
            if(resourcesGROUPS!=null)
            {
                Enumeration<String> resourcesENM=resourcesGROUPS.keys();
                while(resourcesENM.hasMoreElements())
                    res.add(resourcesENM.nextElement());
            }
        }
        return res;
    }

/**
 * Konfiguracios csoportok listazasa
 * @param pParam midleware adatok
 * @return csoport adatok
 */
    private static List<String> getGroupInMidleware(Hashtable<String,Hashtable<String,Vector<String>>> pParam)
    {
        List<String> lres=new Vector<String>();
        if(pParam!=null)
        {
            Enumeration<String> resourcesENM=pParam.keys();
            while(resourcesENM.hasMoreElements())
                lres.add(resourcesENM.nextElement());
        }
        return lres;
    }

/**
 * Atadott konfiguracios leiroban minden Midleware-hoz tartozo csoport lista meghatarozasa amihez proxy szukseges (GT-2,GT-4,GLITE,gemlca)
 * @param pResources eroforras leiro
 * @return csoport lista
 */
    public static Vector<String> getAllGroupsforProxy(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources)
    {
        Vector<String> res=new Vector<String>();
        String[] midlewares={"gt2","gt4","glite","gemlca"};
        Hashtable<String,Hashtable<String,Vector<String>>> resourcesGROUPS;
        for(String t:midlewares)
        {
            resourcesGROUPS=pResources.get(t);
            res.addAll(getGroupInMidleware(resourcesGROUPS));
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
    public static Vector<String> getResources(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources, String pMidleware,String pGroup)
    {

        Vector<String> res=new Vector<String>();
        if(pResources!=null)
        {
            Hashtable<String,Hashtable<String,Vector<String>>> resourcesGROUPS=pResources.get(pMidleware);
            if(resourcesGROUPS!=null)
            {
                Hashtable<String,Vector<String>> resourcesHOST=resourcesGROUPS.get(pGroup);
                if(resourcesHOST!=null)
                {
                    Enumeration<String> resourcesENM=resourcesHOST.keys();
                    while(resourcesENM.hasMoreElements())
                        res.add(resourcesENM.nextElement());
                }
            }
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
    public static Vector<String> getServices(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources, String pMidleware,String pGroup, String pHost)
    {

        Vector<String> res=new Vector<String>();
        if(pResources!=null)
        {
            Hashtable<String,Hashtable<String,Vector<String>>> resourcesGROUPS=pResources.get(pMidleware);
            if(resourcesGROUPS!=null)
            {
                Hashtable<String,Vector<String>> resourcesHOST=resourcesGROUPS.get(pGroup);
                if(resourcesHOST!=null)
                {
                    Vector<String> resourcesSERVICE=resourcesHOST.get(pHost);
                    if(resourcesSERVICE!=null) res=resourcesSERVICE;
                }
            }
        }
        return res;
    }

    public static void print(Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>> pResources)
    {
        Hashtable<String,Hashtable<String,Vector<String>>> resourcesGROUPS;
        Hashtable<String,Vector<String>> resourcesHOST;
        Vector<String> resourcesSERVICE;
        Enumeration<String> enm0,enm1,enm2;
        String key0,key1,key2;
        if(pResources!=null)
        {
            enm0=pResources.keys();
            while(enm0.hasMoreElements())
            {
                key0=enm0.nextElement();
                resourcesGROUPS=pResources.get(key0);

                enm1=resourcesGROUPS.keys();
                while(enm1.hasMoreElements())
                {
                    key1=enm1.nextElement();
                    resourcesHOST=resourcesGROUPS.get(key1);

                    enm2=resourcesHOST.keys();
                    while(enm2.hasMoreElements())
                    {
                        key2=enm2.nextElement();
                        resourcesSERVICE=resourcesHOST.get(key2);
                    }
                }
            }
        }
        else System.out.println("NINCS EROFORRAS LISTA AZ ATADOTT PARAMETERBEN");
    }
}
