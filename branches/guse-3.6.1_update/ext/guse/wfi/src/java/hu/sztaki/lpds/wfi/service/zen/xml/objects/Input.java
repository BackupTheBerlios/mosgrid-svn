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
 * input.java
 * Input leiro
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

import java.util.Hashtable;

/**
 * @author  krisztian
 */
public class Input
{   
    private Hashtable attributes=new Hashtable();
/**
 * Class constructor
 */   
    public Input() {}

/**
 * Attributum ertek beallitasa
 * @param pName Attributum neve
 * @param pValue Attrubutum erteke
 */    
    public void setAttribute(String pName, String pValue)
    {
        attributes.put(pName,pValue);
    }
/**
 * Csatorna Job lekerdezese
 * @return Job neve
 */    
    public String getPreJob(){return (String)attributes.get("prejob");}

/**
 * Csatorna output lekerdezese
 * @return output neve
 */    
    public String getPreOutput(){return (String)attributes.get("preoutput");}

/**
 * Parametrikus darabszam lekerdezese
 * @return darab
 */    
    public long getCount(){return (attributes.get("count")==null)?0:Long.parseLong((String)attributes.get("count"));}

/**
 * Dot es Cross product ID lekeredezese
 * @return DPID csoport azonosito
 */    
    public String getDPID(){return (attributes.get("dpid").equals("null"))?(String)attributes.get("seq"):(String)attributes.get("dpid");}


/**
 * Input nev lekerdezese
 * @return Nev
 */    
    public String getName(){return ""+attributes.get("name");}

/**
 * Input sorszam lekeredezese
 * @return Sorszam
 */    
    public String getSeq(){return ""+attributes.get("seq");}

/**
 * Csatornainput viselkedes lekerdezese
 * @return Bevarasi tulajdonsag(one/all)
 */    
    public String getWaiting(){return (attributes.get("waiting").equals("null"))?"one":(String)attributes.get("waiting");}

/**
 * Beagyazott workflow eseteben az Input orokles lekerdezese
 * @return oroles forras(input)
 */    
    public String getIinput(){return (String)attributes.get("iinput");}
    public String getIjob(){return (String)attributes.get("ijob");}
/**
 * Beagyazott workflow eseteben az Input orokles lekerdezese
 * @return oroles forras(input)
 */    
//    public String getRinput(){return (String)attributes.get("rinput");}
/**
 * Valos Job lekerdezese
 * @return Job neve
 */    
    public String getRjob(){return (String)attributes.get("rjob");}
/**
 * Valos Job PID lekerdezese
 * @return Job PID
 */    
    public String getRPID(){return (String)attributes.get("rpid");}
/**
 * Valos Workflow lekerdezese
 * @return workflow neve
 */    
    public String getRworkflow(){return (String)attributes.get("rworkflow");}
/**
 * Valos workflowRuntimeID nev lekerdezese
 * @return workflowRuntimeID
 */    
    public String getRworkflowRID(){return (String)attributes.get("rworkflowrid");}
/**
 * Valos Port sorszam lekerdezese
 * @return port sorszam
 */    
    public String getRSeq(){return (String)attributes.get("rseq");}
/**
 * Valos Port lekerdezese
 * @return Port neve
 */    
    public String getRPort(){return (String)attributes.get("rport");}
    
}
