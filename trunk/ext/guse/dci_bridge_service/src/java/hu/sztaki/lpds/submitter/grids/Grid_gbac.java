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

package hu.sztaki.lpds.submitter.grids;

import dci.data.Item;
import dci.data.Item.Gbac;
import eu.edges_grid.wsdl._3gbridge.GridAlgList;
import eu.edges_grid.wsdl._3gbridge.LogicalFile;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.service.LB;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import hu.sztaki.lpds.submitter.grids.boinc.BridgeHandler;
import java.net.URL;
import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;

/**
 * @author lpds
 */
public class Grid_gbac extends Grid_boinc{

    public Grid_gbac() {
        THIS_MIDDLEWARE=Base.MIDDLEWARE_GBAC;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(gbac) - "+threadID);
    }

    @Override
    public  void setConfiguration() throws Exception{
        Base.writeLogg(THIS_MIDDLEWARE,new LB(THIS_MIDDLEWARE+"plugon configuring"));
        for(Item it:Conf.getMiddleware(THIS_MIDDLEWARE).getItem()){
            Item grp=Conf.getItem(THIS_MIDDLEWARE,it.getName());
            Item.Gbac t=grp.getGbac();
            if(clients.get(grp.getName())==null){
                Base.writeLogg(THIS_MIDDLEWARE,new LB("new "+THIS_MIDDLEWARE+" grid:"+grp.getName()));
                BridgeHandler tmp=new BridgeHandler();
                URL wsdl=new URL(t.getWsdl());
                tmp.setClient(create3GBridgeClient(wsdl));
                clients.put(grp.getName(), tmp);
                Base.writeLogg(THIS_MIDDLEWARE,new LB("call service:"+t.getWsdl()));
                GridAlgList boincJobs=tmp.getClient().getG3BridgeSubmitter().getGridAlgs(t.getId());
                Item.Gbac.Job job;
                for(String jobName:boincJobs.getGridalgs()){
                    Base.writeLogg(THIS_MIDDLEWARE,new LB(THIS_MIDDLEWARE+"grid:"+grp.getName()+"/"+jobName));
                }
                Base.writeLogg(THIS_MIDDLEWARE,new LB("end of call service:"));
            }
            else{/*refresh jobs*/
                Base.writeLogg(THIS_MIDDLEWARE,new LB("refresh "+THIS_MIDDLEWARE+" grid:"+grp.getName()));
                BridgeHandler tmp=clients.get(grp.getName());
                try{
                    URL wsdl=new URL(t.getWsdl());
                    tmp.setClient(create3GBridgeClient(wsdl));
                    GridAlgList boincJobs=tmp.getClient().getG3BridgeSubmitter().getGridAlgs(t.getId());
                    for(String jobName:boincJobs.getGridalgs()){
                        Base.writeLogg(THIS_MIDDLEWARE,new LB("Boinc grid:"+grp.getName()+"/"+jobName));
                    }
                    Base.writeLogg(THIS_MIDDLEWARE,new LB("end of call service:"));
                }
                catch(Exception e){Base.writeLogg(THIS_MIDDLEWARE,new LB(e));}
            }
            System.gc();
        }
        

    }

 /**
 * Creating job instance for submission in 3GBridge
 * @param pClient Bridge Client instance
 */
    protected eu.edges_grid.wsdl._3gbridge.Job create3GBridgeJob(Job pJC){
//create job
        Gbac item=Conf.getItem(Base.MIDDLEWARE_GBAC, pJC.getConfiguredResource().getVo()).getGbac();
        eu.edges_grid.wsdl._3gbridge.Job job=new eu.edges_grid.wsdl._3gbridge.Job();
        job.setAlg("gbac");
        job.setGrid(item.getId());

        POSIXApplicationType pType=XMLHandler.getData(pJC.getJSDL().getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String params="";
        for(String s:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+s);

        job.setArgs(params);
        job.setTag("DCI-Bridge:"+pJC.getId());
//create inputs
        List<DataStagingType> ports=InputHandler.getInputs(pJC);
        LogicalFile input;
        for(DataStagingType jsdlPort:ports){
            input=new LogicalFile();
            input.setLogicalName(jsdlPort.getFileName());
            input.setURL(jsdlPort.getSource().getURI());
            job.getInputs().add(input);
        }
        input=new LogicalFile();
        input.setLogicalName("gbac_job.xml");
        input.setURL(item.getRundescurl());
        job.getInputs().add(input);

//create outputs
        ports=OutputHandler.getOutputs(pJC.getJSDL());
        for(DataStagingType jsdlPort:ports){
            if(jsdlPort.getName()!=null)
                job.getOutputs().add(jsdlPort.getFileName());
        }

        return job;
    }
    
}
