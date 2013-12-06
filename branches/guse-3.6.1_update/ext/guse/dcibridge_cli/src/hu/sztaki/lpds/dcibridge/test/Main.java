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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.dcibridge.test;

import hu.sztaki.lpds.dcibridge.client.SubbmitterJaxWSIMPL;
import hu.sztaki.lpds.dcibridge.client.SubmitterFace;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType;
import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.CreationFlagEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDescriptionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.SourceTargetType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
import uri.mbschedulingdescriptionlanguage.ConstraintsType;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;
import uri.mbschedulingdescriptionlanguage.MiddlewareType;
import uri.mbschedulingdescriptionlanguage.MyProxyType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * @author krisztian
 */
public class Main {

    private static Class[] classes=new Class[]{
        JobDefinitionType.class,UserNameType.class,GroupNameType.class,FileNameType.class,POSIXApplicationType.class,SDLType.class};

    private static POSIXApplicationType createPosixExtension(){
        POSIXApplicationType posixApplication=new POSIXApplicationType();
//PosixApplication/user name
        UserNameType posixUserName=new UserNameType();
        posixUserName.setValue("12345");
        posixApplication.setUserName(posixUserName);
//PosixApplication/group name
        GroupNameType posixGroupName=new GroupNameType();
        posixGroupName.setValue("http://localhost:8080/wspgrade");
        posixApplication.setGroupName(posixGroupName);

//PosixApplication/error
        FileNameType posixError=new FileNameType();
//        posixError.setFilesystemName("HOME");
        posixError.setValue("stderr.log");
        posixApplication.setError(posixError);

//PosixApplication/std output
        FileNameType posixOutput=new FileNameType();
//        posixOutput.setFilesystemName("HOME");
        posixOutput.setValue("stdout.log");
        posixApplication.setOutput(posixOutput);
//PosixApplication/executable
            FileNameType posixExecutable=new FileNameType();
            posixExecutable.setValue("0");
            posixApplication.setExecutable(posixExecutable);

            return posixApplication;
    }

    public static void main(String[] args){
        SubmitterFace client=new SubbmitterJaxWSIMPL();
        client.setServiceURL("http://localhost:8080/dci_bridge_service");
        client.setServiceID("/BESFactoryService?wsdl");
        JobDefinitionType jsdltmp=new JobDefinitionType();
        CreateActivityType tmp=new CreateActivityType();
        tmp.setActivityDocument(new ActivityDocumentType());
        jsdltmp.setJobDescription(new JobDescriptionType());
        jsdltmp.getJobDescription().setApplication(new ApplicationType());
        jsdltmp.getJobDescription().getApplication().setApplicationName("guse://01234567878/job");
//posix
        jsdltmp.getJobDescription().getApplication().getAny().add(new JAXBElement( new QName("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix", "POSIXApplication_Type"), POSIXApplicationType.class, createPosixExtension()));
//outputs
        String[] outputNames=new String[]{"stderr.log","stdout.log","gridnfo.log","0","1","2","3"};
        for(String t:outputNames){
            DataStagingType output=new DataStagingType();
            output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            output.setDeleteOnTermination(new Boolean(true));
            output.setFileName(t);
            SourceTargetType outURL=new SourceTargetType();
            outURL.setURI("http://localhost/minta?"+output.getFileName());
            output.setTarget(outURL);

            jsdltmp.getJobDescription().getDataStaging().add(output);
        }

//inputs
        SourceTargetType stt=new SourceTargetType();
        stt.setURI("http://localhost/minta");
        for(int i=0;i<6;i++){
            DataStagingType dst=new DataStagingType();
            dst.setFileName(""+i);
            dst.setDeleteOnTermination(new Boolean(true));
            dst.setSource(stt);
            jsdltmp.getJobDescription().getDataStaging().add(dst);
        }
        SDLType metabroker=new SDLType();
        metabroker.setConstraints(new ConstraintsType());

        metabroker.getConstraints().getMiddleware().add(mbsdlMiddleware("local", "dci-bridge", ""));
        jsdltmp.getAny().add(new JAXBElement( new QName("uri:MBSchedulingDescriptionLanguage","SDL_Type"), metabroker.getClass(), metabroker));

        try{
        JobDefinitionType jsdl = readJSDLFromString(getJSDLXML(jsdltmp));
        tmp.getActivityDocument().setJobDefinition(jsdl);

        for(int i=0; i<1000;i++){
            long t0=System.currentTimeMillis();
            for(int j=0;j<100;j++){
                try{client.createActivity(tmp);}
                catch(Exception e){e.printStackTrace();}
            }
            t0=System.currentTimeMillis()-t0;
            System.out.println(t0);
        }
        }
        catch(Exception e){e.printStackTrace();}
    }

    private static MiddlewareType mbsdlMiddleware(String pType,String pVO,String pMyProxy){
        MiddlewareType middleware=new MiddlewareType();
        if("arc".equals(pType)) middleware.setDCIName(DCINameEnumeration.ARC);
        else if("boinc".equals(pType)) middleware.setDCIName(DCINameEnumeration.BOINC);
        else if("gae".equals(pType)) middleware.setDCIName(DCINameEnumeration.GAE);
        else if("gelmca".equals(pType)) middleware.setDCIName(DCINameEnumeration.GEMLCA);
        else if("glite".equals(pType)) middleware.setDCIName(DCINameEnumeration.GLITE);
        else if("gt2".equals(pType)) middleware.setDCIName(DCINameEnumeration.GT_2);
        else if("gt4".equals(pType)) middleware.setDCIName(DCINameEnumeration.GT_4);
        else if("local".equals(pType)) middleware.setDCIName(DCINameEnumeration.LOCAL);
        else if("lsf".equals(pType)) middleware.setDCIName(DCINameEnumeration.LSF);
        else if("pbs".equals(pType)) middleware.setDCIName(DCINameEnumeration.PBS);
        else if("service".equals(pType)) middleware.setDCIName(DCINameEnumeration.SERVICE);
        else if("unicore".equals(pType)) middleware.setDCIName(DCINameEnumeration.UNICORE);

        middleware.setManagedResource(pVO);
        MyProxyType myProxyServer=new MyProxyType();
        myProxyServer.setServerName(pMyProxy);
        middleware.setMyProxy(myProxyServer);
        return middleware;
    }

        private static  String getJSDLXML(JobDefinitionType pValue) throws Exception{
        ByteArrayOutputStream res=new ByteArrayOutputStream();
        try{//
            JAXBContext jc = JAXBContext.newInstance(classes);
            Marshaller msh=jc.createMarshaller();
            JAXBElement<JobDefinitionType> jbx =wrap( "http://schemas.ggf.org/jsdl/2005/11/jsdl", "JobDefinition_Type",  pValue);
            msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            msh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schemas.ggf.org/jsdl/2005/11/jsdl");
            msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            msh.marshal(jbx,res);
        }
        catch(Exception e){e.printStackTrace();}
        return new String(res.toByteArray());
    }

    private static JobDefinitionType readJSDLFromString(String pValue) throws JAXBException, FileNotFoundException{
        JAXBContext jc= JAXBContext.newInstance(classes);

	    Unmarshaller u = jc.createUnmarshaller();
	    JAXBElement<JobDefinitionType> obj=u.unmarshal( new StreamSource(new StringReader(pValue)), JobDefinitionType.class );
        JobDefinitionType jsdl = (JobDefinitionType) obj.getValue();

        return jsdl;
	}

        private static <T> JAXBElement<T> wrap( String ns, String tag, T o ){
        QName qtag = new QName( ns, tag,"jsdl" );
        Class<?> clazz = o.getClass();
        @SuppressWarnings( "unchecked" )
        JAXBElement<T> jbe = new JAXBElement( qtag, clazz, o );
        return jbe;
    }

}
