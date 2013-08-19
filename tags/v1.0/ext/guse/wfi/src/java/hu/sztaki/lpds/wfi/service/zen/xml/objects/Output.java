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
 * Output.java
 * Output leiro bean
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

import java.util.Hashtable;
import java.util.Vector;

public class Output 
{
    private Hashtable data=new Hashtable();
    private Vector parentoutput=new Vector();
    private Vector<Job> postJobs=new Vector<Job>();
    private long counts=0;
/** 
 * Class constructor
 */
    public Output() {}
    
/** 
 * Class constructor
 * @deprected
 * @param pInternal belso file nev
 * @param pJob job neve
 * @param pOutput port neve
 */
    public Output(String pInternal, String pJob, String pOutput) 
    {
        data.put("internal",pInternal);
        data.put("job",pJob);
        data.put("output",pOutput);
        data.put("count","1");
        
    }
/**
 * Kimenethez tartozo csatornafuggosegek deffinialasa
 * @param pJob
 */
    public void addPostJob(Job pJob)
    {
        boolean b=false;
        for(int i=0;(i<postJobs.size())&&(!b);i++) 
            b=postJobs.get(i).equals(pJob);
        if(!b) postJobs.add(pJob);
    }

    public synchronized void addCounts(long pvalue){counts+=pvalue;}

    public long getCounts(){return counts;}
/**
 * Beagyazott Workflow lekerdezese
 * @return workflow neve
 */    
    public String getInternal(){return (String)data.get("internal");}

/**
 * Job lekerdezese
 * @return Job neve
 */    
    public String getJob(){return (String)data.get("job");}

/**
 * Output lekerdezese
 * @return Output neve
 */    
    public String getOutput(){return (String)data.get("output");}

/**
 * Output szamossaganak lekerdezese
 * @return Output szama jobonkent
 */    
//    public int getCount(){return (data.get("count")==null)?0:Integer.parseInt((String)data.get("count"));}

/**
 * Egy job peldany eseteben megjeleno kimenetek szama a porton
 * @return mainCount
 */    
    public int getMainCount()
    {
        if(data.get("maincount")==null)return 1;
        else if(data.get("maincount").equals("null")) return 1;
        return Integer.parseInt((String)data.get("maincount"));
    }
/**
 * Output port generatorsaganak vizsgalata
 * @return true=generator output
 */
    public boolean isGenerator(){return getMainCount()>1;}
/**
 * Porton jelentkezo fileok darabszamanak lekerdezese
 * @return aktualis elemszam
 */
    public long getRealCount()
    {
        try{return Long.parseLong((String)data.get("realCount"));}
        catch(Exception e){return 0;}
    }
/**
 * Port nevenek lekerdezese
 * @return port neve
 */    
    public String getName(){return (String)data.get("name");}
/**
 * Beagyazott workflow eseten a szulo szamara visszaadott output Job nevenek lekerdezese
 * @return Job neve
 */    
    public String getIJob(){return (String)data.get("ijob");}
/**
 * Beagyazott workflow eseten a szulo szamara visszaadott output Port nevenek lekerdezese
 * @return port neve
 */    
    public String getIOutput(){return (String)data.get("ioutput");}
    
/**
 * Attrubutum beallitasa
 * @param pKey kulcs
 * @param pvalue ertek
 */    
    public void setAttribute(String pKey, String pValue){data.put(pKey,pValue);}
    
}
