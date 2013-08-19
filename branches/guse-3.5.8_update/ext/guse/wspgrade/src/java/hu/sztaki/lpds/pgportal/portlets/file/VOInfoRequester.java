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
package hu.sztaki.lpds.pgportal.portlets.file;

import dci.data.Item.Glite;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import java.util.HashMap;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import dci.data.Middleware;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author akos
 */

public class VOInfoRequester {
    
    private List<Middleware> resource;
    private ResourceConfigurationFace client;
    private HashMap infos=new HashMap();
    public  VOInfoRequester(){
        try{
            
           client = (ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
           resource=client.get();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    public List<String> listVOs(){
        try {
            return ConfigHandler.getGroups(resource, "glite");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(VOInfoRequester.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
/*@ToDo vo-property-k*/
    public Glite getInfos(String vo){
        return ConfigHandler.getGliteVoConfig(resource,vo);

        }

}
