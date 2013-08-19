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
 * IOInherited.java
 * egy workflowrol workflowra oroklodo IO port leiro bean
 */

package hu.sztaki.lpds.wfi.service.zen.data;

import java.util.Enumeration;
import java.util.Hashtable;

public class IOInherited 
{
    
    private Hashtable datas=new Hashtable();
/**
 * Class construktor
 */
    public IOInherited() {}
    
/**
 * Workflow nevenek beallitasa
 * @param pData Workflow neve
 */    
    public void setWorkflow(String pData){datas.put("workflow",pData);}
    
/**
 * WorkflowRuntimeID  beallitasa
 * @param pData WorkflowRuntimeID
 */    
    public void setWorkflowRID(String pData){datas.put("workflowrid",pData);}
    
/**
 * Job nevenek beallitasa
 * @param pData Job neve
 */    
    public void setJob(String pData){datas.put("job",pData);}
    
/**
 * Job peldany beallitasa
 * @param pData Job peldany
 */    
    public void setPID(String pData){datas.put("pid",pData);}
    
/**
 * Port nevenek beallitasa
 * @param pData Port neve
 */    
    public void setPort(String pData){datas.put("port",pData);}
    
/**
 * Port sorszamanak beallitasa
 * @param pData Port sorszama
 */    
    public void setSeq(String pData){datas.put("seq",pData);}
    
/**
 * Valos Job index beallitasa
 * @param pData Job Index
 */    
    public void setRealIdx(String pData){datas.put("idx",pData);}

    
/**
 * Workflow nevenek lekerdezes
 * @return workflow neve
 */    
    public String getWorkflow(){return (String)datas.get("workflow");}
    
/**
 * WorkflowRuntimeID lekerdezes
 * @return workflowRuntimeID
 */    
    public String getWorkflowRID(){return (String)datas.get("workflowrid");}
    
/**
 * Job nevenek lekerdezes
 * @return Job neve
 */    
    public String getJob(){return (String)datas.get("job");}
    
/**
 * JOB PID lekerdezes
 * @return job pid
 */    
    public String getPID(){return (String)datas.get("pid");}
    
/**
 * Port nevenek lekerdezes
 * @return Port neve
 */    
    public String getPort(){return (String)datas.get("port");}
    
/**
 * Port sorszamanak lekerdezes
 * @return Port sorszama
 */    
    public String getSeq(){return (String)datas.get("seq");}
    
    public String getIndex(){return (String)datas.get("gindex");}
    public void setIndex(String pValue){datas.put("gindex",pValue);}
/**
 * Port valos indexenek lekerdezes
 * @return Port valos indexe
 */    
    public String getRealIdx(){return (String)datas.get("idx");}
    @Override
    public String toString() 
    {
        StringBuffer res=new StringBuffer("");
        Enumeration enm=datas.keys();
        Object key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            res.append("\t<"+key+" value=\""+datas.get(key)+"\" />\n");
        }
        
        return res.toString(); 

    }

}
