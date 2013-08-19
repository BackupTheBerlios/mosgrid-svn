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
 * LocalFilesImpl.java
 * Lokalis File perzistencia manager
 *
 */

package hu.sztaki.lpds.wfi.service.zen.util.perzistence;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfi.service.zen.util.perzistence.inf.Perzistence;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;


public class LocalFilesImpl implements Perzistence
{
/**
 * Class constructor
 */    
    public LocalFilesImpl() {}
   
/**
 * Uj perzistencia peldany letrehozasa
 * @param pZenID WorkflowRuntimeID
 * @param pParentZenID szulo WorkflowRuntimeID
 * @param pWorkflowDesc adat forras 
 * @param pWorkflowData workflow leiro 
 */    
    public void newInstance(String pZenID, String pParentZenID, String pWorkflowDesc, Hashtable pWorkflowData)
    {
        try 
        {
            String fName=PropertyLoader.getInstance().getProperty("prefix.dir")+"/zen/"+pZenID;
            File f=new File(fName+".data");
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(fName+".data"));
            out.write(pWorkflowDesc);
            out.close();
            f=new File(fName+".com");
            f.createNewFile();
            out = new BufferedWriter(new FileWriter(fName+".com"));
            out.write("<workflow>\n");
            out.write("\t<parent id=\""+pParentZenID+"\">\n");
            Enumeration enm=pWorkflowData.keys();
            while(enm.hasMoreElements())
            {
                String key=""+enm.nextElement();
                out.write("\t<data key=\""+key+"\" value=\""+pWorkflowData.get(key)+"\" />\n");
            }
            out.write("</workflow>\n");
            out.close();
        } 
        catch (IOException e) {}                
    
    }
    
/**
 * Perzistencia peldany torlese
 * @param pZenID WorkflowRuntimeID
 */    
    public void deleteInstance(String pZenID)
    {
        String fName=PropertyLoader.getInstance().getProperty("prefix.dir")+"/zen/"+pZenID;
        File f=new File(fName+".data");
        f.delete();
        f=new File(fName+".com");
        f.delete();
    }
    
/**
 * Meglevo peldanyok megnyitasa
 */    
    public void openAll(){}
    
    
}
