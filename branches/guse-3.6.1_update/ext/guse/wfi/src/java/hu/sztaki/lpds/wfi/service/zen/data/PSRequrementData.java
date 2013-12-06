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
 * PSRequrementData.java
 * Parametrikus feltetel deffinialasa
 */

package hu.sztaki.lpds.wfi.service.zen.data;

import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Hashtable;

public class PSRequrementData 
{
    private Hashtable data=new Hashtable();
    
/**
 * Class constructor
 * @param pInputID Input neve
 * @param pJob Job neve
 * @param pCount JobPID
 */    
    public PSRequrementData(String pInputID, String pJob, Long pCount) 
    {
        data.put("inputid", pInputID);
        data.put("job", pJob);
        data.put("pid", pCount);
        data.put("status", new Integer(1));
    }

    
/**
 * Class constructor
 * @param pInputID Input neve
 * @param pJob Job neve
 * @param pCount JobPID
 * @param pStatus Statusz
 */    
    public PSRequrementData(String pInputID, String pJob, Long pCount, int pStatus) 
    {
        data.put("inputid", pInputID);
        data.put("job", pJob);
        data.put("pid", pCount);
        data.put("status", new Integer(pStatus));
    }

/**
 * Job nev lekerdezese
 * @return Job neve
 */    
    public String getJob(){return (String)data.get("job");}

/**
 * Input ID lekerdezese
 * @return Input ID
 */    
    public String getInputID(){return (String)data.get("inputid");}

/**
 * Job PID lekerdezese
 * @return Job PID
 */    
    public long getPID(){return ((Long)data.get("pid")).longValue();}

/**
 * Job stetusz lekerdezese
 * @return Job status
 */    
    public int getStatus(){return ((Integer)data.get("status")).intValue();}

/**
 * Job status beallitasa
 * @param pValueJob status
 */    
    public void setStatus(int pValue){data.put("status", new Integer(pValue));}

/**
 * Deffinialt feltetel teljesitese
 * @param pJob Job neve
 * @param pCount PID
 */    
    public void setEqualFinishedStatus(String pJob, long pCount)
    {
        if((pJob.equals((String)data.get("job")))&&(((Long)data.get("pid")).longValue()==pCount))
        data.put("status", new Integer(Status.FINISH));
    }
}
