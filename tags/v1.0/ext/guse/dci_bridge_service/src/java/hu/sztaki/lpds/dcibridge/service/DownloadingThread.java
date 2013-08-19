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
 * Thread handling Download
 */

package hu.sztaki.lpds.dcibridge.service;

import dci.data.Item.Forward;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import hu.sztaki.lpds.dcibridge.util.io.HttpHandler;
import hu.sztaki.lpds.metabroker.client.MBServiceClient;
import hu.sztaki.lpds.metabroker.client.ResourceBean;
import hu.sztaki.lpds.submitter.grids.Middleware;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.namespace.QName;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 * @author krisztian karoczkai
 */
public class DownloadingThread extends Thread{
    private BlockingQueue<Job> jobs=new LinkedBlockingQueue<Job>();
    MBServiceClient metaBrokerClient;

    public DownloadingThread() {
        setName("guse/dci-bridge:input file downloadThread("+System.currentTimeMillis()+")");

        try{createMBClient();}
        catch(Exception e){e.printStackTrace();}
    }

/**
 * Creating a meta broker client 
 * @throws java.lang.Exception if the service can not be reached
 */
    public void createMBClient() throws Exception{
        if(Conf.getS().getMetabroker()!=null)
        if(!"".equals(Conf.getS().getMetabroker()))
            metaBrokerClient=new MBServiceClient(new URL(Conf.getS().getMetabroker()));
    }

/**
 * Add a new job to the pool of jobs waiting for download
 * @param pValue
 * @throws java.lang.InterruptedException
 */
    public void addJob(Job pValue) throws InterruptedException{jobs.put(pValue);}

/**
 * @see Thread#run()
 */
    @Override
    public void run() {
        Job tmp;
        while(true){
            try{
                tmp=jobs.take();
                execute(tmp);
            }
            catch(InterruptedException e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
        }
    }

/**
 * Creating the working directory, downloading the needed files, saving the JSDL 
 * @param pValue identifier of the job
 */
    private void execute(Job pValue){

        if(Base.getI().isDestroyedJob(pValue.getId())){
            Base.getI().removeJob(pValue.getId());
            return;
        }
		if(pValue.getFlag()==Middleware.ABORT) return;
		
        Base.initLogg(pValue.getId(), "logg.job.createworkdir");
        String base=Base.getI().getJobDirectory(pValue.getId());
        HttpHandler http=new HttpHandler();
        File job=new File(base+"outputs");
        job.mkdirs();
        job=new File(base+"outputs/guse.jsdl");
        try{
            job.createNewFile();
            FileWriter fw=new FileWriter(job);
            String xml=XMLHandler.getJSDLXML(pValue.getJSDL());
            fw.write(xml);
            fw.flush();
            fw.close();

            job=new File(base+"guse.logg");
            job.createNewFile();
            Base.endJobLogg(pValue, LB.INFO,"");

            Base.initLogg(pValue.getId(), "logg.job.parsejsdl");
            XMLHandler.crateExtensionTaginJSDL(pValue);
            Base.endJobLogg(pValue, LB.INFO,"");
            pValue.setResource("DCI-BRIDGE");
            pValue.setPubStatus(ActivityStateEnumeration.PENDING);



            List<DataStagingType> inputs=InputHandler.getInputs(pValue);
            for(DataStagingType t:inputs){
                Base.initLogg(pValue.getId(), "logg.job.downloadinput");
                if(t.getSource().getURI().startsWith("http"))
                    http.read(t.getSource().getURI(), base+t.getFileName());
                Base.endJobLogg(pValue, LB.INFO,"");
            }

            try{
                Base.initLogg(pValue.getId(), "logg.job.choosemiddleware");
                SDLType mbsdl=XMLHandler.getData(pValue.getJSDL().getAny(), SDLType.class);

                if(!XMLHandler.isMetaBrokerJob(mbsdl)) { 
                    ArrayList<String> tmp=new ArrayList<String>();
                    tmp.add(pValue.getId());
                    tmp.add(mbsdl.getConstraints().getMiddleware().get(0).getDCIName().value());
                    tmp.add(mbsdl.getConstraints().getMiddleware().get(0).getManagedResource());
                    try{tmp.add(pValue.getJSDL().getJobDescription().getResources().getCandidateHosts().getHostName().get(0));}
                    catch(Exception e){/*resource not set*/}
                    ResourceBean rb=fromBrokerResourceList(tmp);
                    pValue.setConfiguredResource(rb);
                }
                else if(metaBrokerClient!=null){
                    ResourceBean rb=fromBrokerResourceList(metaBrokerClient.getOptimalResource(pValue.getId()));
                    rb.setMiddleware(rb.getMiddleware().toLowerCase());
                    rb.setVo(rb.getVo().toLowerCase());
                    pValue.setConfiguredResource(rb);
                    String vo=rb.getVo();
                }
                else{/*@TODO ........... The meta broker can not be reached*/}
                Base.endJobLogg(pValue, LB.INFO,pValue.getConfiguredResource().getMiddleware());

                Base.initLogg(pValue.getId(), "logg.job.getproxy");
                ProxyClient.getProxy(pValue);
                Base.endJobLogg(pValue, LB.INFO,"");


                if(Base.getI().isDestroyedJob(pValue.getId())) {
                    Base.getI().removeJob(pValue.getId());
                    return;
                }
                Base.writeJobLogg(pValue.getId(),LB.INFO, "plugin:"+pValue.getConfiguredResource().getMiddleware());
                String selectedMiddleware=pValue.getConfiguredResource().getMiddleware();
                String selectedVO=pValue.getConfiguredResource().getVo();
                if(selectedMiddleware.equals(Base.MIDDLEWARE_SERVICE)) selectedVO="axis";
                if(selectedMiddleware.equals(Base.MIDDLEWARE_GAE)) selectedVO="google";
                if (selectedMiddleware.equals(Base.MIDDLEWARE_EDGI)) {
                    selectedVO = pValue.getJSDL().getJobDescription().getResources().getOtherAttributes().get(QName.valueOf("ar"));
                }
                Forward selectinBridge=Conf.getItem(selectedMiddleware, selectedVO).getForward();
                if("true".equals(selectinBridge.getUsethis()))
                    Base.getI().getMiddleware(pValue.getConfiguredResource().getMiddleware()).addJob(pValue);
                else{
                    Base.initLogg(pValue.getId(), "logg.job.forward");
                    String wsdl=selectinBridge.getWsdl().get(0);
//                    System.out.println("FORWARD:"+wsdl);
                    URL wsdlURL=new URL(wsdl);
                    pValue.setForwardURL(wsdlURL);
                    DCIBridgeClient client=new DCIBridgeClient();
                    pValue.setForwardID(client.call(pValue));
                    Base.endJobLogg(pValue, LB.INFO,wsdl);
                }

            }
            catch(Exception e){ 
                Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));
                pValue.setResource("middleware not supported");
                pValue.setPubStatus(ActivityStateEnumeration.FAILED);
            }
            try {
                http.close();
            } catch (Exception e) {
                //System.out.println("for debug: no input ports-?");
                //e.printStackTrace();
            }            
        }
        catch(Exception e){
                Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));
                pValue.setResource("input data error");
                pValue.setPubStatus(ActivityStateEnumeration.FAILED);
        }
    }

    public static ResourceBean fromBrokerResourceList(List<String> value) throws Exception{
        if(value.get(0).toLowerCase().equals("error")) throw new Exception("BROKER ERROR");
        ResourceBean res=new ResourceBean();

        res.setMiddleware(value.get(1));//.toLowerCase());
        res.setVo(value.get(2));//.toLowerCase());

        if (value.size() > 3) {
            if (value.get(3).contains("/")) {
                String[] tmp = value.get(3).split("/");
                res.setResource(tmp[0]);
                res.setJobmanager(tmp[1]);
            } else {
                res.setResource(value.get(3));
            }
        }
        return res;
    }
}
