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
 * SaveAllData.java
 * Portal side saving of workflow configuration data
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Portal side saving of workflow configuration data
 *
 * @author krisztian karoczkai
 */
public class SaveAllData extends BASEActions
{
    
    /** Creates a new instance of SaveAllData */
    public SaveAllData() {}
    @Override
    public String getDispacher(Hashtable pParams){return "/jsp/msg.jsp";}
    @Override
    public String getOutput(Hashtable pParams){return null;}
    @Override
    public synchronized Hashtable getParameters(Hashtable pParams){
        Enumeration<String> enmParams=pParams.keys();
        String keyParam;
        System.out.println("***********************");
        while(enmParams.hasMoreElements()){
            keyParam=enmParams.nextElement();
            System.out.println("*"+keyParam+"="+pParams.get(keyParam));
        }
        String user=""+pParams.get("user");
        JobPropertyBean editingJob=PortalCacheService.getInstance().getUser(user).getEditingJobData();
//is workflow
        if(pParams.get("job_iworkflow")!=null){
            deleteGridProperties(editingJob);
            deleteServiceProperties(editingJob);
            deleteCloudProperties(editingJob);
            deleteBinaryProperties(editingJob);
        }
//is binary
        else if(pParams.get("job_grid")!=null){
            deleteGridProperties(editingJob);
            deleteServiceProperties(editingJob);
            deleteCloudProperties(editingJob);
            deleteEmbedWorkflowProperties(editingJob);
        }
//is service
        else if(pParams.get("job_servicetype")!=null){
            deleteGridProperties(editingJob);
            deleteCloudProperties(editingJob);
            deleteEmbedWorkflowProperties(editingJob);
            deleteBinaryProperties(editingJob);
        }
//is cloud
        else if(pParams.get("job_cloudtype")!=null){
            deleteGridProperties(editingJob);
            deleteServiceProperties(editingJob);
            deleteEmbedWorkflowProperties(editingJob);
            deleteBinaryProperties(editingJob);
        }
//metabroker
        else if(pParams.get("job_mbt")!=null){
            deleteGridProperties(editingJob);
            deleteServiceProperties(editingJob);
            deleteCloudProperties(editingJob);
            deleteEmbedWorkflowProperties(editingJob);
        }

        Enumeration enm=pParams.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            if(key.startsWith("tmp*"))pParams.put(key,"");
//            if(!pParams.get(key).equals(""))
            {
                pParams.put(key,(""+pParams.get(key)).replace("\\", "/"));                
                String[] p=key.split("_");
// saving of job parameters
                if(p[0].equals("job")&&(p.length>1)){
                     if(!pParams.get(key).equals("")){
                         try{
                             if(p.length>=3){
                                if ("binary".equals(p[2])) {
                                    editingJob.getExe().put(p[2], "" + pParams.get(key));
                                }
                             }
                             else{
                                 editingJob.getExe().put(p[1], "" + pParams.get(key));
                             }
                         } 
                         catch (Exception e){e.printStackTrace();}
                     }
                     else {
                         System.out.println("SAVE:"+p[1]);
                         editingJob.getExe().remove(p[1]);
                     }
                }
//input parameters
                if(p[0].equals("input")&&(PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1])!=null))
                {
                    if((p[2].equals("equaltype"))&&(pParams.get("input_"+p[1]+"_equaltype").equals("")))
                    {
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("pequalinput");
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("pequalvalue");
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("pequaltype");
                    }
                    if((p[2].equals("pequalvalue"))&&(!pParams.get("input_"+p[1]+"_pequalvalue").equals("")))PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("pequalinput");
                    if((p[2].equals("pequalinput"))&&(!pParams.get("input_"+p[1]+"_pequalinput").equals("")))PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("pequalvalue");
                    if((p[2].equals("remote")&&(!pParams.get("input_"+p[1]+"_remote").equals(""))) ||
                       (p[2].equals("value")&&(!pParams.get("input_"+p[1]+"_value").equals(""))) ||
                       (p[2].equals("sqlurl")&&(!pParams.get("input_"+p[1]+"_sqlurl").equals("")))||
                       (p[2].equals("sqlselect")&&(!pParams.get("input_"+p[1]+"_sqlselect").equals("")))
                       )
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove("file");
                    if(!pParams.get(key).equals(""))
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().put(p[2],""+pParams.get(key));
                    else
                    {
                        if(!p[2].equals("file"))
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getInput(p[1]).getData().remove(p[2]);
                    }
                    if (p[2].equals("gout") && "gemlca".equals("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"))) {
                        String sgout =""+pParams.get("input_"+p[1]+"_gout");//kivalasztott gemlca io parameter
                        String newintname = ""+pParams.get("input_"+p[1]+"_intname");
                        String gparams = "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params");                        
                        
                        String newgparams= ""+GemlcaCacheService.getInstance().getNewExeParams("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"), "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"), newintname, sgout, gparams, true);
                        ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("params", newgparams);                        
                    }

                }
                if(p[0].equals("output")) 
                {
                    if(!pParams.get(key).equals(""))
                    {
                        for(int ii=0;ii<((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutputs().size();ii++)
                        if((PortalCacheService.getInstance().getUser(user).getEditingJobData().getOutput(p[1])!=null))
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getOutput(p[1]).getData().put(p[2],""+pParams.get(key));
                    }
                    else
                        PortalCacheService.getInstance().getUser(user).getEditingJobData().getOutput(p[1]).getData().remove(p[2]);
                    
                    if (p[2].equals("gout") && "gemlca".equals("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"))) {
                        String sgout =""+pParams.get("output_"+p[1]+"_gout");//kivalasztott gemlca io parameter
                        String newintname = ""+pParams.get("output_"+p[1]+"_intname");
                        String gparams = "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params");
                        
                        String newgparams= ""+GemlcaCacheService.getInstance().getNewExeParams("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"), "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"), newintname, sgout, gparams, false);
                        ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("params", newgparams);
                    }
                }

                if(p[0].equals("desc")) 
                {
                    if(!pParams.get(key).equals("")){
                        ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getDesc().put(pParams.get("desc_key"),pParams.get("desc_value"));
                    }
                    else 
                        ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getDesc().remove(pParams.get("desc_key"));
                }
            }
        }
//Settings for Workflow all jobs

        if((pParams.get("job_useallthis")!=null)||(pParams.get("job_useallthisw")!=null)||(pParams.get("job_useallthiss")!=null))
        {
            System.out.println("------use all----");
            Vector<JobPropertyBean> tmp=PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow();
            for(int i=0;i<tmp.size();i++){
                if(!PortalCacheService.getInstance().getUser(user).getEditingJobData().getName().equals(tmp.get(i).getName()))
                if(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype")!=null){
                    try{
                    deleteServiceProperties(tmp.get(i));
                    deleteCloudProperties(tmp.get(i));
                    deleteEmbedWorkflowProperties(tmp.get(i));
                    deleteGridProperties(tmp.get(i));
                    tmp.get(i).getExe().put("grid",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"));
                    tmp.get(i).getExe().put("resource",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"));
                    tmp.get(i).getExe().put("jobmanager",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("jobmanager"));
                    tmp.get(i).getExe().put("gridtype",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"));

                    Iterator<String> it=PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().keySet().iterator();
                    String key;
                    while(it.hasNext()){
                        key=it.next();
                        if(key.startsWith("mbt"))
                            ((JobPropertyBean)tmp.get(i)).getExe().put(key,PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get(key));
                    }
                    }
                    catch(Exception e){e.printStackTrace();}
                }
                else if(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicetype")!=null){
                    System.out.println("------service----");
                    deleteGridProperties(tmp.get(i));
                    deleteCloudProperties(tmp.get(i));
                    deleteEmbedWorkflowProperties(tmp.get(i));
                    ((JobPropertyBean)tmp.get(i)).getExe().put("servicemethod",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicemethod"));
                    ((JobPropertyBean)tmp.get(i)).getExe().put("servicetype",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicetype"));
                    ((JobPropertyBean)tmp.get(i)).getExe().put("serviceurl",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("serviceurl"));                
                }
                else if(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("iworkflow")!=null){
                    deleteGridProperties(tmp.get(i));
                    deleteServiceProperties(tmp.get(i));
                    deleteCloudProperties(tmp.get(i));
                    ((JobPropertyBean)tmp.get(i)).getExe().put("iworkflow",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("iworkflow"));
                }
                
            }                
        }

        //Copy each job name to idbtools
        if((pParams.get("job_jobnamestotools")!=null)) {
            System.out.println("------copy job names to tools----");
            Vector<JobPropertyBean> tmp=PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow();
            for(int i=0;i<tmp.size();i++){
            	try {
            		String jobname = tmp.get(i).getName();
            		if(jobname!=null && !jobname.equals("")){
            			int j = jobname.length() - 1;
            			while ((Character.isDigit(jobname.charAt(j)) || jobname.charAt(j) == '.') && j > 0) {
            				j--;
            			}
            			if (j < jobname.length() - 1 && j > 0){
            				String buffer = new String(jobname);
            				jobname = buffer.substring(0, j+1).concat(" ");
            				jobname = jobname.concat(buffer.substring(j+1));
            			}
                    	tmp.get(i).getExe().put("jobmanager", jobname);
                	}

                }
            	catch(Exception e){e.printStackTrace();}
            }
        }

        
//        ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInput(""+pParams.get("id")).getData().put("parametric", ""+pParams.get("act"));
        
        //saving.....
        Hashtable res=new Hashtable();
        res.put("msg","workflow.config.save.data");
        return res;
    }

    private JobPropertyBean deleteGridProperties(JobPropertyBean pValue){
        pValue.getExe().remove("gridtype");
        pValue.getExe().remove("grid");
        pValue.getExe().remove("jobmanager");
        pValue.getExe().remove("resource");
        pValue.getExe().remove("mbt");
        for(int i=0;i<100;i++)  pValue.getExe().remove("mbt"+i);
        return pValue;
    }

    private JobPropertyBean deleteServiceProperties(JobPropertyBean pValue){
        pValue.getExe().remove("servicetype");
        pValue.getExe().remove("serviceurl");
        pValue.getExe().remove("servicemethod");
        return pValue;
    }

    private JobPropertyBean deleteCloudProperties(JobPropertyBean pValue){
        pValue.getExe().remove("cloudtype");
        pValue.getExe().remove("gaeurl");
        pValue.getExe().remove("defaultgaeservice");
        return pValue;
    }

    private JobPropertyBean deleteEmbedWorkflowProperties(JobPropertyBean pValue){
        pValue.getExe().remove("iworkflow");
        return pValue;
    }

    private JobPropertyBean deleteBinaryProperties(JobPropertyBean pValue){
        pValue.getExe().remove("params");
        pValue.getExe().remove("file");
        pValue.getExe().remove("type");
        return pValue;
    }
}
