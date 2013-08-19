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
/**
 * Workflow leiro plugin altal megvalositando metoduseok deffinicioja
 */

package hu.sztaki.lpds.wfs.service.angie.plugins.wfi.inf;

import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.util.Vector;

/**
 * @author krisztian
 */
public interface WorkflowDescriptionGenerator 
{
    
    
/**
 * WFI szamara workflow leiro szolgaltatasa
 * @param pData a Workflow amirol a leirot kell generalni
 * @return a leiro ami a wfi-nek lesz elkuldve
 */    
       public String getWFIWorkflowDescription(ComDataBean pData);

/**
 * WFI szamara workflow rescue adat leiro szolgaltatasa
 * @param pData a Workflow amirol a leirot kell generalni
 * @param String index honnan kezdje a listazast
 * @return a leiro ami a wfi-nek lesz elkuldve
 */
       public String getWFIWorkflowRescueDescription(ComDataBean pData, String index);

/**
 * Storage szamara workflow leiro szolgaltatasa
 * @param pData a Workflow amirol a leirot kell generalni
 * @return a leiro ami a wfi-nek lesz elkuldve
 */    
       public String getStorageWorkflowDescription(ComDataBean pData);

/**
 * Workflow beallitas ellenorzes es mentes
 * @param workflowIDStr workflow IDString
 * @param value workflow, user adatok
 * @param pData Workflow job leirok
 */       
        public void workflowConfigDataErrorCheck(String workflowIDStr, ComDataBean value, Vector pData);

}
