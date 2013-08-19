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
 * Rescue.java
 * Resscue statusz kezeles
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

import java.util.Hashtable;

public class Rescue 
{
    private Hashtable<String,Integer> outputs=new Hashtable<String,Integer>();
    private long pid=0;
 
    
/**
 * Class constructor
 * @param pName Job neve
 * @param pPid Job PID
 * @param pStatus Job statusz
 */
    public Rescue(long pPid,Hashtable<String,Integer> pOutputs)
    {
        pid=pPid;
        outputs=pOutputs;
    }
    
    
/**
 * Job Pid lekerdezese
 * @return Job PID
 */
    public long getPid(){return pid;}
    
/**
 * Job outputjainak lekerdezese
 * @return Job neve
 */
    public Hashtable<String,Integer> getOutputs(){return outputs;}
    
}
