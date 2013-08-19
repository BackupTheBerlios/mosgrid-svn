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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.wfs.service.angie;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author krisztian
 */
public class LocalLogg
{
    
    public static void writeServiceLogg(String pValue)
    {
        if (PropertyLoader.getInstance().getProperty("guse.wfs.system.servicelogfile") != null) {
            try
            {
                File f=new File(PropertyLoader.getInstance().getProperty("prefix.dir") + PropertyLoader.getInstance().getProperty("guse.wfs.system.servicelogfile"));
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                FileWriter fw;
                if (f.exists()) {
                    fw=new FileWriter(f,true);
                } else {
                    f.createNewFile();
                    fw=new FileWriter(f);
                }
                long ts=System.currentTimeMillis();
                long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                fw.write("<"+ pValue +" memory=\""+memory+"\" time=\""+(new Date(ts))+"\" utc=\""+ts+"/>\n");
                fw.flush();
                fw.close();
            }
            catch(IOException e){e.printStackTrace();}
        }
    }

}
