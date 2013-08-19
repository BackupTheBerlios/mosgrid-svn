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
 * ComDataBean.java
 * Altalanos kommunikacios objektum
 */

package hu.sztaki.lpds.wfs.com;

import java.util.HashMap;

/**
 * @author krisztian
 */

public class ComDataBean 
{
/** Tarolt adatok */       
    private HashMap data=new HashMap();
/** 
 * Class constructor 
 */       
    public ComDataBean() {}
    
 /**
  * Visszaadja a portal azonosotojat
  * @return A portal feldolgozo azonosotoja
  * @see String
  */   
    public String getPortalID(){return ""+data.get("portalID");}
    
 /**
  * Visszaadja a workflow azonositojat
  * @return A workflow azonositoja
  * @see String
  */   
    public String getWorkflowID(){return ""+data.get("workflowID");}
    
 /**
  * Visszaadja a workflow runtime azonositojat
  * @return A workflow azonositoja
  * @see String
  */   
    public String getWorkflowRuntimeID(){return ""+data.get("workflowRuntimeID");}
    
 /**
  * Visszaadja a felhasznalo azonositojat
  * @return A workflow feldolgozo azonositoja
  * @see String
  */   
    public String getUserID(){return ""+data.get("userID");}
        
 /**
  * Visszaadja a workflow oset
  * @return A workflow ose
  * @see String
  */   
    public String getParentWorkflowID(){return ""+data.get("parentWorkflowID");}

 /**
  * Visszaadja az os workflow tipusut
  * 0 konkret workflow grafbol
  * 1 konkret workflow absztactbol
  * 2 abdstractworkflow konkretbol
  * 3 abdstractworkflow upload utan a parent a graf
  * 4 konkret workflow konkretbol
  * @return A workflow osenek tipusa
  * @see Integer
  */   
    public Integer getTyp(){return (Integer)data.get("typ");}

 /**
  * Visszaadja a workflow leirojat
  * @return A workflow leirasa
  * @see String
  */   
    public String getTxt(){return ""+data.get("txt");}

 /**
  * Visszaadja a workflow peldany leirojat
  * @return A workflow peldany leirasa
  * @see String
  */   
    public String getInstanceTxt(){return ""+data.get("instancetxt");}
    
 /**
  * Visszaadja a workflow skeleton neve-t
  * @return A workflow skeleton neve
  * @see String
  */   
    public String getGraf()
    {
        // System.out.println("--"+getWorkflowID()+"#"+data.get("skeleton"));
        return ""+data.get("skeleton");
    }
    
 /**
  * Visszaadja a JobID-t
  * @return A JobID
  * @see String
  */   
    public String getJobID(){return ""+data.get("jobID");}
    
 /**
  * Visszaadja a JobID-t
  * @return A JobID
  * @see String
  */   
    public String getJobPID(){return ""+data.get("pid");}
    
 /**
  * Visszaadja a workflow feldolgozo azonositojat
  * @return A workflow feldolgozo azonositoja
  * @see String
  */   
    public String getWfsID(){return ""+data.get("wfsID");}
    
 /**
  * Visszaadja a workflow feldolgozo azonosito URLjet
  * @return A workflow feldolgozo azonositoja
  * @see String
  */   
    public String getWfiURL(){return (String)data.get("wfiURL");}
    
 /**
  * Visszaadja a workflow feldolgozo azonositojat
  * @return A workflow feldolgozo azonositoja
  * @see String
  */   

    public String getStorageURL(){return (String)data.get("storageURL");}
 /**
  * Visszaadja a workflow feldolgozo azonositojat
  * @return A workflow feldolgozo azonositoja
  * @see String
  */   
    public String getWfsIDService(){return ""+data.get("wfsIDs");}
    
/**
  * Visszaadja a workflow Statuszat
  * @return A worflow statusza
  * @see String
  */   
    public String getStatus(){return (String)data.get("status");}    
    
/**
  * Visszaadja az aktualis meretett
  * @return A worflow meret
  * @see Long
  */   
    public Long getSize(){return (Long)data.get("size");}  
    
 /**
     * Visszaadja az aktualis workflow appmain erteket.
     *
     * @return A workflow appmain erteke
     * @see String
     */
    public String getAppmain() {
        // System.out.println("COMDATABEAN getAppmain():"+data.get("appmain"));
        return ""+data.get("appmain");
    }

 /**
     * Visszaadja az aktualis workflow tipusat, ez alapjan donti el a gUSE hogy melyik WFI implementacio futtataja a wf-t
     * @return A workflow tipusa
     * @see String
     */
    public String getWorkflowtype() {return ""+data.get("workflowtype");}
    
 /**
 * Beallitja a felhasznalo azonositojat
 * @param value A felhasznalo azonositoja
 * @see String
 */    
    public void setUserID(String value){data.put("userID",value);}

/**
 * Beallitja a port_l azonositojat
 * @param value A portal azonositoja
 * @see String
 */    
    public void setPortalID(String value){data.put("portalID",value);}

/**
 * Beallitja a workflow azonositojat
 * @param value A workflow azonositoja
 * @see String
 */    
    public void setWorkflowID(String value){data.put("workflowID",value);}
    
/**
 * Beallitja a workflow runtime azonositojat
 * @param value A workflow azonositoja
 * @see String
 */    
    public void setWorkflowRuntimeID(String value){data.put("workflowRuntimeID",value);}

/**
 * Beallitja a portalosenek IDj_t
 * @param value A portal osenekIDje
 * @see String
 */    
    public void setParentWorkflowID(String value){data.put("parentWorkflowID",value);}

/**
 * Beallitja a workflowosenek tipusat
 * 0 konkr_t workflow grafbol
 * 1 konkr_t workflow absztactbol
 * 2 abdstractworkflow konkretbol
 * 3 abdstractworkflow upload utan a parent a graf
 * 4 konkret workflow konkretbol
 * @param value A workflow osenek tipusa
 * @see Integer
 */    
    public void setTyp(Integer value){data.put("typ",value);}
    
/**
 * Beallitja a workflow leirojat
 * @param value A workflow leirasa
 * @see String
 */    
    public void setTxt(String value){data.put("txt",value);}
/**
 * Workflow peldanyhoz tartozo szubmitacios leiro beallitasa
 * @param value peldany nev/leiras 
 */
    public void setInstanceTxt(String value){data.put("instancetxt",value);}    
    
/**
 * Beallitja a jobID-t
 * @param value A jobID
 * @see String
 */    
    public void setJobID(String value){data.put("jobID",value);}    
    
/**
 * Beallitja a jobPID-et
 * @param value A jobID
 * @see String
 */    
    public void setJobPID(String value){data.put("pid",value);}    
    
/**
 * Beallitja a workflow tar URL-jet
 * @param value A workflow tar azonositoja
 * @see String
 */    
    public void setWfsID(String value){data.put("wfsID",value);}
    
/**
 * Beallitja a workflow tar Service azonositojat
 * @param value A workflow tar service azonositoja
 * @see String
 */    
    public void setWfsIDService(String value){data.put("wfsIDs",value);}
    
/**
 * Beallitja a workflow feldolgozo URL-jet
 * @param value A workflow feldolgozo URL-je
 * @see String
 */    
    public void setWfiURL(String value){data.put("wfiURL",value);}
    
/**
 * Beallitja a storage URL-jet
 * @param value Storge URL
 * @see String
 */    
    public void setStorageURL(String value){data.put("storageURL",value);}

/**
 * Beallitja a workflow statuszat
 * @param value A workflow statusza
 */    
    public void setStatus(String value){data.put("status",value);}    
    
/**
 * Beallitja a workflow skeleton nevet
 * @param value A workflow skeleton neve
 */    
    public void setGraf(String value)
    {
        data.put("skeleton",value);
//        System.out.println("++"+getWorkflowID()+"#"+value);
    }    
    
/**
 * Beallitja az aktualis meretet
 * @param value A workflow statusza
 */    
    public void setSize(Long value){data.put("size",value);}    
    
    /**
     * Beallitja az appmain erteket
     *
     * @param value Az appmain erteke
     */
    public void setAppmain(String value) {
        // System.out.println("COMDATABEAN setAppmain():"+value);
        data.put("appmain", value);
    }

/**
     * Beallitja az aktualis workflow tipusat, ez alapjan donti el a gUSE hogy melyik WFI implementacio futtataja a wf-t
     * @param value A workflow tipusa
     * @see String
     */
    public void setWorkflowtype(String value) {data.put("workflowtype",value);}
 

 
}
