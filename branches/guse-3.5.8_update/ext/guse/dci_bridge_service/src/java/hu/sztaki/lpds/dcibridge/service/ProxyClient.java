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
 * Query of proxy certificate needed for the submission of the given job
 */

package hu.sztaki.lpds.dcibridge.service;

import dci.extension.ExtensionType;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import hu.sztaki.lpds.pgportal.services.credential.CredentialProviderService;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import javax.xml.namespace.QName;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.globus.util.Util;

/**
 * @author krisztian karoczkai
 */
public class ProxyClient {
    

    public static void getProxy(Job pJob) throws Exception{
        byte[] res;
        String vo=pJob.getConfiguredResource().getVo();
        String middleware=pJob.getConfiguredResource().getMiddleware();

        if(Conf.getMiddleware(middleware).getCertificate().size()==0) return;
        
        ExtensionType callBack=XMLHandler.getData(pJob.getJSDL().getAny(), ExtensionType.class);
        if(callBack==null) return;

        POSIXApplicationType app=XMLHandler.getData(pJob.getJSDL().getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        if(app==null) return;

        QName qn= new QName("http://credential.services.pgportal.lpds.sztaki.hu/", "CredentialProviderService");
        CredentialProviderService client=new CredentialProviderService(new URL(callBack.getProxyservice()), qn);
        res=client.getCredentialProviderPort().get(app.getGroupName().getValue(), app.getUserName().getValue(), middleware, vo);

        File f=new File(Base.getI().getJobDirectory(pJob.getId())+"x509up");
        f.createNewFile();
        FileOutputStream fos=new FileOutputStream(f);
        fos.write(res);
        fos.flush();
        fos.close();
        f.setReadable(true, true);
        f.setWritable(true, true);
        Util.setFilePermissions(Base.getI().getJobDirectory(pJob.getId())+"x509up", 600);
    }

}
