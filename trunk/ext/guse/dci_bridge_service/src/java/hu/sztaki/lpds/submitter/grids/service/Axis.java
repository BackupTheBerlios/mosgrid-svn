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
 * Axis based webservice call
 */

package hu.sztaki.lpds.submitter.grids.service;

import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import java.io.FileInputStream;

import java.util.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.Constants;
import javax.xml.rpc.ParameterMode;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;


/**
 * @author krisztian karoczkai
 */
public class Axis implements ServiceCall{

    @Override
    public Object call(Job pJob) throws Exception{
        String path=Base.getI().getJobDirectory(pJob.getId());
        Service service;
        Call call;
        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(pJob.getConfiguredResource().getVo());
        call.setOperationName(pJob.getJSDL().getJobDescription().getJobIdentification().getJobName());

        List<DataStagingType> val=InputHandler.getInputs(pJob);
        Vector valc=new Vector();
        for(int i=0;i<val.size();i++){
            FileInputStream in = new FileInputStream(path+val.get(i).getFileName());
            String param=new String(""),str="";
            byte[] btmp=new byte[512];
            int itmp=0;
            while ((itmp=in.read(btmp))>(-1))
                param=param.concat(new String(btmp,0,itmp));
            in.close();
            call.addParameter( "arg"+i, Constants.XSD_STRING, ParameterMode.IN );
            valc.add(param);
//            System.out.println("param:"+param);
        }
        call.setReturnType(Constants.XSD_STRING);
        Object[] vall=new Object[valc.size()];
        for(int ii=0;ii<valc.size();ii++) vall[ii]=valc.get(ii);
//        System.out.println(vall);
         return call.invoke(vall);
    }

}
