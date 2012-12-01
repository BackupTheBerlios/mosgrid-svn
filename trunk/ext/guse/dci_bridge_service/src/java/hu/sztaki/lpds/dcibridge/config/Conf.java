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
 * Configuration handler
 */

package hu.sztaki.lpds.dcibridge.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import dci.data.*;
import dci.data.Configure.*;
import dci.data.Item;
import dci.data.Item.*;
import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.service.Base;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Marshaller;


/**
 * @author krisztian karoczkai
 */
public class Conf {
    private static Configure instance;
    private static File configXML;

/**
 * Configuration initialization
 * @param pConfig default configuration file
 */
    public static void init(File pConfig) {
        try{
            configXML=pConfig;
            JAXBContext jc = JAXBContext.newInstance("dci.data");
            Unmarshaller u = jc.createUnmarshaller();
            instance = (Configure)u.unmarshal(configXML);
            Base.getI();
        }
        catch(Exception e){
            e.printStackTrace();
            instance=new Configure();
            instance.setMiddlewares(new Middlewares());
            instance.setProperties(new Configure.Properties());
            instance.setSystem(new Configure.System());
            try{
                configXML.createNewFile();
                write();
            }
            catch(Exception e0){e0.printStackTrace();}

        }
    }

/**
 * Service configuration query
 * @return configuration instance
 */
    public static Configure getC(){return instance;}

/**
 * Middleware configuration query
 * @return configuration instance
 */
    public static Middlewares getM(){return instance.getMiddlewares();}

/**
 * System configuration query
 * @return configuration instance
 */
    public static Configure.System getS(){return instance.getSystem();}

/**
 * System configuration query
 * @return configuration instance
 */
    public static Configure.Properties getP(){return instance.getProperties();}

    public static Middleware getMiddleware(String pName) throws NullPointerException{
        for(Middleware t:getM().getMiddleware())
            if(t.getType().equals(pName)) return t;
        throw new NullPointerException(pName +" middleware type not configured");
    }

    public static Item getItem(String pMiddleware,String pName) throws NullPointerException{
        Middleware tmp=getMiddleware(pMiddleware);
        for(Item t:tmp.getItem())
            if(t.getName().equals(pName)) return t;
        throw new NullPointerException(pMiddleware +" middleware not  includes "+pName);
    }

    public static Middleware getMiddlewareNewInstanceIfNotExist(String pName){
        for(Middleware t:getM().getMiddleware())
            if(t.getType().equals(pName)) return t;
        Middleware t=new Middleware();
        t.setType(pName);
        getM().getMiddleware().add(t);
        return t;
    }

    public static Item getItemNewInstanceIfNotExist(String pMiddleware,String pName) throws NullPointerException{
        java.lang.System.out.println("getItemNewInstanceIfNotExist("+pMiddleware+","+pName+")");
		Middleware tmp=getMiddlewareNewInstanceIfNotExist(pMiddleware);
        for(Item t:tmp.getItem())
            if(t.getName().equals(pName)) return t;
        Item t=new Item();
        t.setName(pName);
        t.setForward(new Forward());
        t.getForward().setUsethis("true");
        tmp.getItem().add(t);
        return t;
    }

    public static boolean isExistItemInMiddleware(String pMiddleware,String pName){
        Middleware tmp=getMiddleware(pMiddleware);
        for(Item t:tmp.getItem())
            if(t.getName().equals(pName)) return true;
        return false;
    }


/**
 * Searching a queue in a given cluster. If either of parameter is null, analysis is not happend
 * @param pCluster Pbs cluster configuration descriptor
 * @param pName new queue name
 * @return true=it is in list
 */
    public static boolean isQueueInPbs(Pbs pCluster,String pName){
        if(pCluster==null || pName==null) return false;
        boolean b=false;
        for(String t:pCluster.getQueue())
            b=b||pName.equals(t);
        return b;
    }


/**
 * Searching a queue in a given cluster. If either of parameter is null, analysis is not happend
 * @param pCluster Pbs cluster configuration descriptor
 * @param pName queue name
 * @return true=it is in list
 */
    public static boolean isQueueInLsf(Lsf pCluster,String pName){
        if(pCluster==null || pName==null) return false;
        boolean b=false;
        for(String t:pCluster.getQueue())
            b=b||pName.equals(t);
        return b;
    }

    public static boolean isJobInBoinc(Boinc pBoinc,String pName){
        if(pBoinc==null || pName==null) return false;
        boolean b=false;
        for(Boinc.Job t:pBoinc.getJob())
            b=b||t.getName().equals(pName);
        return b;
    }
/**
 * Querying certificate styles are available by system to a string list.
 * @return Certificate name list
 */
    public static List<String> getCertificateTypesInStringList(){
        List<String> res=new ArrayList<String>();
        for(Certificate c:Certificate.values()) res.add(c.value());
        return res;
    }

/**
 * Writing current configuration file to XML 
 */
    public static void write() throws JAXBException, FileNotFoundException, IOException {
        ByteArrayOutputStream res=new ByteArrayOutputStream();
            JAXBContext jc = JAXBContext.newInstance(Configure.class);
            Marshaller msh=jc.createMarshaller();
            msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            msh.marshal(instance,res);
            FileOutputStream ff=new FileOutputStream(configXML);
            ff.write(res.toByteArray());
            ff.flush();
            ff.close();
    }
}
