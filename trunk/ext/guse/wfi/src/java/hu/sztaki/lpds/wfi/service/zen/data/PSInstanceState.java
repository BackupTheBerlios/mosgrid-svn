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
 * PSInstanceState.java
 * Parametrikus Job peldany statusz
 */

package hu.sztaki.lpds.wfi.service.zen.data;

import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public class PSInstanceState 
{
    
    private long tim=0;
    private int status=1;
    private String serviceURL="";
    Vector preJobs=new Vector();
    
/**
 * Class constructor
 */    
    public PSInstanceState() {}
    
/**
 * Job vegrehajtasi feltetel hozzaadas
 * @param pValue Vegrehajtasi feltetel leiro
 * @see PSRequrementData
 */
    public void add(PSRequrementData pValue){preJobs.add(pValue);}

/**
 * Job statusz beallitasa
 * @param pStatus Statusz
 * @param pTim Status valtozosi ido
 */    
    public void setStatus(int pStatus, long pTim)
    {
        if(pTim>=tim)
        {
            tim=pTim;
            status=pStatus;
        }
    }

/**
 * Job statusz lekerdezes
 * @return Stutus
 */    
    public int getStatus(){return status;}

/**
 * Job feltetel statusz beallitasa
 * @param pJob Job neve
 * @param pPID Job PID
 */    
    public void setRequirementFinishedStatus(String pJob, long pPID)
    {
        for(int i=0;i<preJobs.size();i++)((PSRequrementData)preJobs.get(i)).setEqualFinishedStatus(pJob, pPID);
    }

/**
 * Job peldany futtatasi vizsgalat
 * @return true eset_n fut
 */    
    public boolean isRunning()
    {
        boolean res=true;
        for(int i=0;(i<preJobs.size())&&res;i++)
        {
            res=res&&(((PSRequrementData)preJobs.get(i)).getStatus()<6)&&(((PSRequrementData)preJobs.get(i)).getStatus()>1);
        }
        return res;
    }

/**
 * Job peldany futtathatosagi vizsgalat
 * @return true eseten futtathato
 */    
    public boolean isRunnable()
    {
        boolean res=true;
        int istatus;
        for(int i=0;(i<preJobs.size())&&res;i++)
        {
            istatus=((PSRequrementData)preJobs.get(i)).getStatus();
            res=res && Status.isFinished(istatus);
        }
        return res&&(status==1);
    }

/**
 * Job peldany futas befejezes vizsgalat
 * @return true esetan mar lefutott
 */    
    public boolean isFinished(){return Status.isEndStatus(status);}

/**
 * Submitter URL beallitasa
 * @param pServiceURL URL
 */    
    public void submit(String pServiceURL){serviceURL=pServiceURL;}

/**
 * Futatasi feltetelek visszaadasa
 * @return Job peldany feltetelek
 */    
    public Vector getAllPreJobData(){return preJobs;}

/**
 * Adott futatasi feltetel visszaadasa
 * @param pValue Feltetel sorszama
 * @return Job peldany feltetel
 * @see PSRequrementData
 */    
    public PSRequrementData getPreJobData(int pValue){return (PSRequrementData)preJobs.get(pValue);}
}
