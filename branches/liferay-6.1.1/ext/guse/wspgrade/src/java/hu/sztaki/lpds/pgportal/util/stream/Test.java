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

package hu.sztaki.lpds.pgportal.util.stream;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.portal.util.stream.HttpClient;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author krisztian karoczkai
 */
public class Test {

    public static void main(String[] args)
    {
        File f=new File("/home/krisztian/demowf/Jul15___cross_2_project_all.zip");
        Hashtable h=new Hashtable();
        h.put("senderObj","ZipFileSender");
        h.put("portalURL","http://localhost:8080/portal30");
        h.put("wfsID","http://localhost:8080/wfs");
        h.put("userID","alma2");

        h.put("newGrafName","");
        h.put("newAbstName","");
        h.put("newRealName","");



        try
        {
            Hashtable hsh = new Hashtable();
            hsh.put("url", "http://localhost:8080/storage");
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID("/receiver");
            ps.fileUpload(f,"fileName", h);
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
}
