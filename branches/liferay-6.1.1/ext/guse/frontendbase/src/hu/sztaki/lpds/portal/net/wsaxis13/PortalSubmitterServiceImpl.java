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
 * PortalSubmitterServiceImpl.java
 */

package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.portal.inf.PortalSubmitterService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Vector;

/**
 * Handles the request from the 
 * submitter on the portal side.
 *
 * @author krisztian
 */
public class PortalSubmitterServiceImpl implements PortalSubmitterService
{
    
    /**
     * @throws Exception in case of an error (file system error)
     * @see PortalSubmitterService#getProxy(java.lang.String, java.lang.String)
     */
    public String getProxy(String pUser, String pGrid)throws Exception
    {
        String res="";
        File f=new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"/users/"+pUser+"/x509up."+pGrid);
        if(f.exists())
        {
            FileInputStream fr=new FileInputStream(f);
            byte[] tmp=new byte[512];
            int i=0;
            while((i=fr.read(tmp))>(-1))res=res.concat(new String(tmp,0,i));
        }
//        System.out.println(f.getAbsolutePath());
        return res;   
    }
    
    /**
     * @see PortalSubmitterService#getActiveProxys(java.lang.String)
     */
    public Vector getActiveProxys(String pUser)
    {
        Vector res=new Vector();
        File dir = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"/users/"+pUser);
    
        String[] children = dir.list();
        if (children != null) 
        {
            for (int i=0; i<children.length; i++) 
            {
                String filename = children[i];
                if(filename.startsWith("x509up."))
                {
                    res.add(filename.substring(7));
                }
            }
        }
        return res;
    }
    
}
