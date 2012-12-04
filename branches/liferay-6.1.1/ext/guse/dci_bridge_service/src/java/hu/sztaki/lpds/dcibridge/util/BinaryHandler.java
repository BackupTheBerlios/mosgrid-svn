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
 * Tasks for binary handling 
 */

package hu.sztaki.lpds.dcibridge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import uri.mbschedulingdescriptionlanguage.JobTypeEnumeration;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 * @author krisztian karoczkai
 */
public class BinaryHandler {
/**
 * Reviewing the possibility of java runtime environment to run a job registered by id 
 * @param pID JOB ID
 * @return true=Java VM is necessary for running
 */
    public static boolean isJavaJob(JobDefinitionType pJSDL){
        try{
            OperatingSystemTypeEnumeration osType=pJSDL.getJobDescription().getResources().getOperatingSystem().getOperatingSystemType().getOperatingSystemName();
            return osType.equals(OperatingSystemTypeEnumeration.JAVA_VM);
        }
        catch(Exception e){return false;}
    }
/**
 * Reviewing the possibility of app.tgz extension to a job registered by id.
 * @param pID JOB ID
 * @return true=.app.tgz extension is needed
 */
    public static boolean isAppTgzExtension(JobDefinitionType pJSDL){
        try{ 
            String JobName=pJSDL.getJobDescription().getJobIdentification().getJobName();
            return JobName.endsWith(".app.tgz");
        }
        catch(Exception e){return false;}
    }
/**
 * Removing the app.tgz extension from job name
 * @param pID JOB ID
 * @return job real name
 */
    public static String getAppTgzBase(JobDefinitionType pJSDL){
        String JobName=pJSDL.getJobDescription().getJobIdentification().getJobName();
        try{ return JobName.split(".app.tgz")[0];}
        catch(Exception e){return JobName;}
    }
/**
 * Querying job commandline parameters to job id 
 * @param pID JOB ID
 * @return command line parameters
 */
    public static List<String> getCommandLineParameter(POSIXApplicationType pApp){
        List<String> res=new ArrayList<String>();
        if(pApp!=null) for(ArgumentType at:pApp.getArgument()) res.add(at.getValue());
        return res;
    }
/**
 * Querying job standard error file to job id
 * @param pID JOB ID
 * @return command line parameters
 */
    public static String getStdErrorFileName(POSIXApplicationType pApp){
        String res="";
        if(pApp!=null) res=pApp.getError().getValue();
        return res;
    }
/**
 * Querying job standard out file to job id
 * @param pID JOB ID
 * @return command line parameters
 */
    public static String getStdOutFileName(POSIXApplicationType pApp){
        String res="";
        if(pApp!=null) res=pApp.getOutput().getValue();
        return res;
    }
/**
 * Querying job standard out file to job id
 * @param pID JOB ID
 * @return command line parameters
 */
    public static String getBinaryFileName(POSIXApplicationType pApp){
        String res="";
        if(pApp!=null && pApp.getExecutable() != null) {
            res=pApp.getExecutable().getValue();
        }
        return res;
    }
/**
 * Querying user name for a job id
 * @param pID JOB ID
 * @return user name
 * @exception Exception nincs beallitva a parameter
 */
    public static String getUserName(POSIXApplicationType pApp) throws Exception{
        String res="";
        if(pApp!=null) res=pApp.getUserName().getValue();
        return res;
    }
/**
 * Querying user group (portal id in case of guse) for job id
 * @param pID JOB ID
 * @return group name
 * @exception Exception in case of lack of parameter settings
 */
    public static String getGroupName(POSIXApplicationType pApp) throws Exception{
        String res="";
        if(pApp!=null) res=pApp.getGroupName().getValue();
        return res;
    }

    /**
     * get job description parameters -jdl;rsl...
     * @param pJSDL
     * @return List, value.getName(): glite.keyRetryCount
     * value.getValue(): 4
     */
    public static List<OtherType> getOtherConstrList(JobDefinitionType pJSDL) {
           return XMLHandler.getData(pJSDL.getAny(), SDLType.class).getConstraints().getOtherConstraint();
    }

    public static boolean isMPI(JobDefinitionType pJSDL) {
        try {
            List jobtype = XMLHandler.getData(pJSDL.getAny(), SDLType.class).getConstraints().getJobType();
            Iterator itr = jobtype.iterator();
            while (itr.hasNext()) {
                if (itr.next().equals(JobTypeEnumeration.MPI) ){
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }    
      
}