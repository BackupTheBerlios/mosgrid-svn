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
 * Status callBack
 */

package hu.sztaki.lpds.submitter.status;

import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.wfi.net.wsaxis13.zen.JobStatusServiceService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStatusType;

/**
 * @author krisztian karoczkai
 */
public class Client {

    private JobStatusServiceService client;

    public Client(String pWSDL) throws MalformedURLException {
        QName ns=new QName("http://zen.wsaxis13.net.wfi.lpds.sztaki.hu/", "JobStatusServiceService");
        client=new JobStatusServiceService(new URL(pWSDL), ns);
    }
    
    public void sendStatus(Job pJob) throws Exception{
        ActivityStatusType status=new ActivityStatusType();
        status.setState(pJob.getPubStatus());
        client.getJobStatusServicePort().sendStatus(pJob.getId(), status,pJob.getResource());
    }

}
