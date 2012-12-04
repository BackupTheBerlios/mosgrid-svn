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
 * XML parameters handling
 */

package hu.sztaki.lpds.dcibridge.util;

import dci.extension.ExtensionType;
import hu.sztaki.lpds.dcibridge.service.Base;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.service.LB;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.ggf.schemas.jsdl._2005._11.jsdl.*;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.LimitsType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
import org.w3c.dom.Element;
import uri.mbschedulingdescriptionlanguage.SDLType;
/**
 * @author krisztian karoczkai
 */
public class XMLHandler {
    private static final Class[] classes=new Class[]{
        JobDefinitionType.class,UserNameType.class,GroupNameType.class,FileNameType.class,
        ArgumentType.class,LimitsType.class, EnvironmentType.class,POSIXApplicationType.class,
        SDLType.class,ExtensionType.class};

/**
 * Layer-level searching among tags that are in the same branch.
 * @param pList element list
 * @param pNodeName tag name
 * @return tag text content
 * @throws java.lang.NullPointerException given tag is not in the list
 */
    public static String getFirstTextContent(NodeList pList,String pNodeName) throws NullPointerException{
        Node tmp;
        for(int i=0;i<pList.getLength();i++){
                tmp=pList.item(i);
                if(pNodeName.equals(tmp.getNodeName())) return tmp.getTextContent();
            }
        throw new NullPointerException(pNodeName+" not exist");
    }

    public static <T> JAXBElement<T> unMarshal(Node pNode, T pClass ) throws JAXBException, FileNotFoundException{

        JAXBContext jc= JAXBContext.newInstance(classes);
        Class<?> clazz = pClass.getClass();
	    Unmarshaller u = jc.createUnmarshaller();
	    JAXBElement<T> obj=(JAXBElement<T>) u.unmarshal( pNode, clazz );
        return obj;
	}

/**
 * Retrieving JSDL from the Job BES submit descriptor
 * @param pJob job description object
 * @return xml stringkent
 */

    public static String getJSDLXML(JobDefinitionType pValue){
        ByteArrayOutputStream res=new ByteArrayOutputStream();
        try{
//            JAXBContext jc= JAXBContext.newInstance(new Class[]{JobDefinitionType.class,UserNameType.class,GroupNameType.class,FileNameType.class,ArgumentType.class,LimitsType.class,POSIXApplicationType.class,ExtensionType.class,SDLType.class, EnvironmentType.class});
            JAXBContext jc= JAXBContext.newInstance(classes);

            Marshaller msh=jc.createMarshaller();
            JAXBElement<JobDefinitionType> jbx =wrap( "http://schemas.ggf.org/jsdl/2005/11/jsdl", "JobDefinition", pValue );
            msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            msh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schemas.ggf.org/jsdl/2005/11/jsdl");
            msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            msh.marshal(jbx,res);
        }
        catch(Exception e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
        return new String(res.toByteArray());
    }

/**
 * JAXB list creation from existing objects for XML 
 * @param <T> Root object type
 * @param ns nameSpace
 * @param tag root tag name
 * @param o Object for XML generation
 * @return List for XML generation
 */
    private static <T> JAXBElement<T> wrap( String ns, String tag, T o ){
        QName qtag = new QName( ns, tag,"jsdl" );
        Class<?> clazz = o.getClass();
        @SuppressWarnings( "unchecked" )
        JAXBElement<T> jbe = new JAXBElement( qtag, clazz, o );
        return jbe;
    }

    public static <T> T getData(List<Object> pAny,Class<T> pClient) {
        Object r=null;
        for(int i=0;i<pAny.size();i++){
            r=pAny.get(i);
            if(r.getClass().getName().equals(pClient.getName()) )
                return (T)r;
        }
        return null;
    }

    public static void crateExtensionTaginJSDL(Job pJob){
        List<Object> l=pJob.getJSDL().getAny();
        Element r;
        for(int i=0;i<l.size();i++){
            r=(Element)l.get(i);
            try{
                if("SDL_Type".equals(r.getLocalName())){
                        JAXBElement<SDLType> mbsdl=XMLHandler.unMarshal(r, new SDLType());
                        pJob.getJSDL().getAny().add(i,mbsdl.getValue());
                        pJob.getJSDL().getAny().remove(i+1);
                }
                else if("extension_type".equals(r.getLocalName())){
                        JAXBElement<ExtensionType> extDCI=XMLHandler.unMarshal(r, new ExtensionType());
                        pJob.getJSDL().getAny().add(i,extDCI.getValue());
                        pJob.getJSDL().getAny().remove(i+1);
                }
            }catch(Exception e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
        }
        l=pJob.getJSDL().getJobDescription().getApplication().getAny();
        for(int i=0;i<l.size();i++){
            r=(Element)l.get(i);
            try{
                if("POSIXApplication_Type".equals(r.getLocalName())){
                        JAXBElement<POSIXApplicationType> posixApp=XMLHandler.unMarshal(r, new POSIXApplicationType());
                        pJob.getJSDL().getJobDescription().getApplication().getAny().add(i,posixApp.getValue());
                        pJob.getJSDL().getJobDescription().getApplication().getAny().remove(i+1);
                }
            }catch(Exception e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
        }
    }

/**
 * Decide the job passing to Metabroker for runnable resource selection.
 * @param pJob
 * @return
 */
    public static boolean isMetaBrokerJob(SDLType mbsdl){
        boolean res=false;
        if(mbsdl!=null)
            res=res || mbsdl.getConstraints().getMiddleware().size()>1;
        return res;
    }
   
}
