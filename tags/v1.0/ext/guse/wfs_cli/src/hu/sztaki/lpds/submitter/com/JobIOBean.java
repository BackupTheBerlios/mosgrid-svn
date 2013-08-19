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
 * JobIOBean.java
 * Egy submitalando Job Input/outputjait leiro bean
 *
 */

package hu.sztaki.lpds.submitter.com;

import java.util.Enumeration;
import java.util.Hashtable;

public class JobIOBean {
    
    private Hashtable datas=new Hashtable();
/**
 * Class constructor
 */
    public JobIOBean() { }

/**
 * Class constructor
 * @param p0 workflowID 
 * @param p1 workflowRuntimeID
 * @param p2 jobID
 * @param p3 port sorszam
 * @param p4 port neve
 * @param p5 PID
 * @param p6 index
 */
    public JobIOBean(String p0, String p1, String p2,String p3,String p4,String p5,String p6) 
    { 
        setWorkflowID(p0);
        setRuntimeID(p1);
        setJobID(p2);
        setSeq(p3);
        setName(p4);
        setPID(p5);
        setIndex(p6);
    }

/**
 * Class constructor
 * @param p0 workflowID 
 * @param p1 workflowRuntimeID
 * @param p2 jobID
 * @param p3 port sorszam
 * @param p4 port neve
 * @param p5 PID
 * @param p6 CollectorNumber
 * @param p7 index
 */
    public JobIOBean(String p0, String p1, String p2,String p3,String p4,String p5,String p6,String p7) 
    { 
        setWorkflowID(p0);
        setRuntimeID(p1);
        setJobID(p2);
        setSeq(p3);
        setName(p4);
        setPID(p5);
        setCollectorNumber(p6);
        setIndex(p7);
    }
    
    
/**
 * Class constructor
 * @param p0 workflowID 
 * @param p1 workflowRuntimeID
 * @param p2 jobID
 * @param p3 port neve
 * @param p4 PID
 * @param p6 History
 * @param p5 fake parameter
 * @param p7 index
 */
    public JobIOBean(String p0, String p1, String p2,String p3,String p4,String p6,boolean p5,String p7) 
    { 
        setWorkflowID(p0);
        setRuntimeID(p1);
        setJobID(p2);
        setName(p3);
        setPID(p4);
        setHistory(p6);
        setIndex(p7);
    }
/**
 * Class constructor
 * @param p0 workflowID 
 * @param p1 jobID
 * @param p2 port sorszam
 * @param p3 port neve
 * @param p4 PID
 * @param p5 index
 */
    public JobIOBean(String p0,String p1,String p2,String p3,String p4,String p5) 
    { 
        setWorkflowID(p0);
        setJobID(p1);
        setSeq(p2);
        setName(p3);
        setPID(p4);    
        setIndex(p5);
    }

/**
 * WorkflowID lekerdezese
 * @return workflowID
 */    
    public String getWorkflowID(){return (String)datas.get("workflowid");}

/**
 * Collector szamossag lekerdezese
 * @return workflowID
 */    
    public String getCollectorNumber()
    {
        if(datas.get("ncol")==null) return "1";
        else return ((String)datas.get("ncol"));
    }
    
/**
 * WorkflowRuntimeID lekerdezese
 * @return workflowRuntimeID
 */    
    public String getRuntimeID(){return (String)datas.get("runtimeid");}

/**
 * JobID lekerdezese
 * @return JobID
 */    
    public String getJobID(){return (String)datas.get("jobid");}

/**
 * Port sorszam lekerdezese
 * @return port sorszam
 */    
    public String getSeq(){return (String)datas.get("seq");}

/**
 * Port nev lekerdezese
 * @return port neve
 */    
    public String getName(){return (String)datas.get("name");}

/**
 * Job pid lekerdezese
 * @return job PID
 */    
    public String getPID(){return (String)datas.get("pid");}

/**
 * job input history lekerdezese
 * @return input port history xml string
 */    
    public String getHistory(){return (String)datas.get("history");}    

/**
 * Port index lekerdezese
 * @return index erteke
 */    
    public String getIndex(){return (String)datas.get("index");}    

    /**
 * WorkflowID beallitasa
 * @param pValue workflowID 
 */    
    public void setWorkflowID(String pValue){datas.put("workflowid",pValue);}
/**
 * WorkflowRuntimeID beallitasa
 * @param pValue workflowRuntimeID 
 */    
    public void setRuntimeID(String pValue){datas.put("runtimeid",pValue);}
/**
 * JobID beallitasa
 * @param pValue JobID 
 */    
    public void setJobID(String pValue){datas.put("jobid",pValue);}
/**
 * Port sorszam beallitasa
 * @param pValue port sorszam
 */    
    public void setSeq(String pValue){datas.put("seq",pValue);}
/**
 * Port nev beallitasa
 * @param pValue port neve
 */    
    public void setName(String pValue){datas.put("name",pValue);}
/**
 * PID beallitasa
 * @param pValue PID
 */    
    public void setPID(String pValue){datas.put("pid",pValue);}
/**
 * Hivasi input history beallitasa
 * @param pValue input history
 */    
    public void setHistory(String pValue){datas.put("history",pValue);}
/**
 * Collector szamossag beallitasa
 * @param pValue input history
 */    
    public void setCollectorNumber(String pValue)
    {
        datas.put("ncol",pValue);
    }
/**
 * porton jelentkezo file index. generator eseten nem -1
 * @param pValue index erteke
 */
    public void setIndex(String pValue){datas.put("index",pValue);}
/**
 * Tarolt adatok megjelenitese xml formatumban
 * @return xml adat leiro
 */    

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
