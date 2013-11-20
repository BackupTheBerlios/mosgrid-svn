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
 * ResourceServiceImpl.java
 * Deffinialt eroforrasok kezelese
 */
 
package hu.sztaki.lpds.pgportal.util.resource;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Deffinialt eroforrasok kezelese
 *
 * @author krisztian karoczkai
 */
public class ResourceServiceImpl 
{
    private static ResourceServiceImpl instance=null;
    private Hashtable data=new Hashtable();
    private Vector services=new Vector();

    /**
     * Statikus osztaly peldany lekerdezese
     * @return statikus osztaly peldany
     */
    public static ResourceServiceImpl getI()
    {
        if(instance==null)instance=new ResourceServiceImpl();
        return instance;
    }
    
    /**
     * Class constructor
     */
    public ResourceServiceImpl()
    {
        try
        {
        File dir=new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"ws");
        File[] f=dir.listFiles();
        for(int i=0;i<f.length;i++)
        {
            if(f[i].isDirectory())
            {
                File sDir=new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"ws/"+f[i].getName());
                File[] sf=sDir.listFiles();
                for(int j=0;j<sf.length;j++)
                {
                    if(sf[j].isFile())
                    {
                        Kxml handler = new Kxml(f[i].getName(),sf[j].getName());
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        try 
                        {
                            SAXParser saxParser = factory.newSAXParser();
                            saxParser.parse( new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"ws/"+f[i].getName()+"/"+sf[j].getName()), handler);
                            services.addAll(handler.getXMLData());
                        } 
                        catch (Throwable t) {System.out.println("Hib�s service.xml\n");t.printStackTrace();}
                    
                    }
                }
            }
        }
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    /**
     * Rendelkezesre allo service tipusok lekerdezese
     * @return tipus Vector
     */
    public Hashtable getServiceTypes()
    {
        Hashtable res=new Hashtable();
        for(int i=0;i<services.size();i++)
        {res.put(((ServiceResourceBean)services.get(i)).getType(),"");}
        return res; 
    }
    
    /**
     * WSDL file-ok lekerdezese
     * @param pType service tipus
     * @return ServiceURL/WSDL Hash
     */
    public Hashtable getWSDLs(String pType)
    {
        Hashtable res=new Hashtable();
        for(int i=0;i<services.size();i++)
        {
            if(((ServiceResourceBean)services.get(i)).getType().equals(pType))
            res.put(((ServiceResourceBean)services.get(i)).getUrl(),((ServiceResourceBean)services.get(i)).getFile());
        }
        return res; 
    }    
    
    /**
     * Publikus web service-ek hosztjan talalhato servizek
     *
     * @param pType service tipus
     * @param pURL Service url
     * @return serviz Hash
     */
    public Hashtable getServiceMethod(String pType, String pURL)
    {
        Hashtable res=new Hashtable();
        for(int i=0;i<services.size();i++)
        {
            if(
                    ((ServiceResourceBean)services.get(i)).getType().equals(pType)&&
                    ((ServiceResourceBean)services.get(i)).getUrl().equals(pURL)
              )
            res.put(((ServiceResourceBean)services.get(i)).getMethod(),"");
        }
        return res; 
    }

    /**
     * Grid tipusok lekerdezese
     * @return typus Vector
     */
    public Vector getGridType()
    {
        Vector res=new Vector() ;
        String s="";
        try
        {
            BufferedReader fr=new BufferedReader(new FileReader(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/.grid.resources.conf"));
            while((s=fr.readLine())!=null)
            {
                String lin[]=s.split(" ");
                //
                String lin1 = lin[1];
                if (lin1.contains("#")) {
                    lin1 = lin1.split("#")[1];
                }
                //
                boolean newType=true;
                for(int i=0;i<res.size();i++)if((""+res.get(i)).equals(lin1))newType=false;
                if(newType)
                if(!lin1.equals("GRID"))
                {    
                    res.add(lin1);
                }
            }
            fr.close();
        }
        catch(IOException e){e.printStackTrace();}
        return res;
    }

    /**
     * Legacy kodot futato tipusok lekerdezese
     * @deprected
     * @return typus Vector
     */
    public Vector getGridLegacyType()
    {
        Vector res=new Vector() ;
        return res;
    }

    /**
     * Gridek lekerdezese
     * @param pGridType grid tipus
     * @return grid Vector
     */
    public Vector getGrids(String pGridType)
    {
        Vector res=new Vector() ;
        String s="";
        try
        {
            BufferedReader fr=new BufferedReader(new FileReader(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/.grid.resources.conf"));
            while((s=fr.readLine())!=null)
            {
                String lin[]=s.split(" ");
                //
                String lin1 = lin[1];
                if (lin1.contains("#")) {
                    lin1 = lin1.split("#")[1];
                }
                //
                boolean newType=true;
                if(((pGridType).equals(lin1))&&(!lin[0].equals("")))if(newType)res.add(lin[0]);
            }
            fr.close();
        }
        catch(IOException e){e.printStackTrace();}
        return res;
    }
    
    /**
     * Eroforrasok lekerdezese
     * @param pGrid grid tipus
     * @param pUser portal user
     * @return site Vector
     */
    public Vector getResources(String pGrid, String pUser) throws IOException {
        Vector res=new Vector();
        File propFile = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/"+pUser+"/.resources.conf."+pGrid);
        if (propFile.exists()) {
            String s="";
            BufferedReader fr=new BufferedReader(new FileReader(propFile));
            while((s=fr.readLine())!=null) {
                String lin[]=s.split(" ");
                boolean newType=true;
                for(int i=0;i<res.size();i++)
                    if((""+res.get(i)).equals(lin[0]))newType=false;
                if(newType) {
                    if((!lin[0].equals("#"))&&(!lin[0].equals(""))) {
                        res.add(lin[0]);
                    }
                }
            }
            fr.close();
        }
        return res;
    }
    
    /**
     * Legacy kodok lekerdezese
     * @deprected
     * @param pGrid grid tipus
     * @param pResource grid site
     * @return legacy Vector
     */
    public Vector getLegacies(String pGrid, String pResource)
    {
        Vector res=new Vector() ;
        return res;
    }
    
    /**
     * Job managerek lekerdezese
     * @param pGrid grid tipus
     * @param pResource grid site
     * @param pUser portal user
     * @return Jobmanager Vector
     */
    public Vector getJobManagers(String pGrid, String pResource, String pUser)
    {
        Vector res=new Vector();
        File propFile = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/"+pUser+"/.resources.conf."+pGrid);
        if (propFile.exists()) {
            String s="";
            try {
                BufferedReader fr=new BufferedReader(new FileReader(propFile));
                while((s=fr.readLine())!=null) {
                    String lin[]=s.split(" ");
                    boolean newType=true;
                    if((pResource).equals(lin[0]))res.add(lin[1]);
                }
                fr.close();
            } catch(IOException e){e.printStackTrace();}
        }
        return res;
    }
    
    /**
     * Grid legacy tipus vizsgalat
     * @deprected
     * @param pGrid grid tipus
     * @return true/false
     */
    public boolean isLegacy(String pGrid)
    {
        return true;
    }

}
